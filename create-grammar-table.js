const mysql = require('mysql2/promise');

async function createTable() {
  const pool = mysql.createPool({
    host: 'localhost',
    user: 'root',
    password: '123456',
    database: 'minlish_db'
  });

  await pool.query(`
    CREATE TABLE IF NOT EXISTS grammar_quizzes (
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
  `);
  console.log('Table grammar_quizzes created!');
  process.exit();
}

createTable();
