const router = require('express').Router();
const auth = require('../middleware/auth');
const ctrl = require('../controllers/grammarController');

router.get('/grammar', auth, ctrl.getGrammarNotes);
router.get('/grammar/:id', auth, ctrl.getGrammarNoteById);
router.post('/grammar', auth, ctrl.createGrammarNote);
router.put('/grammar/:id', auth, ctrl.updateGrammarNote);
router.delete('/grammar/:id', auth, ctrl.deleteGrammarNote);

module.exports = router;