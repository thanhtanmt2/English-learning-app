-- Đảm bảo database dùng UTF-8
-- (ALTER DATABASE được thực hiện bởi db-create.js nếu cần, schema chỉ tạo bảng)
-- 1. Bảng người dùng
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NULL,
    google_id VARCHAR(255) UNIQUE,
    avatar VARCHAR(255),
    goal VARCHAR(100) DEFAULT 'general',
    level VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- 2. Bảng bộ từ vựng
CREATE TABLE IF NOT EXISTS word_sets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- 3. Bảng từ vựng (có cột SM-2)
CREATE TABLE IF NOT EXISTS words (
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
CREATE TABLE IF NOT EXISTS grammar_notes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NULL,
    title VARCHAR(150) NOT NULL,
    formula TEXT,
    explanation TEXT,
    example TEXT,
    common_mistake TEXT,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- 5. Bảng tiến độ học hàng ngày
CREATE TABLE IF NOT EXISTS user_progress (
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
-- 6. Bảng cài đặt thông báo
CREATE TABLE IF NOT EXISTS notification_settings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    daily_reminder BOOLEAN DEFAULT TRUE,
    reminder_time TIME DEFAULT '08:00:00',
    quiz_reminders BOOLEAN DEFAULT TRUE,
    progress_updates BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- 7. Bảng tiến độ làm trắc nghiệm ngữ pháp của người dùng
CREATE TABLE IF NOT EXISTS user_grammar_progress (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    grammar_note_id INT NOT NULL,
    highest_score INT NOT NULL DEFAULT 0,
    total_questions INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_grammar (user_id, grammar_note_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (grammar_note_id) REFERENCES grammar_notes(id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- 8. Bảng OTP (đăng ký + quên mật khẩu)
CREATE TABLE IF NOT EXISTS otp_codes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(150) NOT NULL,
    code VARCHAR(6) NOT NULL,
    type ENUM('register', 'reset_password') NOT NULL,
    expires_at DATETIME NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- 9. Bang cau hoi trac nghiem ngu phap
CREATE TABLE IF NOT EXISTS grammar_quizzes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    grammar_note_id INT NOT NULL,
    question TEXT NOT NULL,
    option_a VARCHAR(255) NOT NULL,
    option_b VARCHAR(255) NOT NULL,
    option_c VARCHAR(255) NOT NULL,
    option_d VARCHAR(255) NOT NULL,
    correct_answer CHAR(1) NOT NULL,
    explanation TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (grammar_note_id) REFERENCES grammar_notes(id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;