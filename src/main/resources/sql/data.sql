-- Insert Users
INSERT INTO users (id, email, password, full_name, phone, avatar, bio, created_date, is_locked, is_active)
VALUES
    (UUID_TO_BIN('11111111-1111-1111-1111-111111111111'), 'alice@example.com', 'pass123', 'Alice Johnson', '1234567890', NULL, 'Loves science and coding.', NOW(), false, true),
    (UUID_TO_BIN('22222222-2222-2222-2222-222222222222'), 'bob@example.com', 'pass123', 'Bob Smith', '0987654321', NULL, 'Math and physics mentor.', NOW(), false, true),
    (UUID_TO_BIN('33333333-3333-3333-3333-333333333333'), 'carol@example.com', 'pass123', 'Carol White', '1122334455', NULL, 'Admin at EduTrack.', NOW(), false, true),
    (UUID_TO_BIN('44444444-4444-4444-4444-444444444444'), 'dave@example.com', 'pass123', 'Dave Brown', '6677889900', NULL, 'Helps with platform logistics.', NOW(), false, true),
    (UUID_TO_BIN('55555555-5555-5555-5555-555555555555'), 'erin@example.com', 'pass123', 'Erin Black', '1010101010', NULL, 'Enjoys psychology and mentoring.', NOW(), false, true),
    (UUID_TO_BIN('66666666-6666-6666-6666-666666666666'), 'frank@example.com', 'pass123', 'Frank Miller', '2020202020', NULL, 'Computer Science graduate.', NOW(), false, true),
    (UUID_TO_BIN('77777777-7777-7777-7777-777777777777'), 'grace@example.com', 'pass123', 'Grace Lee', '3030303030', NULL, 'Platform support manager.', NOW(), false, true),
    (UUID_TO_BIN('88888888-8888-8888-8888-888888888888'), 'harry@example.com', 'pass123', 'Harry Wilson', '4040404040', NULL, 'Biology and Chemistry focus.', NOW(), false, true);

-- Insert Mentees
INSERT INTO mentees (user_id, total_sessions, interests)
VALUES
    (UUID_TO_BIN('11111111-1111-1111-1111-111111111111'), 5, 'Biology, Chemistry'),
    (UUID_TO_BIN('66666666-6666-6666-6666-666666666666'), 3, 'Programming, Web Development'),
    (UUID_TO_BIN('88888888-8888-8888-8888-888888888888'), 2, 'Environmental Science, Data Analysis');

-- Insert Mentors
INSERT INTO mentors (user_id, is_available, total_sessions, expertise, rating)
VALUES
    (UUID_TO_BIN('22222222-2222-2222-2222-222222222222'), true, 10, 'Mathematics, Physics', 4.5),
    (UUID_TO_BIN('55555555-5555-5555-5555-555555555555'), true, 7, 'Psychology, Mentoring', 4.8);

-- Insert Staff
INSERT INTO staffs (user_id, role)
VALUES
    (UUID_TO_BIN('33333333-3333-3333-3333-333333333333'), 'Admin'),
    (UUID_TO_BIN('44444444-4444-4444-4444-444444444444'), 'Manager'),
    (UUID_TO_BIN('77777777-7777-7777-7777-777777777777'), 'Manager');


