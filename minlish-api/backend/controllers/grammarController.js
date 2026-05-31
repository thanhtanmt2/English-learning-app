const db = require('../config/db');

//Lấy danh sách tất cả ghi chú ngữ pháp của user
exports.getGrammarNotes = async (req, res) => {
  try {
    const userId = req.user.id;
    const [rows] = await db.execute(
      'SELECT * FROM grammar_notes WHERE user_id = ? ORDER BY created_at DESC',
      [userId]
    );
    res.json(rows);
  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

//Chi tiết một ghi chú ngữ pháp
exports.getGrammarNoteById = async (req, res) => {
  try {
    const userId = req.user.id;
    const noteId = req.params.id;

    const [[note]] = await db.execute(
      'SELECT * FROM grammar_notes WHERE id = ? AND user_id = ?',
      [noteId, userId]
    );

    if (!note) {
      return res.status(404).json({ message: 'Không tìm thấy ghi chú ngữ pháp' });
    }

    res.json(note);
  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

//Thêm ghi chú ngữ pháp mới
exports.createGrammarNote = async (req, res) => {
  const { title, formula, explanation, example, common_mistake } = req.body;
  const userId = req.user.id;

  if (!title) {
    return res.status(400).json({ message: 'Tiêu đề không được để trống' });
  }

  try {
    const [[existing]] = await db.execute(
      'SELECT id FROM grammar_notes WHERE title = ? AND user_id = ?',
      [title, userId]
    );

    if (existing) {
      return res.status(400).json({ 
        message: 'Bạn đã tạo điểm ngữ pháp này trước đó rồi.' 
      });
    }
    const [result] = await db.execute(
      'INSERT INTO grammar_notes (user_id, title, formula, explanation, example, common_mistake) VALUES (?, ?, ?, ?, ?, ?)',
      [
        userId,
        title,
        formula || null,
        explanation || null,
        example || null,
        common_mistake || null
      ]
    );

    res.status(201).json({
      message: 'Tạo ghi chú ngữ pháp thành công',
      note: {
        id: result.insertId,
        title,
        formula,
        explanation,
        example,
        common_mistake
      }
    });
  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

//Cập nhật ghi chú ngữ pháp
exports.updateGrammarNote = async (req, res) => {
  const { title, formula, explanation, example, common_mistake } = req.body;
  const userId = req.user.id;
  const noteId = req.params.id;

  if (!title) {
    return res.status(400).json({ message: 'Tiêu đề không được để trống' });
  }

  try {
    // Kiểm tra quyền sở hữu trước khi cập nhật
    const [[existingNote]] = await db.execute(
      'SELECT id FROM grammar_notes WHERE id = ? AND user_id = ?',
      [noteId, userId]
    );

    if (!existingNote) {
      return res.status(404).json({ message: 'Không tìm thấy ghi chú để cập nhật' });
    }

    await db.execute(
      'UPDATE grammar_notes SET title = ?, formula = ?, explanation = ?, example = ?, common_mistake = ? WHERE id = ? AND user_id = ?',
      [
        title,
        formula || null,
        explanation || null,
        example || null,
        common_mistake || null,
        noteId,
        userId
      ]
    );

    res.json({ message: 'Cập nhật ghi chú ngữ pháp thành công' });
  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

//Xóa ghi chú ngữ pháp
exports.deleteGrammarNote = async (req, res) => {
  const userId = req.user.id;
  const noteId = req.params.id;

  try {
    const [[existingNote]] = await db.execute(
      'SELECT id FROM grammar_notes WHERE id = ? AND user_id = ?',
      [noteId, userId]
    );

    if (!existingNote) {
      return res.status(404).json({ message: 'Không tìm thấy ghi chú để xóa' });
    }

    await db.execute(
      'DELETE FROM grammar_notes WHERE id = ? AND user_id = ?',
      [noteId, userId]
    );

    res.json({ message: 'Xóa ghi chú ngữ pháp thành công' });
  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};