const router = require('express').Router();
const {
  register,
  login,
  googleLogin,
  sendRegisterOtp,
  verifyRegisterOtp,
  registerWithOtp,
  forgotPassword,
  resetPassword,
} = require('../controllers/authController');

router.post('/register',              register);
router.post('/login',                 login);
router.post('/google',                googleLogin);
router.post('/register/send-otp',    sendRegisterOtp);
router.post('/register/verify-otp',  verifyRegisterOtp);
router.post('/register/complete',    registerWithOtp);
router.post('/forgot-password',      forgotPassword);
router.post('/reset-password',       resetPassword);

module.exports = router;