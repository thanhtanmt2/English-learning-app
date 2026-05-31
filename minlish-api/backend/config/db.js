const mysql = require('mysql2/promise');
require('dotenv').config();

// Pool thay vì single connection
// Pool tự quản lý nhiều kết nối cùng lúc, phù hợp cho API
const pool = mysql.createPool({
  host:     process.env.DB_HOST,
  port:     process.env.DB_PORT,
  user:     process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME,
  waitForConnections: true,
  connectionLimit: 10,
  charset: 'utf8mb4' // Đảm bảo hỗ trợ tiếng Việt có dấu
});

// Test kết nối khi khởi động
pool.getConnection()
  .then(() => console.log('Kết nối MySQL thành công!'))
  .catch(err => console.error('Lỗi kết nối MySQL:', err.message));

module.exports = pool;