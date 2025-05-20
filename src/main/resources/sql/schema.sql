-- USERS
CREATE TABLE users
(
    id           VARCHAR(36) PRIMARY KEY,
    email        VARCHAR(255) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    full_name    VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20)  NOT NULL,
    avatar       LONGBLOB,
    bio          TEXT,
    created_date DATETIME     NOT NULL
);

-- MENTORS
CREATE TABLE mentors
(
    user_id        VARCHAR(36) PRIMARY KEY,
    is_available   BOOLEAN DEFAULT TRUE,
    total_sessions INT     DEFAULT 0,
    expertise      TEXT,
    rating         DECIMAL(2, 1),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

-- MENTEES
CREATE TABLE mentees
(
    user_id        VARCHAR(36) PRIMARY KEY,
    total_sessions INT DEFAULT 0,
    interests      TEXT,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

-- STAFFS
CREATE TABLE staffs
(
    user_id      VARCHAR(36) PRIMARY KEY,
    role         ENUM ('Admin', 'Manager') NOT NULL,
    created_date DATETIME                  NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

-- COURSES
CREATE TABLE courses
(
    id           VARCHAR(36) PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    description  TEXT         NOT NULL,
    is_approved  BOOLEAN DEFAULT FALSE,
    is_open      BOOLEAN DEFAULT FALSE,
    created_date DATETIME     NOT NULL
);

-- COURSE_MENTOR
CREATE TABLE course_mentor
(
    course_id    VARCHAR(36),
    mentor_id    VARCHAR(36),
    created_date DATETIME NOT NULL,
    PRIMARY KEY (course_id, mentor_id),
    FOREIGN KEY (course_id) REFERENCES courses (id),
    FOREIGN KEY (mentor_id) REFERENCES mentors (user_id)
);

-- TAGS
CREATE TABLE tags
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT
);

-- COURSE_TAGS
CREATE TABLE course_tags
(
    course_id VARCHAR(36),
    tag_id    INT,
    PRIMARY KEY (course_id, tag_id),
    FOREIGN KEY (course_id) REFERENCES courses (id),
    FOREIGN KEY (tag_id) REFERENCES tags (id)
);

-- SLOTS
CREATE TABLE slots
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    start_time TIME NOT NULL,
    end_time   TIME NOT NULL
);

-- MENTOR_AVAILABLE_TIME
CREATE TABLE mentor_available_time
(
    mentor_id VARCHAR(36),
    slot_id   INT,
    day       ENUM ('Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'),
    PRIMARY KEY (mentor_id, slot_id, day),
    FOREIGN KEY (mentor_id) REFERENCES mentors (user_id),
    FOREIGN KEY (slot_id) REFERENCES slots (id)
);

-- ENROLLMENTS
CREATE TABLE enrollments
(
    id             INT AUTO_INCREMENT PRIMARY KEY,
    mentee_id      VARCHAR(36),
    mentor_id      VARCHAR(36),
    course_id      VARCHAR(36),
    start_time     DATETIME                                              NOT NULL,
    total_sessions INT,
    status         ENUM ('Pending', 'Accepted', 'Rejected', 'Cancelled') NOT NULL,
    is_paid        BOOLEAN DEFAULT FALSE,
    is_approved    BOOLEAN DEFAULT FALSE,
    created_date   DATETIME                                              NOT NULL,
    FOREIGN KEY (mentee_id) REFERENCES mentees (user_id),
    FOREIGN KEY (mentor_id) REFERENCES mentors (user_id),
    FOREIGN KEY (course_id) REFERENCES courses (id)
);

-- REQUESTED_ENROLLMENT_SLOTS
CREATE TABLE requested_enrollment_slots
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    enrollment_id INT,
    slot_id       INT,
    date          DATE NOT NULL,
    FOREIGN KEY (enrollment_id) REFERENCES enrollments (id),
    FOREIGN KEY (slot_id) REFERENCES slots (id)
);

