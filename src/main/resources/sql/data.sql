-- Mentor: Bob Smith
INSERT INTO users (id, full_name, email, password, phone, is_active, is_locked, created_date, bio, gender,
                   birth_date)
VALUES (UUID_TO_BIN('22222222-2222-2222-2222-222222222222'),
        'Bob Smith',
        'bob.smith@example.com',
        'securepassword123',
        '+1234567890',
        1, 0, NOW(),
        'Physics tutor who loves to teach complex topics in simple ways.',
        'male',
        '1985-04-10');

-- Mentor: Erin Black
INSERT INTO users (id, full_name, email, password, phone, is_active, is_locked, created_date, bio, gender,
                   birth_date)
VALUES (UUID_TO_BIN('55555555-5555-5555-5555-555555555555'),
        'Erin Black',
        'erin.black@example.com',
        'anothersecurepass',
        '+1234567891',
        1, 0, NOW(),
        'Passionate psychology mentor helping students find purpose.',
        'female',
        '1990-08-22');

-- Mentee: Frank Miller
INSERT INTO users (id, full_name, email, password, phone, is_active, is_locked, created_date, bio, gender,
                   birth_date)
VALUES (UUID_TO_BIN('66666666-6666-6666-6666-666666666666'),
        'Frank Miller',
        'frank.miller@example.com',
        'frankpass2024',
        '+1234567892',
        1, 0, NOW(),
        'Future software engineer, eager to learn.',
        'male',
        '2003-11-12');

-- Mentee: Harry Wilson
INSERT INTO users (id, full_name, email, password, phone, is_active, is_locked, created_date, bio, gender,
                   birth_date)
VALUES (UUID_TO_BIN('88888888-8888-8888-8888-888888888888'),
        'Harry Wilson',
        'harry.wilson@example.com',
        'harrysafe123',
        '+1234567893',
        1, 0, NOW(),
        'Environmental science enthusiast exploring data science.',
        'male',
        '2001-06-03');

-- User: Alice Johnson (UUID 1111...)
INSERT INTO users (id, full_name, email, password, phone, is_active, is_locked, created_date, bio, gender, birth_date)
VALUES (UUID_TO_BIN('11111111-1111-1111-1111-111111111111'),
        'Alice Johnson',
        'alice.johnson@example.com',
        'alicepass2024',
        '+1234567894',
        1, 0, NOW(),
        'Computer science student and tech enthusiast.',
        'female',
        '2002-02-15');

-- User: Carol Davis (UUID 3333...)
INSERT INTO users (id, full_name, email, password, phone, is_active, is_locked, created_date, bio, gender, birth_date)
VALUES (UUID_TO_BIN('33333333-3333-3333-3333-333333333333'),
        'Carol Davis',
        'carol.davis@example.com',
        'carolsecure123',
        '+1234567895',
        1, 0, NOW(),
        'Aspiring data analyst with a love for numbers.',
        'female',
        '1999-09-25');


-- Insert Bob Smith into mentors table
INSERT INTO mentors (expertise, is_available, rating, total_sessions, user_id)
VALUES ('Physics;Mathematics;Calculus', 1, 4.8, 120, UUID_TO_BIN('22222222-2222-2222-2222-222222222222'));

-- Insert Erin Black into mentors table
INSERT INTO mentors (expertise, is_available, rating, total_sessions, user_id)
VALUES ('Psychology;Mentoring;Conflict Resolution', 1, 4.5, 95, UUID_TO_BIN('55555555-5555-5555-5555-555555555555'));