-- COURSES
INSERT INTO courses (id, name, description, is_approved, is_open, created_date)
VALUES (UUID_TO_BIN('d1111111-dddd-dddd-dddd-dddddddddddd'), 'Introduction to Java',
        'This course covers the basics of Java programming including syntax, OOP principles, and standard libraries.',
        1, 1, NOW()),

       (UUID_TO_BIN('d2222222-dddd-dddd-dddd-dddddddddddd'), 'Web Development with React',
        'A hands-on course teaching modern frontend development using React, JSX, hooks, and component-based architecture.',
        1, 1, NOW()),

       (UUID_TO_BIN('d3333333-dddd-dddd-dddd-dddddddddddd'), 'Data Structures and Algorithms',
        'Explore common data structures and algorithms with a focus on problem solving and coding interviews.',
        1, 0, NOW()),

       (UUID_TO_BIN('d4444444-dddd-dddd-dddd-dddddddddddd'), 'DevOps Fundamentals',
        'Learn CI/CD, containerization with Docker, orchestration with Kubernetes, and infrastructure as code.',
        0, 0, NOW()),

       (UUID_TO_BIN('d5555555-dddd-dddd-dddd-dddddddddddd'), 'Cloud Computing with AWS',
        'An introductory course to AWS services including EC2, S3, Lambda, and IAM roles.',
        1, 1, NOW()),

       (UUID_TO_BIN('d6666666-dddd-dddd-dddd-dddddddddddd'), 'Database Systems',
        'Covers relational database concepts, normalization, SQL queries, and JDBC integration with Java.',
        1, 1, NOW()),

       (UUID_TO_BIN('d7777777-dddd-dddd-dddd-dddddddddddd'), 'Machine Learning Basics',
        'A gentle introduction to machine learning concepts including supervised learning, regression, and classification.',
        0, 1, NOW()),

       (UUID_TO_BIN('d8888888-dddd-dddd-dddd-dddddddddddd'), 'Cybersecurity Essentials',
        'Understand the basics of securing systems and data, covering topics like encryption, firewalls, and ethical hacking.',
        1, 0, NOW());


-- CV for Mentor: Bob Smith (UUID: 22222222-2222-2222-2222-222222222222)
INSERT INTO cv (
    user_id, summary, experience_years, skills, education, experience,
    certifications, languages, portfolio_url, status, updated_date
)
VALUES (
           UUID_TO_BIN('22222222-2222-2222-2222-222222222222'),
           'Experienced physics tutor with a passion for interactive teaching.',
           5,
           'Physics;Mathematics;Calculus;Problem Solving',
           'BSc Physics;MSc Education',
           'High School Physics Teacher;Private Tutor;Online Workshop Leader',
           'Teaching Certificate;Cambridge International Certification',
           'English;Spanish',
           'https://www.linkedin.com/in/bobsmith',
           'approved',
           NOW()
       );

-- CV for Mentor: Erin Black (UUID: 55555555-5555-5555-5555-555555555555)
INSERT INTO cv (
    user_id, summary, experience_years, skills, education, experience,
    certifications, languages, portfolio_url, status, updated_date
)
VALUES (
           UUID_TO_BIN('55555555-5555-5555-5555-555555555555'),
           'Psychology mentor skilled in cognitive development and mentoring teens.',
           4,
           'Psychology;Mentoring;Active Listening;Conflict Resolution',
           'BA Psychology;MA Child Development',
           'Youth Counselor;School Guidance;Volunteer Mentor',
           'Certified Mentor;Mental Health First Aid',
           'English;French',
           'https://www.linkedin.com/in/erinblack',
           'approved',
           NOW()
       );

-- CV for Mentee: Frank Miller (UUID: 66666666-6666-6666-6666-666666666666)
INSERT INTO cv (
    user_id, summary, experience_years, skills, education, experience,
    certifications, languages, portfolio_url, status, updated_date
)
VALUES (
           UUID_TO_BIN('66666666-6666-6666-6666-666666666666'),
           'Aspiring developer looking for mentorship in web and mobile development.',
           1,
           'HTML;CSS;JavaScript;Git',
           'High School Diploma;Online Web Dev Bootcamp',
           'Personal Projects;Hackathon Participant',
           'Responsive Web Design Certification',
           'English',
           'https://github.com/frankmiller',
           'pending',
           NOW()
       );

-- CV for Mentee: Harry Wilson (UUID: 88888888-8888-8888-8888-888888888888)
INSERT INTO cv (
    user_id, summary, experience_years, skills, education, experience,
    certifications, languages, portfolio_url, status, updated_date
)
VALUES (
           UUID_TO_BIN('88888888-8888-8888-8888-888888888888'),
           'Motivated student passionate about environmental science and data.',
           2,
           'Data Analysis;Python;Excel;Statistics',
           'BSc Environmental Science',
           'Field Data Collector;Research Intern',
           'Data Science Certificate',
           'English;German',
           NULL,
           'pending',
           NOW()
       );
