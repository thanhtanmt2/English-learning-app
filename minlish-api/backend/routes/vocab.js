const router = require('express').Router();
const auth   = require('../middleware/auth');
const ctrl   = require('../controllers/vocabController');

// Word Sets
router.get  ('/wordsets',          auth, ctrl.getWordSets);
router.post ('/wordsets',          auth, ctrl.createWordSet);

// Words
router.get  ('/wordsets/:id/words', auth, ctrl.getWords);
router.post ('/words',              auth, ctrl.addWord);

// SRS
router.get  ('/words/review',       auth, ctrl.getReviewWords);
router.post ('/words/:id/review',   auth, ctrl.submitReview);

module.exports = router;