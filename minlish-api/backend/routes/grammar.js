const router = require('express').Router();
const grammarController = require('../controllers/grammarController');
const auth = require('../middleware/auth');

router.use(auth);

router.get('/grammar', grammarController.getGrammar);
router.post('/grammar', grammarController.addGrammar);
router.get('/quiz_questions', grammarController.getQuizzes);
router.post('/grammar/:id/score', grammarController.submitQuizScore);

module.exports = router;