-- COURSES
INSERT INTO courses (id, name, description, is_open, created_date)
VALUES (UUID_TO_BIN('d1111111-dddd-dddd-dddd-dddddddddddd'), 'Introduction to Java',
        'This course covers the basics of Java programming including syntax, OOP principles, and standard libraries.',
        1, NOW()),

       (UUID_TO_BIN('d2222222-dddd-dddd-dddd-dddddddddddd'), 'Web Development with React',
        'A hands-on course teaching modern frontend development using React, JSX, hooks, and component-based architecture.',
        1, NOW()),

       (UUID_TO_BIN('d3333333-dddd-dddd-dddd-dddddddddddd'), 'Data Structures and Algorithms',
        'Explore common data structures and algorithms with a focus on problem solving and coding interviews.',
        0, NOW()),

       (UUID_TO_BIN('d4444444-dddd-dddd-dddd-dddddddddddd'), 'DevOps Fundamentals',
        'Learn CI/CD, containerization with Docker, orchestration with Kubernetes, and infrastructure as code.',
        0, NOW()),

       (UUID_TO_BIN('d5555555-dddd-dddd-dddd-dddddddddddd'), 'Cloud Computing with AWS',
        'An introductory course to AWS services including EC2, S3, Lambda, and IAM roles.',
        1, NOW()),

       (UUID_TO_BIN('d6666666-dddd-dddd-dddd-dddddddddddd'), 'Database Systems',
        'Covers relational database concepts, normalization, SQL queries, and JDBC integration with Java.',
        1, NOW()),

       (UUID_TO_BIN('d7777777-dddd-dddd-dddd-dddddddddddd'), 'Machine Learning Basics',
        'A gentle introduction to machine learning concepts including supervised learning, regression, and classification.',
        1, NOW()),

       (UUID_TO_BIN('d8888888-dddd-dddd-dddd-dddddddddddd'), 'Cybersecurity Essentials',
        'Understand the basics of securing systems and data, covering topics like encryption, firewalls, and ethical hacking.',
        0, NOW());


-- CV for Mentor: Bob Smith (UUID: 22222222-2222-2222-2222-222222222222)
INSERT INTO cv (user_id, summary, experience_years, skills, education, experience,
                certifications, languages, portfolio_url, status, created_date)
VALUES (UUID_TO_BIN('22222222-2222-2222-2222-222222222222'),
        'Experienced physics tutor with a passion for interactive teaching.',
        5,
        'Physics;Mathematics;Calculus;Problem Solving',
        'BSc Physics;MSc Education',
        'High School Physics Teacher;Private Tutor;Online Workshop Leader',
        'Teaching Certificate;Cambridge International Certification',
        'English;Spanish',
        'https://www.linkedin.com/in/bobsmith',
        'approved',
        NOW());

-- CV for Mentor: Erin Black (UUID: 55555555-5555-5555-5555-555555555555)
INSERT INTO cv (user_id, summary, experience_years, skills, education, experience,
                certifications, languages, portfolio_url, status, created_date)
VALUES (UUID_TO_BIN('55555555-5555-5555-5555-555555555555'),
        'Psychology mentor skilled in cognitive development and mentoring teens.',
        4,
        'Psychology;Mentoring;Active Listening;Conflict Resolution',
        'BA Psychology;MA Child Development',
        'Youth Counselor;School Guidance;Volunteer Mentor',
        'Certified Mentor;Mental Health First Aid',
        'English;French',
        'https://www.linkedin.com/in/erinblack',
        'approved',
        NOW());

-- CV for Mentee: Frank Miller (UUID: 66666666-6666-6666-6666-666666666666)
INSERT INTO cv (user_id, summary, experience_years, skills, education, experience,
                certifications, languages, portfolio_url, status, created_date)
VALUES (UUID_TO_BIN('66666666-6666-6666-6666-666666666666'),
        'Aspiring developer looking for mentorship in web and mobile development.',
        1,
        'HTML;CSS;JavaScript;Git',
        'High School Diploma;Online Web Dev Bootcamp',
        'Personal Projects;Hackathon Participant',
        'Responsive Web Design Certification',
        'English',
        'https://github.com/frankmiller',
        'pending',
        NOW());

-- CV for Mentee: Harry Wilson (UUID: 88888888-8888-8888-8888-888888888888)
INSERT INTO cv (user_id, summary, experience_years, skills, education, experience,
                certifications, languages, portfolio_url, status, created_date)
VALUES (UUID_TO_BIN('88888888-8888-8888-8888-888888888888'),
        'Motivated student passionate about environmental science and data.',
        2,
        'Data Analysis;Python;Excel;Statistics',
        'BSc Environmental Science',
        'Field Data Collector;Research Intern',
        'Data Science Certificate',
        'English;German',
        NULL,
        'pending',
        NOW());


