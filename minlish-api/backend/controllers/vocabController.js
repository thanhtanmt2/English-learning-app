const db = require('../config/db');

// GET /api/wordsets
exports.getWordSets = async (req, res) => {
  try {
    // Lấy bộ từ của chính User HOẶC bộ từ của Demo User (id=1)
    const [rows] = await db.execute(
      'SELECT * FROM word_sets WHERE user_id = ? OR user_id = 1 ORDER BY created_at DESC',
      [req.user.id]
    );
    res.json(rows);
  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

// POST /api/wordsets
exports.createWordSet = async (req, res) => {
  const { name, description } = req.body;
  if (!name) return res.status(400).json({ message: 'Tên bộ từ là bắt buộc' });

  try {
    const [result] = await db.execute(
      'INSERT INTO word_sets (user_id, name, description) VALUES (?, ?, ?)',
      [req.user.id, name, description || '']
    );
    res.status(201).json({ id: result.insertId, name, description, user_id: req.user.id });
  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

// GET /api/wordsets/:id/words
exports.getWordsBySet = async (req, res) => {
  const setId = req.params.id;
  try {
    // Cho phép lấy từ nếu bộ từ đó của chính user HOẶC của Demo User (id=1)
    const [[wordSet]] = await db.execute(
      'SELECT * FROM word_sets WHERE id = ? AND (user_id = ? OR user_id = 1)',
      [setId, req.user.id]
    );

    if (!wordSet) return res.status(404).json({ message: 'Không tìm thấy bộ từ hoặc không có quyền truy cập' });

    const [words] = await db.execute('SELECT * FROM words WHERE word_set_id = ?', [setId]);
    res.json(words);
  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

// POST /api/words
exports.addWord = async (req, res) => {
  const { word_set_id, word, meaning, pronunciation, example } = req.body;
  if (!word_set_id || !word || !meaning) {
    return res.status(400).json({ message: 'word_set_id, word và meaning là bắt buộc' });
  }

  try {
    // Kiểm tra quyền
    const [[wordSet]] = await db.execute('SELECT id FROM word_sets WHERE id = ? AND user_id = ?', [word_set_id, req.user.id]);
    if (!wordSet) return res.status(403).json({ message: 'Không có quyền thêm từ vào bộ này' });

    const [result] = await db.execute(
      'INSERT INTO words (word_set_id, word, pronunciation, meaning, example) VALUES (?, ?, ?, ?, ?)',
      [word_set_id, word, pronunciation || '', meaning, example || '']
    );
    res.status(201).json({ id: result.insertId, word_set_id, word, pronunciation, meaning, example });
  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

// PUT /api/words/:id
exports.updateWord = async (req, res) => {
  const wordId = req.params.id;
  const { word, meaning, pronunciation, example } = req.body;

  try {
    // Check ownership by joining with word_sets
    const [[existingWord]] = await db.execute(
      `SELECT w.id FROM words w JOIN word_sets ws ON w.word_set_id = ws.id WHERE w.id = ? AND ws.user_id = ?`,
      [wordId, req.user.id]
    );
    if (!existingWord) return res.status(404).json({ message: 'Không tìm thấy từ hoặc không có quyền' });

    await db.execute(
      'UPDATE words SET word = ?, meaning = ?, pronunciation = ?, example = ? WHERE id = ?',
      [word, meaning, pronunciation || '', example || '', wordId]
    );
    res.json({ message: 'Cập nhật từ thành công' });
  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

// DELETE /api/words/:id
exports.deleteWord = async (req, res) => {
  const wordId = req.params.id;
  try {
    const [[existingWord]] = await db.execute(
      `SELECT w.id FROM words w JOIN word_sets ws ON w.word_set_id = ws.id WHERE w.id = ? AND ws.user_id = ?`,
      [wordId, req.user.id]
    );
    if (!existingWord) return res.status(404).json({ message: 'Không tìm thấy từ hoặc không có quyền' });

    await db.execute('DELETE FROM words WHERE id = ?', [wordId]);
    res.json({ message: 'Xóa từ thành công' });
  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

// GET /api/words/review (Lấy từ cần ôn hôm nay dựa trên SM-2)
exports.getWordsToReview = async (req, res) => {
  try {
    const [words] = await db.execute(
      `SELECT w.* FROM words w JOIN word_sets ws ON w.word_set_id = ws.id 
       WHERE ws.user_id = ? AND w.next_review_date <= CURRENT_DATE()`,
      [req.user.id]
    );
    res.json(words);
  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

// POST /api/words/:id/review (Cập nhật SM-2)
exports.reviewWord = async (req, res) => {
  const wordId = req.params.id;
  const { quality } = req.body; // Điểm từ 0 đến 5

  if (quality === undefined || quality < 0 || quality > 5) {
    return res.status(400).json({ message: 'quality (0-5) là bắt buộc' });
  }

  try {
    const [[word]] = await db.execute(
      `SELECT w.* FROM words w JOIN word_sets ws ON w.word_set_id = ws.id WHERE w.id = ? AND (ws.user_id = ? OR ws.user_id = 1)`,
      [wordId, req.user.id]
    );
    if (!word) return res.status(404).json({ message: 'Không tìm thấy từ' });

    let easeFactor = word.ease_factor;
    let interval = word.interval_days;

    // SM-2 logic
    if (quality >= 3) {
      if (interval === 0) {
        interval = 1;
      } else if (interval === 1) {
        interval = 6;
      } else {
        interval = Math.round(interval * easeFactor);
      }
    } else {
      interval = 1; // Học lại từ đầu
    }

    easeFactor = easeFactor + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02));
    if (easeFactor < 1.3) easeFactor = 1.3;

    await db.execute(
      `UPDATE words SET ease_factor = ?, interval_days = ?, next_review_date = DATE_ADD(CURRENT_DATE(), INTERVAL ? DAY) WHERE id = ?`,
      [easeFactor, interval, interval, wordId]
    );
    
    // Lưu vào user_progress (tăng số từ đã ôn)
    // Chuyển múi giờ về Việt Nam để study_date luôn khớp
    const today = new Date().toLocaleDateString('en-CA'); // Trả về định dạng YYYY-MM-DD
    const isCorrect = quality >= 3 ? 1 : 0;

    console.log(`[Progress Update] User: ${req.user.id}, Date: ${today}, Correct: ${isCorrect}`);

    await db.execute(
      `INSERT INTO user_progress (user_id, study_date, words_studied, correct_count) 
       VALUES (?, ?, 1, ?) 
       ON DUPLICATE KEY UPDATE words_studied = words_studied + 1, correct_count = correct_count + ?`,
      [req.user.id, today, isCorrect, isCorrect]
    );

    res.json({ message: 'Đã cập nhật tiến độ ôn tập', interval, easeFactor });
  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};