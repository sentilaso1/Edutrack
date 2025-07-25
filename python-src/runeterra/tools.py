import mysql.connector
import mysql.connector.pooling
from contextlib import contextmanager
from typing import Any, List

from langchain.tools import tool
from langchain_core.messages import ToolMessage
from langchain_core.messages.tool import ToolCall
from langchain_core.tools import BaseTool

from runeterra.config import Config
from runeterra.logging import log, log_panel

connection_pool = mysql.connector.pooling.MySQLConnectionPool(
    pool_name='edutrack',
    pool_size=10,
    pool_reset_session=True,
    host=Config.MySQL.HOST,
    port=Config.MySQL.PORT,
    user=Config.MySQL.USER,
    password=Config.MySQL.PASSWORD,
    database=Config.MySQL.DATABASE
)


def get_available_tools() -> List[BaseTool]:
    return [
        execute_sql,
        recommend_mentors_by_topic,
        recommend_courses_by_topic,
        get_mentor_availability,
        get_top_rated_mentors,
        list_courses_for_mentor
    ]

def call_tool(tool_call: ToolCall) -> Any:
    tools_by_name = {tool.name: tool for tool in get_available_tools()}
    tool = tools_by_name[tool_call["name"]]
    response = tool.invoke(tool_call["args"])
    return ToolMessage(content=response, tool_call_id=tool_call["id"])


@contextmanager
def with_sql_cursor(readonly=True):
    conn = connection_pool.get_connection()
    cur = conn.cursor()

    try:
        yield cur
        if not readonly:
            conn.commit()
    except Exception:
        if not readonly:
            conn.rollback()
        raise
    finally:
        cur.close()
        conn.close()

def _execute_sql(reasoning: str, sql_query: str) -> str:
    log_panel(
        title="Execute SQL Tool",
        content=f"Query: {sql_query}\nReasoning: {reasoning}",
    )
    try:
        with with_sql_cursor() as cursor:
            cursor.execute(sql_query)
            rows = cursor.fetchall()
        return "\n".join([str(row) for row in rows])
    except Exception as e:
        log(f"[red]Error running query: {str(e)}[/red]")
        return f"Error running query: {str(e)}"
    pass

@tool(parse_docstring=True)
def execute_sql(reasoning: str, sql_query: str) -> str:
    """Executes a SQL query on the MySQL database and returns the result.
    PERFORMANCE GUIDELINES:
    - Use LIMIT clause for large result sets
    - Prefer specific columns over SELECT *
    - Use appropriate WHERE clauses with indexed columns
    - Consider using specialized tools for common queries

    Args:
        reasoning: Explanation of why this query needs to be run.
        sql_query: A complete, valid MySQL SQL query.

    Returns:
        Query result as a string, showing each row as a tuple on a new line.
    """
    return _execute_sql(reasoning, sql_query)

@tool(parse_docstring=True)
def recommend_mentors_by_topic(topic: str, limit: int = 3) -> str:
    """
    Recommends available mentors who teach courses related to a specific topic.

    Args:
        topic: Keyword like "Java" or "Cybersecurity" to match with course tags or descriptions.
        limit: Max number of mentors to return (default: 3).

    Returns:
        A list of mentor names, expertise, and average ratings.
    """
    reasoning = f"Find mentors for topic '{topic}'"
    query = f"""
        SELECT u.full_name, m.expertise, ROUND(AVG(f.rating), 1) AS avg_rating
        FROM mentors m
        JOIN users u ON m.user_id = u.id
        JOIN course_mentor cm ON m.user_id = cm.mentor_user_id
        JOIN courses c ON cm.course_id = c.id
        JOIN course_tags ct ON c.id = ct.course_id
        JOIN tags t ON ct.tag_id = t.id
        LEFT JOIN feedbacks f ON cm.id = f.course_mentor_id
        WHERE LOWER(t.title) LIKE '%{topic.lower()}%' AND m.is_available = 1
        GROUP BY m.user_id
        ORDER BY avg_rating DESC
        LIMIT {limit};
    """
    return _execute_sql(reasoning=reasoning, sql_query=query)

@tool(parse_docstring=True)
def recommend_courses_by_topic(topic: str, limit: int = 3) -> str:
    """
    Recommends open courses based on a topic.

    Args:
        topic: Topic or keyword to search in tags (e.g., "AI", "Java").
        limit: Max number of courses to return.

    Returns:
        A list of course names and descriptions.
    """
    reasoning = f"Find open courses about '{topic}'"
    query = f"""
        SELECT c.name, c.description
        FROM courses c
        JOIN course_tags ct ON c.id = ct.course_id
        JOIN tags t ON ct.tag_id = t.id
        WHERE LOWER(t.title) LIKE '%{topic.lower()}%' AND c.is_open = 1
        LIMIT {limit};
    """
    return _execute_sql(reasoning=reasoning, sql_query=query)

