const db = require('../config/db');

exports.updateUser = async (req, res) => {
  const userId = req.params.id;
  const { name, goal, level } = req.body;

  try {
    const [result] = await db.execute(
      'UPDATE users SET name = ?, goal = ?, level = ? WHERE id = ?',
      [name, goal || null, level || null, userId]
    );

    if (result.affectedRows === 0) {
      return res.status(404).json({ message: 'Không tìm thấy người dùng' });
    }

    // Fetch the updated user
    const [[user]] = await db.execute(
      'SELECT id, name, email, goal, level, avatar FROM users WHERE id = ?',
      [userId]
    );

    res.json(user);
  } catch (err) {
    console.error('[Update User Error]', err);
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};
