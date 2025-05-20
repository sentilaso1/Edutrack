-- USERS
INSERT INTO users (id, email, password, full_name, phone, bio, avatar, created_date)
VALUES
-- Mentees
(UUID_TO_BIN('a1111111-aaaa-aaaa-aaaa-aaaaaaaaaaaa'), 'alice@example.com', 'password123', 'Alice Johnson', '0123456789',
 'Aspiring developer', NULL, NOW()),
(UUID_TO_BIN('a2222222-aaaa-aaaa-aaaa-aaaaaaaaaaaa'), 'ethan@example.com', 'password123', 'Ethan Tran', '0234567890',
 'Loves algorithms', NULL, NOW()),
(UUID_TO_BIN('a3333333-aaaa-aaaa-aaaa-aaaaaaaaaaaa'), 'nina@example.com', 'password123', 'Nina Nguyen', '0345678901',
 'Frontend enthusiast', NULL, NOW()),

-- Mentors
(UUID_TO_BIN('b1111111-bbbb-bbbb-bbbb-bbbbbbbbbbbb'), 'bob@example.com', 'password123', 'Bob Smith', '0987654321',
 'Java backend enthusiast', NULL, NOW()),
(UUID_TO_BIN('b2222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb'), 'carol@example.com', 'password123', 'Carol White', '0112233445',
 'Experienced mentor', NULL, NOW()),
(UUID_TO_BIN('b3333333-bbbb-bbbb-bbbb-bbbbbbbbbbbb'), 'hassan@example.com', 'password123', 'Hassan Ali', '0456789012',
 'Cloud specialist', NULL, NOW()),

-- Staff
(UUID_TO_BIN('c1111111-cccc-cccc-cccc-cccccccccccc'), 'dave@example.com', 'password123', 'Dave Lee', '0998877665',
 'Staff and admin', NULL, NOW()),
(UUID_TO_BIN('c2222222-cccc-cccc-cccc-cccccccccccc'), 'sophia@example.com', 'password123', 'Sophia Lin', '0887766554',
 'Manages users', NULL, NOW());

-- MENTEES
INSERT INTO mentees (user_id, total_sessions, interests)
VALUES (UUID_TO_BIN('a1111111-aaaa-aaaa-aaaa-aaaaaaaaaaaa'), 5, 'Web development, JavaScript'),
       (UUID_TO_BIN('a2222222-aaaa-aaaa-aaaa-aaaaaaaaaaaa'), 3, 'Competitive programming, C++'),
       (UUID_TO_BIN('a3333333-aaaa-aaaa-aaaa-aaaaaaaaaaaa'), 7, 'UI/UX Design, React');

-- MENTORS
INSERT INTO mentors (user_id, is_available, total_sessions, expertise, rating, role)
VALUES (UUID_TO_BIN('b1111111-bbbb-bbbb-bbbb-bbbbbbbbbbbb'), 1, 20, 'Spring Boot, Microservices', 4.5, 'Manager'),
       (UUID_TO_BIN('b2222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb'), 1, 50, 'Data Science, Machine Learning', 4.8, 'Admin'),
       (UUID_TO_BIN('b3333333-bbbb-bbbb-bbbb-bbbbbbbbbbbb'), 0, 10, 'AWS, Docker, DevOps', 4.1, 'Manager');

-- STAFFS
INSERT INTO staffs (user_id, role)
VALUES (UUID_TO_BIN('c1111111-cccc-cccc-cccc-cccccccccccc'), 'Admin'),
       (UUID_TO_BIN('c2222222-cccc-cccc-cccc-cccccccccccc'), 'Manager');


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