@tool(parse_docstring=True)
def get_mentor_availability(day: str) -> str:
    """
    Lists mentors available on a specific day.

    Args:
        day: Day of the week (e.g., 'MONDAY', 'TUESDAY').

    Returns:
        A list of mentor names available on that day.
    """
    reasoning = f"Find mentors available on {day.upper()}"
    query = f"""
        SELECT DISTINCT u.full_name, m.expertise
        FROM mentors m
        JOIN users u ON m.user_id = u.id
        JOIN mentor_available_time mat ON mat.mentor_id = m.user_id
        WHERE mat.day = '{day.upper()}' AND mat.status = 'APPROVED' AND m.is_available = 1;
    """
    return _execute_sql(reasoning=reasoning, sql_query=query)

@tool(parse_docstring=True)
def get_top_rated_mentors(limit: int = 5) -> str:
    """
    Retrieves top-rated mentors based on feedback scores.

    Args:
        limit: Max number of mentors to return.

    Returns:
        Mentor names, expertise, and average ratings.
    """
    reasoning = "Find top-rated mentors by average rating"
    query = f"""
        SELECT u.full_name, m.expertise, ROUND(AVG(f.rating), 1) AS avg_rating
        FROM mentors m
        JOIN users u ON m.user_id = u.id
        JOIN course_mentor cm ON cm.mentor_user_id = m.user_id
        JOIN feedbacks f ON f.course_mentor_id = cm.id
        WHERE m.is_available = 1
        GROUP BY m.user_id
        ORDER BY avg_rating DESC
        LIMIT {limit};
    """
    return _execute_sql(reasoning=reasoning, sql_query=query)

@tool(parse_docstring=True)
def list_courses_for_mentor(mentor_name: str) -> str:
    """
    Lists courses taught by a specific mentor.

    Args:
        mentor_name: Full name of the mentor.

    Returns:
        A list of course names and their descriptions.
    """
    reasoning = f"Find courses for mentor '{mentor_name}'"
    query = f"""
        SELECT c.name, c.description
        FROM users u
        JOIN mentors m ON u.id = m.user_id
        JOIN course_mentor cm ON cm.mentor_user_id = m.user_id
        JOIN courses c ON c.id = cm.course_id
        WHERE u.full_name = '{mentor_name}' AND cm.status = 'ACCEPTED';
    """
    return _execute_sql(reasoning=reasoning, sql_query=query)

@tool(parse_docstring=True)
def get_mentor_tags(mentor_name: str) -> str:
    """
    Lists all topic tags associated with the mentor's courses.

    Args:
        mentor_name: Full name of the mentor (e.g., "Bob Smith").

    Returns:
        A list of tags/topics the mentor is associated with.
    """
    query = f"""
        SELECT DISTINCT t.title
        FROM users u
        JOIN mentors m ON u.id = m.user_id
        JOIN course_mentor cm ON cm.mentor_user_id = m.user_id
        JOIN courses c ON c.id = cm.course_id
        JOIN course_tags ct ON c.id = ct.course_id
        JOIN tags t ON ct.tag_id = t.id
        WHERE u.full_name = '{mentor_name}';
    """
    return _execute_sql(reasoning=f"Verify topics associated with {mentor_name}", sql_query=query)

@tool(parse_docstring=True)
def list_courses_and_tags_for_mentor(mentor_name: str) -> str:
    """
    Shows the mentor's courses and their associated tags.

    Args:
        mentor_name: Full name of the mentor.

    Returns:
        Each row contains: course name and list of tags.
    """
    query = f"""
        SELECT c.name, GROUP_CONCAT(t.title) as tags
        FROM users u
        JOIN mentors m ON u.id = m.user_id
        JOIN course_mentor cm ON cm.mentor_user_id = m.user_id
        JOIN courses c ON c.id = cm.course_id
        JOIN course_tags ct ON ct.course_id = c.id
        JOIN tags t ON ct.tag_id = t.id
        WHERE u.full_name = '{mentor_name}'
        GROUP BY c.id;
    """
    return _execute_sql(reasoning=f"Check tags for courses taught by {mentor_name}", sql_query=query)
