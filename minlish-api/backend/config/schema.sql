USE minlish_db;

-- Đảm bảo database dùng UTF-8
ALTER DATABASE minlish_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 1. Bảng người dùng
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    goal VARCHAR(100) DEFAULT 'general',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- 2. Bảng bộ từ vựng
CREATE TABLE word_sets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- 3. Bảng từ vựng (có cột SM-2)
CREATE TABLE words (
    id INT AUTO_INCREMENT PRIMARY KEY,
    word_set_id INT NOT NULL,
    word VARCHAR(100) NOT NULL,
    pronunciation VARCHAR(100),
    meaning TEXT NOT NULL,
    example TEXT,
    ease_factor FLOAT DEFAULT 2.5,
    interval_days INT DEFAULT 1,
    next_review_date DATE DEFAULT (CURRENT_DATE),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (word_set_id) REFERENCES word_sets(id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- 4. Bảng ngữ pháp
CREATE TABLE grammar_notes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    formula TEXT,
    explanation TEXT,
    example TEXT,
    common_mistake TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- 5. Bảng tiến độ học hàng ngày
CREATE TABLE user_progress (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    study_date DATE NOT NULL,
    words_studied INT DEFAULT 0,
    correct_count INT DEFAULT 0,
    UNIQUE KEY unique_user_date (user_id, study_date),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE words
ADD COLUMN part_of_speech VARCHAR(50),
    ADD COLUMN v2_past VARCHAR(100),
    ADD COLUMN v3_past_participle VARCHAR(100),
    ADD COLUMN plural_form VARCHAR(100),
    ADD COLUMN description TEXT,
    ADD COLUMN collocation TEXT,
    ADD COLUMN synonyms TEXT,
    ADD COLUMN antonyms TEXT,
    ADD COLUMN cefr_level VARCHAR(10),
    ADD COLUMN topic VARCHAR(100),
    ADD COLUMN quiz_question TEXT;

-- 6. Bảng trắc nghiệm ngữ pháp
CREATE TABLE grammar_quizzes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    grammar_note_id INT NOT NULL,
    question TEXT NOT NULL,
    option_a VARCHAR(255) NOT NULL,
    option_b VARCHAR(255) NOT NULL,
    option_c VARCHAR(255) NOT NULL,
    option_d VARCHAR(255) NOT NULL,
    correct_answer VARCHAR(255) NOT NULL,
    explanation TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (grammar_note_id) REFERENCES grammar_notes(id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;