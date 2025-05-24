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
INSERT INTO cv (user_id, summary, experience_years, skills, education, experience,
                certifications, languages, portfolio_url, status, updated_date)
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
                certifications, languages, portfolio_url, status, updated_date)
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
                certifications, languages, portfolio_url, status, updated_date)
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
                certifications, languages, portfolio_url, status, updated_date)
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
