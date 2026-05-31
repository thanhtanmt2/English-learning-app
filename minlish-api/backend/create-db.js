const mysql = require('mysql2/promise');
require('dotenv').config();

async function createDb() {
  try {
    const connection = await mysql.createConnection({
      host: process.env.DB_HOST,
      port: process.env.DB_PORT,
      user: process.env.DB_USER,
      password: process.env.DB_PASSWORD,
    });
    await connection.query(`CREATE DATABASE IF NOT EXISTS \`${process.env.DB_NAME}\`;`);
    console.log(`Database ${process.env.DB_NAME} created or already exists.`);

    // Thêm lệnh xóa bảng cũ để làm mới cấu hình UTF-8
    const connection2 = await mysql.createConnection({
      host: process.env.DB_HOST,
      port: process.env.DB_PORT,
      user: process.env.DB_USER,
      password: process.env.DB_PASSWORD,
      database: process.env.DB_NAME,
      multipleStatements: true
    });

    console.log('Cleaning old tables...');
    await connection2.query(`
      SET FOREIGN_KEY_CHECKS = 0;
      DROP TABLE IF EXISTS user_progress;
      DROP TABLE IF EXISTS words;
      DROP TABLE IF EXISTS word_sets;
      DROP TABLE IF EXISTS grammar_notes;
      DROP TABLE IF EXISTS users;
      SET FOREIGN_KEY_CHECKS = 1;
    `);

    const fs = require('fs');
    const path = require('path');
    const schema = fs.readFileSync(path.join(__dirname, 'config', 'schema.sql'), 'utf8');

    await connection2.query(schema);
    console.log('Schema imported successfully (UTF-8 enabled).');
    
    process.exit(0);
  } catch (error) {
    console.error('Error creating database:', error);
    process.exit(1);
  }
}

createDb();
