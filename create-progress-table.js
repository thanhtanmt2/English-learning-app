const mysql = require('mysql2/promise');

async function createTable() {
  const pool = mysql.createPool({
    host: 'localhost',
    user: 'root',
    password: '123456',
    database: 'minlish_db',
    charset: 'utf8mb4'
  });

  try {
    await pool.query(`
      CREATE TABLE IF NOT EXISTS user_grammar_progress (
          id INT AUTO_INCREMENT PRIMARY KEY,
          user_id INT NOT NULL,
          grammar_note_id INT NOT NULL,
          highest_score INT DEFAULT 0,
          total_questions INT DEFAULT 0,
          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
          UNIQUE KEY unique_user_grammar (user_id, grammar_note_id),
          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
          FOREIGN KEY (grammar_note_id) REFERENCES grammar_notes(id) ON DELETE CASCADE
      ) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    `);
    console.log("Created table user_grammar_progress successfully.");
  } catch (err) {
    console.error("Error creating table:", err);
  } finally {
    process.exit(0);
  }
}

createTable();
