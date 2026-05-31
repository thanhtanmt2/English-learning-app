const router = require('express').Router();
const auth   = require('../middleware/auth');
const ctrl   = require('../controllers/progressController');

router.get('/progress', auth, ctrl.getProgress);
router.get('/progress_history', auth, ctrl.getProgressHistory);

module.exports = router;