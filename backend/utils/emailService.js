const nodemailer = require('nodemailer');

const transporter = nodemailer.createTransport({
  host: process.env.EMAIL_HOST,
  port: parseInt(process.env.EMAIL_PORT),
  secure: false,
  auth: {
    user: process.env.EMAIL_USER,
    pass: process.env.EMAIL_PASS,
  },
});

async function sendOtpEmail(to, otp, type) {
  const isRegister = type === 'register';
  const subject = isRegister
    ? '[Minlish] Mã xác thực đăng ký tài khoản'
    : '[Minlish] Mã xác thực đặt lại mật khẩu';
  const action = isRegister ? 'xác thực email đăng ký' : 'đặt lại mật khẩu';

  await transporter.sendMail({
    from: `"Minlish App" <${process.env.EMAIL_USER}>`,
    to,
    subject,
    html: `
      <div style="font-family: Arial, sans-serif; max-width: 480px; margin: 0 auto;">
        <h2 style="color: #4F46E5;">Minlish - Học tiếng Anh</h2>
        <p>Xin chào!</p>
        <p>Bạn đã yêu cầu <strong>${action}</strong>. Vui lòng sử dụng mã OTP dưới đây:</p>
        <div style="background: #F3F4F6; border-radius: 8px; padding: 20px; text-align: center; margin: 24px 0;">
          <span style="font-size: 36px; font-weight: bold; letter-spacing: 8px; color: #4F46E5;">${otp}</span>
        </div>
        <p style="color: #6B7280; font-size: 13px;">Mã có hiệu lực trong <strong>10 phút</strong>. Không chia sẻ mã này cho bất kỳ ai.</p>
        <p style="color: #6B7280; font-size: 13px;">Nếu bạn không thực hiện yêu cầu này, hãy bỏ qua email này.</p>
      </div>
    `,
  });
}

module.exports = { sendOtpEmail };