-- Bob Smith bookmarks "Introduction to Java" and "Database Systems"
INSERT INTO bookmarks (created_date, course_id, user_id)
VALUES (NOW(), UUID_TO_BIN('d1111111-dddd-dddd-dddd-dddddddddddd'),
        UUID_TO_BIN('22222222-2222-2222-2222-222222222222')),
       (NOW(), UUID_TO_BIN('d6666666-dddd-dddd-dddd-dddddddddddd'),
        UUID_TO_BIN('22222222-2222-2222-2222-222222222222'));

-- Erin Black bookmarks "Machine Learning Basics" and "Cloud Computing with AWS"
INSERT INTO bookmarks (created_date, course_id, user_id)
VALUES (NOW(), UUID_TO_BIN('d7777777-dddd-dddd-dddd-dddddddddddd'),
        UUID_TO_BIN('55555555-5555-5555-5555-555555555555')),
       (NOW(), UUID_TO_BIN('d5555555-dddd-dddd-dddd-dddddddddddd'),
        UUID_TO_BIN('55555555-5555-5555-5555-555555555555'));

-- Frank Miller bookmarks "Web Development with React", "Cybersecurity Essentials", and "Introduction to Java"
INSERT INTO bookmarks (created_date, course_id, user_id)
VALUES (NOW(), UUID_TO_BIN('d2222222-dddd-dddd-dddd-dddddddddddd'),
        UUID_TO_BIN('66666666-6666-6666-6666-666666666666')),
       (NOW(), UUID_TO_BIN('d8888888-dddd-dddd-dddd-dddddddddddd'),
        UUID_TO_BIN('66666666-6666-6666-6666-666666666666')),
       (NOW(), UUID_TO_BIN('d1111111-dddd-dddd-dddd-dddddddddddd'),
        UUID_TO_BIN('66666666-6666-6666-6666-666666666666'));

-- Harry Wilson bookmarks "DevOps Fundamentals" (not approved) and "Data Structures and Algorithms"
INSERT INTO bookmarks (created_date, course_id, user_id)
VALUES (NOW(), UUID_TO_BIN('d4444444-dddd-dddd-dddd-dddddddddddd'),
        UUID_TO_BIN('88888888-8888-8888-8888-888888888888')),
       (NOW(), UUID_TO_BIN('d3333333-dddd-dddd-dddd-dddddddddddd'),
        UUID_TO_BIN('88888888-8888-8888-8888-888888888888'));


INSERT INTO tags (title, description)
VALUES ('Java', 'Covers topics related to Java programming and its ecosystem.'),
       ('Web Development', 'Topics related to building web applications and sites.'),
       ('Algorithms', 'Focus on problem solving, logic, and algorithm design.'),
       ('DevOps', 'Covers CI/CD, Docker, Kubernetes, and cloud infrastructure.'),
       ('Cloud', 'Topics related to cloud computing platforms like AWS.'),
       ('Databases', 'Covers relational databases, SQL, and data modeling.'),
       ('Machine Learning', 'Basics of supervised/unsupervised learning and AI.'),
       ('Cybersecurity', 'Focus on securing digital systems and information.');


-- "Introduction to Java" -> Java
INSERT INTO course_tags (tag_id, course_id)
VALUES (1, UUID_TO_BIN('d1111111-dddd-dddd-dddd-dddddddddddd'));

-- "Web Development with React" -> Web Development
INSERT INTO course_tags (tag_id, course_id)
VALUES (2, UUID_TO_BIN('d2222222-dddd-dddd-dddd-dddddddddddd'));

-- "Data Structures and Algorithms" -> Algorithms, Java
INSERT INTO course_tags (tag_id, course_id)
VALUES (3, UUID_TO_BIN('d3333333-dddd-dddd-dddd-dddddddddddd')),
       (1, UUID_TO_BIN('d3333333-dddd-dddd-dddd-dddddddddddd'));

-- "DevOps Fundamentals" -> DevOps, Cloud
INSERT INTO course_tags (tag_id, course_id)
VALUES (4, UUID_TO_BIN('d4444444-dddd-dddd-dddd-dddddddddddd')),
       (5, UUID_TO_BIN('d4444444-dddd-dddd-dddd-dddddddddddd'));

-- "Cloud Computing with AWS" -> Cloud, DevOps
INSERT INTO course_tags (tag_id, course_id)
VALUES (5, UUID_TO_BIN('d5555555-dddd-dddd-dddd-dddddddddddd')),
       (4, UUID_TO_BIN('d5555555-dddd-dddd-dddd-dddddddddddd'));

