const router = require('express').Router();
const { updateUser } = require('../controllers/userController');

// PATCH /api/users/:id
router.patch('/:id', updateUser);

module.exports = router;
