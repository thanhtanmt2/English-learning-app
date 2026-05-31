const db     = require('../config/db');
const bcrypt = require('bcryptjs');
const jwt    = require('jsonwebtoken');

// POST /api/auth/register
exports.register = async (req, res) => {
  const { name, email, password, goal } = req.body;

  // Validate đầu vào
  if (!name || !email || !password) {
    return res.status(400).json({ message: 'Vui lòng điền đầy đủ thông tin' });
  }

  try {
    // Kiểm tra email đã tồn tại chưa
    const [[existing]] = await db.execute(
      'SELECT id FROM users WHERE email = ?', [email]
    );
    if (existing) {
      return res.status(409).json({ message: 'Email đã được sử dụng' });
    }

    // Mã hóa password
    const hashed = await bcrypt.hash(password, 10);

    // Tạo user mới
    const [result] = await db.execute(
      'INSERT INTO users (name, email, password, goal) VALUES (?, ?, ?, ?)',
      [name, email, hashed, goal || 'general']
    );

    // Tạo JWT token
    const token = jwt.sign(
      { id: result.insertId, email },
      process.env.JWT_SECRET,
      { expiresIn: process.env.JWT_EXPIRES_IN }
    );

    res.status(201).json({
      message: 'Đăng ký thành công',
      token,
      user: { id: result.insertId, name, email }
    });

  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

// POST /api/auth/login
exports.login = async (req, res) => {
  const { email, password } = req.body;
  console.log(`[Login Attempt] Email: ${email}, Password: ${password}`);

  if (!email || !password) {
    return res.status(400).json({ message: 'Vui lòng nhập email và password' });
  }

  try {
    // Tìm user theo email
    const [[user]] = await db.execute(
      'SELECT * FROM users WHERE email = ?', [email]
    );

    if (!user) {
      console.log(`[Login Failed] User not found: ${email}`);
      return res.status(401).json({ message: 'Email hoặc password không đúng' });
    }

    // So sánh password
    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) {
      console.log(`[Login Failed] Password mismatch for: ${email}`);
      return res.status(401).json({ message: 'Email hoặc password không đúng' });
    }

    // Tạo JWT token
    const token = jwt.sign(
      { id: user.id, email: user.email },
      process.env.JWT_SECRET,
      { expiresIn: process.env.JWT_EXPIRES_IN }
    );

    res.json({
      message: 'Đăng nhập thành công',
      token,
      user: { id: user.id, name: user.name, email: user.email, goal: user.goal }
    });

  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};