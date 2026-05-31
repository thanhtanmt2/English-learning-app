const db = require('../config/db');

// GET /api/grammar
exports.getGrammar = async (req, res) => {
  try {
    const [rows] = await db.execute(`
      SELECT g.*, p.highest_score, p.total_questions 
      FROM grammar_notes g
      LEFT JOIN user_grammar_progress p 
        ON g.id = p.grammar_note_id AND p.user_id = ?
      WHERE g.user_id = ? OR g.user_id = 1 
      ORDER BY g.created_at DESC
    `, [req.user.id, req.user.id]);
    const mappedRows = rows.map(r => ({
      id: r.id.toString(),
      title: r.title,
      category: "Grammar",
      level: "B1",
      formula: r.formula || "",
      explanation: r.explanation || "",
      example: r.example || "",
      commonMistakes: r.common_mistake || "",
      highestScore: r.highest_score,
      totalQuestions: r.total_questions
    }));
    res.json(mappedRows);
  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

// POST /api/grammar
exports.addGrammar = async (req, res) => {
  const { title, formula, explanation, example, commonMistakes } = req.body;
  if (!title) return res.status(400).json({ message: 'title là bắt buộc' });

  try {
    const [result] = await db.execute(
      `INSERT INTO grammar_notes (user_id, title, formula, explanation, example, common_mistake)
       VALUES (?, ?, ?, ?, ?, ?)`,
      [req.user.id, title, formula || '', explanation || '', example || '', commonMistakes || '']
    );
    res.status(201).json({
      id: result.insertId.toString(),
      title,
      category: "Grammar",
      level: "B1",
      formula: formula || "",
      explanation: explanation || "",
      example: example || "",
      commonMistakes: commonMistakes || ""
    });
  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

// GET /api/quiz_questions (nhận query param noteId)
exports.getQuizzes = async (req, res) => {
  const { noteId } = req.query;
  try {
    let query = 'SELECT * FROM grammar_quizzes';
    let params = [];

    if (noteId) {
      query += ' WHERE grammar_note_id = ?';
      params.push(noteId);
    }
    
    query += ' ORDER BY RAND() LIMIT 10';

    const [rows] = await db.execute(query, params);

    const quizzes = rows.map(r => ({
      id: r.id.toString(),
      question: r.question,
      options: [r.option_a, r.option_b, r.option_c, r.option_d],
      correctAnswer: r.correct_answer,
      explanation: r.explanation
    }));

    res.json(quizzes);
  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

// POST /api/grammar/:id/score
exports.submitQuizScore = async (req, res) => {
  const noteId = req.params.id;
  const { score, total } = req.body;
  const userId = req.user.id;
  
  try {
    const [existing] = await db.execute(
      'SELECT highest_score FROM user_grammar_progress WHERE user_id = ? AND grammar_note_id = ?',
      [userId, noteId]
    );
    
    if (existing.length > 0) {
      if (score > existing[0].highest_score) {
        await db.execute(
          'UPDATE user_grammar_progress SET highest_score = ?, total_questions = ? WHERE user_id = ? AND grammar_note_id = ?',
          [score, total, userId, noteId]
        );
      }
    } else {
      await db.execute(
        'INSERT INTO user_grammar_progress (user_id, grammar_note_id, highest_score, total_questions) VALUES (?, ?, ?, ?)',
        [userId, noteId, score, total]
      );
    }
    res.json({ message: 'Score updated successfully' });
  } catch(err) {
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
