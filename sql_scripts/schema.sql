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