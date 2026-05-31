const router = require('express').Router();
const auth   = require('../middleware/auth');
const ctrl   = require('../controllers/vocabController');

// Word Sets
router.get  ('/wordsets',          auth, ctrl.getWordSets);
router.post ('/wordsets',          auth, ctrl.createWordSet);
router.get  ('/wordsets/:id',      auth, ctrl.getWordSetById);
router.put  ('/wordsets/:id',      auth, ctrl.updateWordSet);
router.delete('/wordsets/:id',     auth, ctrl.deleteWordSet);

// Words
router.get  ('/wordsets/:id/words', auth, ctrl.getWordsBySet);
router.get  ('/words',              auth, ctrl.getAllWords);
router.post ('/words',              auth, ctrl.addWord);
router.get  ('/words/:id',          auth, ctrl.getWordById);
router.put  ('/words/:id',          auth, ctrl.updateWord);
router.delete('/words/:id',         auth, ctrl.deleteWord);

// SRS
router.get  ('/words/review',       auth, ctrl.getWordsToReview);
router.post ('/words/:id/review',   auth, ctrl.reviewWord);

module.exports = router;