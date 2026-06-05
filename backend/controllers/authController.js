const db     = require('../config/db');
const bcrypt = require('bcryptjs');
const jwt    = require('jsonwebtoken');
const { sendOtpEmail } = require('../utils/emailService');
const { generateOtp, saveOtp, verifyOtp } = require('../utils/otpHelper');

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
    console.error('[Register Error]', err);
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
    console.error('[Login Error]', err);
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

// POST /api/auth/register/send-otp
exports.sendRegisterOtp = async (req, res) => {
  const { email } = req.body;
  if (!email || !email.includes('@')) {
    return res.status(400).json({ message: 'Email không hợp lệ' });
  }
  try {
    const [[existing]] = await db.execute('SELECT id FROM users WHERE email = ?', [email]);
    if (existing) {
      return res.status(409).json({ message: 'Email đã được sử dụng' });
    }
    const otp = generateOtp();
    await saveOtp(email, otp, 'register');
    await sendOtpEmail(email, otp, 'register');
    res.json({ message: 'Mã OTP đã được gửi đến email của bạn' });
  } catch (err) {
    console.error('[SendRegisterOtp Error]', err);
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

// POST /api/auth/register/verify-otp
exports.verifyRegisterOtp = async (req, res) => {
  const { email, otp } = req.body;
  if (!email || !otp) {
    return res.status(400).json({ message: 'Thiếu email hoặc mã OTP' });
  }
  try {
    const valid = await verifyOtp(email, otp, 'register');
    if (!valid) {
      return res.status(400).json({ message: 'Mã OTP không hợp lệ hoặc đã hết hạn' });
    }
    res.json({ message: 'Xác thực OTP thành công', verified: true });
  } catch (err) {
    console.error('[VerifyRegisterOtp Error]', err);
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

// POST /api/auth/register/complete
exports.registerWithOtp = async (req, res) => {
  const { name, email, password, otp, goal, level } = req.body;
  if (!name || !email || !password || !otp) {
    return res.status(400).json({ message: 'Vui lòng điền đầy đủ thông tin' });
  }
  try {
    const valid = await verifyOtp(email, otp, 'register');
    if (!valid) {
      return res.status(400).json({ message: 'Mã OTP không hợp lệ hoặc đã hết hạn' });
    }
    const [[existing]] = await db.execute('SELECT id FROM users WHERE email = ?', [email]);
    if (existing) {
      return res.status(409).json({ message: 'Email đã được sử dụng' });
    }
    const hashed = await bcrypt.hash(password, 10);
    const [result] = await db.execute(
      'INSERT INTO users (name, email, password, goal, level) VALUES (?, ?, ?, ?, ?)',
      [name, email, hashed, goal || 'general', level || null]
    );
    const token = jwt.sign(
      { id: result.insertId, email },
      process.env.JWT_SECRET,
      { expiresIn: process.env.JWT_EXPIRES_IN }
    );
    res.status(201).json({
      message: 'Đăng ký thành công',
      token,
      user: { id: result.insertId, name, email, goal: goal || 'general', level: level || null }
    });
  } catch (err) {
    console.error('[RegisterWithOtp Error]', err);
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

// POST /api/auth/forgot-password
exports.forgotPassword = async (req, res) => {
  const { email } = req.body;
  if (!email || !email.includes('@')) {
    return res.status(400).json({ message: 'Email không hợp lệ' });
  }
  try {
    const [[user]] = await db.execute('SELECT id FROM users WHERE email = ?', [email]);
    if (!user) {
      return res.status(404).json({ message: 'Email chưa được đăng ký' });
    }
    const otp = generateOtp();
    await saveOtp(email, otp, 'reset_password');
    await sendOtpEmail(email, otp, 'reset_password');
    res.json({ message: 'Mã OTP đã được gửi đến email của bạn' });
  } catch (err) {
    console.error('[ForgotPassword Error]', err);
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

// POST /api/auth/reset-password
exports.resetPassword = async (req, res) => {
  const { email, otp, newPassword } = req.body;
  if (!email || !otp || !newPassword) {
    return res.status(400).json({ message: 'Thiếu thông tin cần thiết' });
  }
  if (newPassword.length < 6) {
    return res.status(400).json({ message: 'Mật khẩu phải dài ít nhất 6 ký tự' });
  }
  try {
    const valid = await verifyOtp(email, otp, 'reset_password');
    if (!valid) {
      return res.status(400).json({ message: 'Mã OTP không hợp lệ hoặc đã hết hạn' });
    }
    const hashed = await bcrypt.hash(newPassword, 10);
    await db.execute('UPDATE users SET password = ? WHERE email = ?', [hashed, email]);
    res.json({ message: 'Đặt lại mật khẩu thành công' });
  } catch (err) {
    console.error('[ResetPassword Error]', err);
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

// POST /api/auth/google
exports.googleLogin = async (req, res) => {
  const { idToken } = req.body;

  if (!idToken) {
    return res.status(400).json({ message: 'Thiếu idToken của Google' });
  }

  try {
    const { OAuth2Client } = require('google-auth-library');
    const client = new OAuth2Client(process.env.GOOGLE_CLIENT_ID);
    
    // 1. Verify idToken
    const ticket = await client.verifyIdToken({
      idToken,
      audience: process.env.GOOGLE_CLIENT_ID,
    });
    const payload = ticket.getPayload();
    const { email, name, sub: google_id, picture: avatar } = payload;

    // 2. Kiểm tra user trong database
    const [[user]] = await db.execute('SELECT * FROM users WHERE email = ?', [email]);

    let userId = null;

    if (user) {
      userId = user.id;
      // Nếu user chưa có google_id thì cập nhật
      if (!user.google_id) {
        await db.execute('UPDATE users SET google_id = ?, avatar = ? WHERE id = ?', [google_id, avatar, userId]);
      }
    } else {
      // 3. Tạo user mới
      const [result] = await db.execute(
        'INSERT INTO users (name, email, password, google_id, avatar) VALUES (?, ?, ?, ?, ?)',
        [name, email, null, google_id, avatar]
      );
      userId = result.insertId;
    }

    // 4. Tạo JWT token
    const token = jwt.sign(
      { id: userId, email },
      process.env.JWT_SECRET,
      { expiresIn: process.env.JWT_EXPIRES_IN }
    );

    res.json({
      message: 'Đăng nhập Google thành công',
      token,
      user: { id: userId, name, email, avatar }
    });

  } catch (err) {
    console.error('[Google Login Error]', err);
    res.status(401).json({ message: 'Xác thực Google thất bại', error: err.message });
  }
};