-- "Database Systems" -> Databases, Java
INSERT INTO course_tags (tag_id, course_id)
VALUES (6, UUID_TO_BIN('d6666666-dddd-dddd-dddd-dddddddddddd')),
       (1, UUID_TO_BIN('d6666666-dddd-dddd-dddd-dddddddddddd'));

-- "Machine Learning Basics" -> Machine Learning, Algorithms
INSERT INTO course_tags (tag_id, course_id)
VALUES (7, UUID_TO_BIN('d7777777-dddd-dddd-dddd-dddddddddddd')),
       (3, UUID_TO_BIN('d7777777-dddd-dddd-dddd-dddddddddddd'));

-- "Cybersecurity Essentials" -> Cybersecurity, Cloud
INSERT INTO course_tags (tag_id, course_id)
VALUES (8, UUID_TO_BIN('d8888888-dddd-dddd-dddd-dddddddddddd')),
       (5, UUID_TO_BIN('d8888888-dddd-dddd-dddd-dddddddddddd'));


INSERT INTO bookmarks (created_date, course_id, user_id)
VALUES ('2024-05-01 10:15:00.000000', UUID_TO_BIN('d1111111-dddd-dddd-dddd-dddddddddddd'),
        UUID_TO_BIN('11111111-1111-1111-1111-111111111111')),
       ('2024-05-03 08:30:00.000000', UUID_TO_BIN('d2222222-dddd-dddd-dddd-dddddddddddd'),
        UUID_TO_BIN('11111111-1111-1111-1111-111111111111')),
       ('2024-05-04 11:45:00.000000', UUID_TO_BIN('d3333333-dddd-dddd-dddd-dddddddddddd'),
        UUID_TO_BIN('22222222-2222-2222-2222-222222222222')),
       ('2024-05-06 15:20:00.000000', UUID_TO_BIN('d4444444-dddd-dddd-dddd-dddddddddddd'),
        UUID_TO_BIN('33333333-3333-3333-3333-333333333333')),
       ('2024-05-07 17:00:00.000000', UUID_TO_BIN('d2222222-dddd-dddd-dddd-dddddddddddd'),
        UUID_TO_BIN('33333333-3333-3333-3333-333333333333')),
       ('2024-05-08 19:40:00.000000', UUID_TO_BIN('d1111111-dddd-dddd-dddd-dddddddddddd'),
        UUID_TO_BIN('22222222-2222-2222-2222-222222222222'));

INSERT INTO course_mentor (id, applied_date, status, course_id, mentor_user_id)
VALUES
    (UUID_TO_BIN('aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaa1'), '2024-05-01 09:00:00.000000', 'PENDING',
     UUID_TO_BIN('d1111111-dddd-dddd-dddd-dddddddddddd'), UUID_TO_BIN('22222222-2222-2222-2222-222222222222')),

    (UUID_TO_BIN('aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaa2'), '2024-05-02 14:30:00.000000', 'ACCEPTED',
     UUID_TO_BIN('d2222222-dddd-dddd-dddd-dddddddddddd'), UUID_TO_BIN('22222222-2222-2222-2222-222222222222')),

    (UUID_TO_BIN('aaaaaaa3-aaaa-aaaa-aaaa-aaaaaaaaaaa3'), '2024-05-03 11:15:00.000000', 'REJECTED',
     UUID_TO_BIN('d7777777-dddd-dddd-dddd-dddddddddddd'), UUID_TO_BIN('22222222-2222-2222-2222-222222222222')),

    (UUID_TO_BIN('aaaaaaa4-aaaa-aaaa-aaaa-aaaaaaaaaaa4'), '2024-05-01 10:00:00.000000', 'PENDING',
     UUID_TO_BIN('d5555555-dddd-dddd-dddd-dddddddddddd'), UUID_TO_BIN('55555555-5555-5555-5555-555555555555')),

    (UUID_TO_BIN('aaaaaaa5-aaaa-aaaa-aaaa-aaaaaaaaaaa5'), '2024-05-05 16:00:00.000000', 'ACCEPTED',
     UUID_TO_BIN('d7777777-dddd-dddd-dddd-dddddddddddd'), UUID_TO_BIN('55555555-5555-5555-5555-555555555555'));
