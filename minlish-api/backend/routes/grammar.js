const router = require('express').Router();
const grammarController = require('../controllers/grammarController');
const auth = require('../middleware/auth');

router.use(auth);

router.get('/grammar', grammarController.getGrammar);
router.post('/grammar', grammarController.addGrammar);
router.get('/quiz_questions', grammarController.getQuizzes);
router.post('/grammar/:id/score', grammarController.submitQuizScore);

router.get('/grammar/:id', grammarController.getGrammarNoteById);
router.put('/grammar/:id', grammarController.updateGrammarNote);
router.delete('/grammar/:id', grammarController.deleteGrammarNote);

module.exports = router;
