CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    phone_number VARCHAR(20),
    avatar LONGBLOB,
    bio TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE mentors (
    user_id VARCHAR(36) PRIMARY KEY,
    is_available BOOLEAN,
    total_sessions INT DEFAULT 0,
    expertise TEXT,
    rating DECIMAL(2,1),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE mentees (
    user_id VARCHAR(36) PRIMARY KEY,
    total_sessions INT DEFAULT 0,
    interests TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE staffs (
    user_id VARCHAR(36) PRIMARY KEY,
    role ENUM('Admin', 'Manager'),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE courses (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    is_approved BOOLEAN,
    is_open BOOLEAN,
    total_sessions INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE course_mentor (
    course_id VARCHAR(36),
    mentor_id VARCHAR(36),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (course_id, mentor_id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (mentor_id) REFERENCES mentors(user_id)
);

CREATE TABLE tags (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255),
    description TEXT
);

CREATE TABLE course_tags (
    course_id VARCHAR(36),
    tag_id INT,
    PRIMARY KEY (course_id, tag_id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (tag_id) REFERENCES tags(id)
);

CREATE TABLE mentee_progress (
    mentee_id VARCHAR(36),
    course_id VARCHAR(36),
    progress_percentage DECIMAL(5,2),
    note TEXT,
    PRIMARY KEY (mentee_id, course_id),
    FOREIGN KEY (mentee_id) REFERENCES mentees(user_id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
);

CREATE TABLE schedules (
    id INT PRIMARY KEY AUTO_INCREMENT,
    time DATETIME,
    status ENUM('Not Yet', 'In Progress', 'Finished', 'Cancelled'),
    mentee_id VARCHAR(36),
    mentor_id VARCHAR(36),
    course_id VARCHAR(36),
    attendance ENUM('Not Yet', 'Present', 'Absent'),
    score DECIMAL(3,1),
    mentor_note TEXT,
    FOREIGN KEY (mentee_id) REFERENCES mentees(user_id),
    FOREIGN KEY (mentor_id) REFERENCES mentors(user_id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
);

CREATE TABLE mentee_preferred_time (
    mentee_id VARCHAR(36),
    mentor_id VARCHAR(36),
    course_id VARCHAR(36),
    day TEXT,
    start_time TIME,
    end_time TIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    approve_date DATETIME,
    PRIMARY KEY (mentee_id, mentor_id, course_id),
    FOREIGN KEY (mentee_id) REFERENCES mentees(user_id),
    FOREIGN KEY (mentor_id) REFERENCES mentors(user_id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
);

CREATE TABLE requests (
    id INT PRIMARY KEY AUTO_INCREMENT,
    mentee_id VARCHAR(36),
    course_id VARCHAR(36),
    from_schedule_id INT,
    to_schedule_id INT,
    reason TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (mentee_id) REFERENCES mentees(user_id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (from_schedule_id) REFERENCES schedules(id),
    FOREIGN KEY (to_schedule_id) REFERENCES schedules(id)
    -- Mentor ID match check must be enforced via application logic
);

CREATE TABLE complaints (
    id INT PRIMARY KEY AUTO_INCREMENT,
    mentee_id VARCHAR(36),
    course_id VARCHAR(36),
    mentor_id VARCHAR(36),
    content TEXT,
    status ENUM('Pending', 'Resolved', 'Rejected'),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (mentee_id) REFERENCES mentees(user_id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (mentor_id) REFERENCES mentors(user_id)
);

CREATE TABLE mentee_payments (
    id VARCHAR(36) PRIMARY KEY,
    amount DECIMAL(15,3),
    method ENUM('Office', 'Bank', 'Gateway'),
    from_mentee_id VARCHAR(36),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (from_mentee_id) REFERENCES mentees(user_id)
);

CREATE TABLE mentor_earnings (
    id VARCHAR(36) PRIMARY KEY,
    amount DECIMAL(15,3),
    to_mentor_id VARCHAR(36),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (to_mentor_id) REFERENCES mentors(user_id)
);

CREATE TABLE cv (
    mentor_id VARCHAR(36) PRIMARY KEY,
    summary TEXT,
    years_of_experience DECIMAL(3,1),
    skills TEXT,
    education TEXT,
    experience TEXT,
    certifications TEXT,
    languages TEXT,
    portfolio_url VARCHAR(255),
    updated_at DATETIME,
    FOREIGN KEY (mentor_id) REFERENCES mentors(user_id)
);

CREATE TABLE teaching_materials (
    id INT PRIMARY KEY AUTO_INCREMENT,
    file LONGBLOB,
    course_id VARCHAR(36),
    mentor_id VARCHAR(36),
    uploaded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (mentor_id) REFERENCES mentors(user_id)
);

CREATE TABLE feedbacks (
    id VARCHAR(36) PRIMARY KEY,
    content TEXT,
    rating DECIMAL(2,1),
    mentee_id VARCHAR(36),
    course_id VARCHAR(36),
    mentor_id VARCHAR(36),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (mentee_id) REFERENCES mentees(user_id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (mentor_id) REFERENCES mentors(user_id)
);
