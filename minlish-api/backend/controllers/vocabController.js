const db = require('../config/db');

// ─── SM-2 Algorithm ───────────────────────────────────────────
function calculateSM2(quality, easeFactor, intervalDays) {
  // quality: 0=Again, 1=Hard, 2=Good, 3=Easy
  let newEF       = easeFactor;
  let newInterval = intervalDays;

  if (quality === 0) {
    // Quên hoàn toàn → reset
    newInterval = 1;
    newEF       = Math.max(1.3, easeFactor - 0.2);

  } else if (quality === 1) {
    // Nhớ mơ hồ → tăng nhẹ, giảm EF
    newInterval = Math.ceil(intervalDays * 1.2);
    newEF       = Math.max(1.3, easeFactor - 0.15);

  } else if (quality === 2) {
    // Nhớ ổn → tăng theo EF
    newInterval = Math.ceil(intervalDays * easeFactor);

  } else if (quality === 3) {
    // Nhớ rất chắc → tăng nhiều hơn
    newInterval = Math.ceil(intervalDays * easeFactor * 1.3);
    newEF       = Math.min(3.0, easeFactor + 0.1);
  }

  // Tính ngày ôn tiếp theo
  const nextDate = new Date();
  nextDate.setDate(nextDate.getDate() + newInterval);

  return {
    easeFactor:     parseFloat(newEF.toFixed(2)),
    intervalDays:   newInterval,
    nextReviewDate: nextDate.toISOString().split('T')[0], // YYYY-MM-DD
  };
}

// ─── Word Sets ────────────────────────────────────────────────

// GET /api/wordsets
exports.getWordSets = async (req, res) => {
  try {
    const [rows] = await db.execute(
      `SELECT ws.*, COUNT(w.id) AS word_count
       FROM word_sets ws
       LEFT JOIN words w ON w.word_set_id = ws.id
       WHERE ws.user_id = ?
       GROUP BY ws.id
       ORDER BY ws.created_at DESC`,
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
  if (!name) return res.status(400).json({ message: 'Tên bộ từ không được trống' });

  try {
    const [result] = await db.execute(
      'INSERT INTO word_sets (user_id, name, description) VALUES (?, ?, ?)',
      [req.user.id, name, description || null]
    );
    res.status(201).json({
      message: 'Tạo bộ từ thành công',
      wordSet: { id: result.insertId, name, description }
    });
  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

// ─── Words ────────────────────────────────────────────────────

// GET /api/wordsets/:id/words
exports.getWords = async (req, res) => {
  try {
    // Kiểm tra word set thuộc user này không
    const [[wordSet]] = await db.execute(
      'SELECT id FROM word_sets WHERE id = ? AND user_id = ?',
      [req.params.id, req.user.id]
    );
    if (!wordSet) return res.status(404).json({ message: 'Không tìm thấy bộ từ' });

    const [words] = await db.execute(
      'SELECT * FROM words WHERE word_set_id = ? ORDER BY created_at ASC',
      [req.params.id]
    );
    res.json(words);
  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

// POST /api/words
exports.addWord = async (req, res) => {
  const { word_set_id, word, pronunciation, meaning, example } = req.body;
  if (!word_set_id || !word || !meaning) {
    return res.status(400).json({ message: 'Thiếu thông tin bắt buộc' });
  }

  try {
    // Kiểm tra word set thuộc user này không
    const [[wordSet]] = await db.execute(
      'SELECT id FROM word_sets WHERE id = ? AND user_id = ?',
      [word_set_id, req.user.id]
    );
    if (!wordSet) return res.status(404).json({ message: 'Không tìm thấy bộ từ' });

    const [result] = await db.execute(
      `INSERT INTO words (word_set_id, word, pronunciation, meaning, example)
       VALUES (?, ?, ?, ?, ?)`,
      [word_set_id, word, pronunciation || null, meaning, example || null]
    );
    res.status(201).json({
      message: 'Thêm từ thành công',
      word: { id: result.insertId, word, meaning }
    });
  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

// ─── SRS ─────────────────────────────────────────────────────

// GET /api/words/review  → từ cần ôn hôm nay
exports.getReviewWords = async (req, res) => {
  try {
    const [words] = await db.execute(
      `SELECT w.*
       FROM words w
       JOIN word_sets ws ON ws.id = w.word_set_id
       WHERE ws.user_id = ?
         AND w.next_review_date <= CURDATE()
       ORDER BY w.next_review_date ASC
       LIMIT 50`,
      [req.user.id]
    );

    res.json({
      total: words.length,
      words
    });
  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

// POST /api/words/:id/review  → gửi kết quả học
exports.submitReview = async (req, res) => {
  const { quality } = req.body; // 0=Again, 1=Hard, 2=Good, 3=Easy

  if (quality === undefined || quality < 0 || quality > 3) {
    return res.status(400).json({ message: 'quality phải là 0, 1, 2 hoặc 3' });
  }

  try {
    // Kiểm tra từ thuộc user này không
    const [[word]] = await db.execute(
      `SELECT w.* FROM words w
       JOIN word_sets ws ON ws.id = w.word_set_id
       WHERE w.id = ? AND ws.user_id = ?`,
      [req.params.id, req.user.id]
    );
    if (!word) return res.status(404).json({ message: 'Không tìm thấy từ' });

    // Tính SM-2
    const { easeFactor, intervalDays, nextReviewDate } = calculateSM2(
      quality,
      word.ease_factor,
      word.interval_days
    );

    // Cập nhật từ
    await db.execute(
      `UPDATE words
       SET ease_factor = ?, interval_days = ?, next_review_date = ?
       WHERE id = ?`,
      [easeFactor, intervalDays, nextReviewDate, word.id]
    );

    // Cập nhật tiến độ hôm nay
    const isCorrect = quality >= 2 ? 1 : 0;
    await db.execute(
      `INSERT INTO user_progress (user_id, study_date, words_studied, correct_count)
       VALUES (?, CURDATE(), 1, ?)
       ON DUPLICATE KEY UPDATE
         words_studied = words_studied + 1,
         correct_count = correct_count + ?`,
      [req.user.id, isCorrect, isCorrect]
    );

    res.json({
      message: 'Đã lưu kết quả',
      result: { easeFactor, intervalDays, nextReviewDate }
    });

  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};