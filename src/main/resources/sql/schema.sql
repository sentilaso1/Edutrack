CREATE TABLE users (
    id BINARY(16) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    avatar LONGBLOB,
    bio TEXT,
    gender ENUM('male', 'female', 'other') NOT NULL,
    birth_date DATETIME,
    reset_token BINARY(16),
    created_date DATETIME NOT NULL
);

CREATE TABLE mentors (
    user_id BINARY(16) PRIMARY KEY,
    is_available BOOLEAN NOT NULL,
    total_sessions INT DEFAULT 0,
    expertise TEXT,
    rating DECIMAL(2,1),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE mentees (
    user_id BINARY(16) PRIMARY KEY,
    total_sessions INT DEFAULT 0,
    interests TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE staffs (
    user_id BINARY(16) PRIMARY KEY,
    role ENUM('Admin', 'Manager') NOT NULL,
    created_date DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE courses (
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_approved BOOLEAN NOT NULL,
    is_open BOOLEAN NOT NULL,
    created_date DATETIME NOT NULL
);

CREATE TABLE course_mentor (
    course_id BINARY(16),
    mentor_id BINARY(16),
    status ENUM('pending', 'approved', 'rejected'),
    created_date DATETIME NOT NULL,
    PRIMARY KEY (course_id, mentor_id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (mentor_id) REFERENCES mentors(user_id)
);

CREATE TABLE tags (
    id INT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE course_tags (
    course_id BINARY(16),
    tag_id INT,
    PRIMARY KEY (course_id, tag_id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (tag_id) REFERENCES tags(id)
);

CREATE TABLE slots (
    id INT PRIMARY KEY AUTO_INCREMENT,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL
);

CREATE TABLE mentor_available_time (
    mentor_id BINARY(16),
    slot_id INT,
    day ENUM('Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'),
    PRIMARY KEY (mentor_id, slot_id, day),
    FOREIGN KEY (mentor_id) REFERENCES mentors(user_id),
    FOREIGN KEY (slot_id) REFERENCES slots(id)
);

CREATE TABLE enrollments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    mentee_id BINARY(16),
    mentor_id BINARY(16),
    course_id BINARY(16),
    start_time DATETIME NOT NULL,
    total_sessions INT NOT NULL,
    status ENUM('Pending', 'Accepted', 'Rejected', 'Cancelled') NOT NULL,
    is_paid BOOLEAN NOT NULL,
    is_approved BOOLEAN NOT NULL,
    created_date DATETIME NOT NULL,
    FOREIGN KEY (mentee_id) REFERENCES mentees(user_id),
    FOREIGN KEY (mentor_id) REFERENCES mentors(user_id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
);

CREATE TABLE requested_enrollment_slots (
    id INT PRIMARY KEY AUTO_INCREMENT,
    enrollment_id INT,
    slot_id INT,
    date DATETIME NOT NULL,
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(id),
    FOREIGN KEY (slot_id) REFERENCES slots(id)
);

CREATE TABLE mentor_schedule_request (
    id INT PRIMARY KEY AUTO_INCREMENT,
    mentee_id BINARY(16),
    mentor_id BINARY(16),
    course_id BINARY(16),
    slot_id INT,
    date DATETIME NOT NULL,
    status ENUM('Pending', 'Accepted', 'Rejected', 'Cancelled') NOT NULL,
    created_date DATETIME NOT NULL,
    FOREIGN KEY (mentee_id) REFERENCES mentees(user_id),
    FOREIGN KEY (mentor_id) REFERENCES mentors(user_id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (slot_id) REFERENCES slots(id)
);

CREATE TABLE slot_prices (
    course_id BINARY(16),
    mentor_id BINARY(16),
    price DOUBLE NOT NULL,
    PRIMARY KEY (course_id, mentor_id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (mentor_id) REFERENCES mentors(user_id)
);

CREATE TABLE schedules (
    id INT PRIMARY KEY AUTO_INCREMENT,
    mentee_id BINARY(16),
    mentor_id BINARY(16),
    course_id BINARY(16),
    slot_id INT,
    date DATETIME NOT NULL,
    attendance ENUM('Not Yet', 'Present', 'Absent', 'Cancelled') NOT NULL,
    FOREIGN KEY (mentee_id) REFERENCES mentees(user_id),
    FOREIGN KEY (mentor_id) REFERENCES mentors(user_id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (slot_id) REFERENCES slots(id)
);

CREATE TABLE wallets (
    id BINARY(16) PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    balance DOUBLE NOT NULL,
    on_hold DOUBLE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE transactions (
    id BINARY(16) PRIMARY KEY,
    wallet_id BINARY(16),
    amount DOUBLE NOT NULL,
    status ENUM('pending', 'success', 'failed') NOT NULL,
    created_date DATETIME NOT NULL,
    updated_date DATETIME NOT NULL,
    FOREIGN KEY (wallet_id) REFERENCES wallets(id)
);

CREATE TABLE cv (
    user_id BINARY(16) PRIMARY KEY,
    summary TEXT NOT NULL,
    experience_years DECIMAL(3,1) NOT NULL,
    skills TEXT NOT NULL,
    education TEXT NOT NULL,
    experience TEXT NOT NULL,
    certifications TEXT NOT NULL,
    languages TEXT NOT NULL,
    portfolio_url VARCHAR(255),
    status ENUM('pending', 'approved', 'rejected') NOT NULL,
    created_date DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE teaching_materials (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    file_type VARCHAR(100) NOT NULL,
    file LONGBLOB NOT NULL,
    course_id BINARY(16),
    mentor_id BINARY(16),
    upload_date DATETIME NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (mentor_id) REFERENCES mentors(user_id)
);

CREATE TABLE feedbacks (
    id BINARY(16) PRIMARY KEY,
    content TEXT NOT NULL,
    rating DECIMAL(2,1) NOT NULL,
    mentee_id BINARY(16),
    course_id BINARY(16),
    mentor_id BINARY(16),
    created_date DATETIME NOT NULL,
    FOREIGN KEY (mentee_id) REFERENCES mentees(user_id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (mentor_id) REFERENCES mentors(user_id)
);

CREATE TABLE bookmarks (
    id INT PRIMARY KEY AUTO_INCREMENT,
    course_id BINARY(16),
    user_id BINARY(16),
    created_date DATETIME NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
