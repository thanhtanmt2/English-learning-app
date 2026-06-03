const express = require('express');
const router = express.Router();
const auth = require('../middleware/auth');
const { getSettings, updateSettings } = require('../controllers/notificationController');

router.get('/settings', auth, getSettings);
router.put('/settings', auth, updateSettings);

module.exports = router;
