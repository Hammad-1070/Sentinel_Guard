create database sentinel_guard_db_01;


use sentinel_guard_db_01

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    pass_hash VARCHAR(255) NOT NULL,
    security_question_1 VARCHAR(255),
    security_answer_hash_1 VARCHAR(255),
    security_question_2 VARCHAR(255),
    security_answer_hash_2 VARCHAR(255),
    encrypted_master_key VARCHAR(500),
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE users
MODIFY username VARCHAR(50) COLLATE utf8mb4_bin UNIQUE NOT NULL;


USE sentinel_guard_DB;

CREATE TABLE vault_notes (
    note_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    note_title VARCHAR(100) NOT NULL,
    encrypted_content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);


// This will maintain the login details

CREATE TABLE audit_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    attempted_username VARCHAR(50) NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    log_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);