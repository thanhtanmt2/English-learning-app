const db = require('../config/db');

function generateOtp() {
  return Math.floor(100000 + Math.random() * 900000).toString();
}

async function saveOtp(email, code, type) {
  // Xóa OTP cũ chưa dùng của cùng email+type trước khi tạo mới
  await db.execute(
    'DELETE FROM otp_codes WHERE email = ? AND type = ? AND used = FALSE',
    [email, type]
  );
  const expiresAt = new Date(Date.now() + 10 * 60 * 1000); // 10 phút
  await db.execute(
    'INSERT INTO otp_codes (email, code, type, expires_at) VALUES (?, ?, ?, ?)',
    [email, code, type, expiresAt]
  );
}

async function verifyOtp(email, code, type) {
  const [[row]] = await db.execute(
    `SELECT id FROM otp_codes
     WHERE email = ? AND code = ? AND type = ? AND used = FALSE AND expires_at > NOW()
     ORDER BY created_at DESC LIMIT 1`,
    [email, code, type]
  );
  if (!row) return false;
  await db.execute('UPDATE otp_codes SET used = TRUE WHERE id = ?', [row.id]);
  return true;
}

module.exports = { generateOtp, saveOtp, verifyOtp };