-- MENTOR_SCHEDULE_REQUEST
CREATE TABLE mentor_schedule_request
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    mentee_id    VARCHAR(36),
    mentor_id    VARCHAR(36),
    course_id    VARCHAR(36),
    slot_id      INT,
    date         DATE                                                  NOT NULL,
    status       ENUM ('Pending', 'Accepted', 'Rejected', 'Cancelled') NOT NULL,
    created_date DATETIME                                              NOT NULL,
    FOREIGN KEY (mentee_id) REFERENCES mentees (user_id),
    FOREIGN KEY (mentor_id) REFERENCES mentors (user_id),
    FOREIGN KEY (course_id) REFERENCES courses (id),
    FOREIGN KEY (slot_id) REFERENCES slots (id)
);

-- SLOT_PRICES
CREATE TABLE slot_prices
(
    course_id VARCHAR(36),
    mentor_id VARCHAR(36),
    price     DOUBLE NOT NULL,
    PRIMARY KEY (course_id, mentor_id),
    FOREIGN KEY (course_id) REFERENCES courses (id),
    FOREIGN KEY (mentor_id) REFERENCES mentors (user_id)
);

-- SCHEDULES
CREATE TABLE schedules
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    mentee_id  VARCHAR(36),
    mentor_id  VARCHAR(36),
    course_id  VARCHAR(36),
    slot_id    INT,
    date       DATE                                               NOT NULL,
    attendance ENUM ('Not Yet', 'Present', 'Absent', 'Cancelled') NOT NULL,
    FOREIGN KEY (mentee_id) REFERENCES mentees (user_id),
    FOREIGN KEY (mentor_id) REFERENCES mentors (user_id),
    FOREIGN KEY (course_id) REFERENCES courses (id),
    FOREIGN KEY (slot_id) REFERENCES slots (id)
);

-- WALLETS
CREATE TABLE wallets
(
    id      VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36),
    balance DOUBLE DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

-- MENTEE_PAYMENTS
CREATE TABLE mentee_payments
(
    id           VARCHAR(36) PRIMARY KEY,
    wallet_id    VARCHAR(36),
    amount       DOUBLE   NOT NULL,
    created_date DATETIME NOT NULL,
    FOREIGN KEY (wallet_id) REFERENCES wallets (id)
);

-- MENTOR_EARNINGS
CREATE TABLE mentor_earnings
(
    id           VARCHAR(36) PRIMARY KEY,
    wallet_id    VARCHAR(36),
    amount       DOUBLE   NOT NULL,
    created_date DATETIME NOT NULL,
    FOREIGN KEY (wallet_id) REFERENCES wallets (id)
);

-- CV
CREATE TABLE cv
(
    mentor_id           VARCHAR(36) PRIMARY KEY,
    summary             TEXT,
    years_of_experience DECIMAL(3, 1),
    skills              TEXT,
    education           TEXT,
    experience          TEXT,
    certifications      TEXT,
    languages           TEXT,
    portfolio_url       VARCHAR(255),
    update_date         DATETIME NOT NULL,
    FOREIGN KEY (mentor_id) REFERENCES mentors (user_id)
);

-- TEACHING_MATERIALS
CREATE TABLE teaching_materials
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    file        LONGBLOB NOT NULL,
    course_id   VARCHAR(36),
    mentor_id   VARCHAR(36),
    upload_date DATETIME NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses (id),
    FOREIGN KEY (mentor_id) REFERENCES mentors (user_id)
);

-- FEEDBACKS
CREATE TABLE feedbacks
(
    id           VARCHAR(36) PRIMARY KEY,
    content      TEXT,
    rating       DECIMAL(2, 1),
    mentee_id    VARCHAR(36),
    course_id    VARCHAR(36),
    mentor_id    VARCHAR(36),
    created_date DATETIME NOT NULL,
    FOREIGN KEY (mentee_id) REFERENCES mentees (user_id),
    FOREIGN KEY (course_id) REFERENCES courses (id),
    FOREIGN KEY (mentor_id) REFERENCES mentors (user_id)
);

-- BOOKMARKS
CREATE TABLE bookmarks
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    course_id    VARCHAR(36),
    user_id      VARCHAR(36),
    created_date DATETIME NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);
