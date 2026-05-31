const db = require('../config/db');

// GET /api/progress
exports.getProgress = async (req, res) => {
  try {
    const userId = req.user.id;

    // Tổng từ đã học (bao gồm cả bộ từ dùng chung)
    const [[wordStats]] = await db.execute(
      `SELECT
        COUNT(w.id)                          AS total_words,
        SUM(w.interval_days > 1)             AS learned_words
       FROM words w
       JOIN word_sets ws ON ws.id = w.word_set_id
       WHERE ws.user_id = ? OR ws.user_id = 1`,
      [userId]
    );

    // Số từ cần ôn hôm nay
    const [[reviewToday]] = await db.execute(
      `SELECT COUNT(*) as count FROM words w
       JOIN word_sets ws ON w.word_set_id = ws.id
       WHERE (ws.user_id = ? OR ws.user_id = 1)
         AND w.next_review_date <= CURRENT_DATE()`,
      [userId]
    );

    // Tỉ lệ chính xác 7 ngày gần nhất
    const [[accuracy]] = await db.execute(
      `SELECT
        SUM(words_studied)   AS total_studied,
        SUM(correct_count)   AS total_correct
       FROM user_progress
       WHERE user_id = ?
         AND study_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)`,
      [userId]
    );

    const totalStudied = parseInt(accuracy.total_studied) || 0;
    const totalCorrect = parseInt(accuracy.total_correct) || 0;

    // Ép kiểu sang float trước khi chia để tránh lỗi integer division
    const accuracyRate = totalStudied > 0
      ? Math.round((totalCorrect / totalStudied) * 100)
      : 0;

    // Lấy streak thực tế
    const [progressRows] = await db.execute(
      `SELECT study_date FROM user_progress
       WHERE user_id = ? AND words_studied > 0
       ORDER BY study_date DESC`,
      [userId]
    );
    const currentStreak = calculateStreak(progressRows);

    // Lấy lịch sử (phải trả về danh sách thì App mới vẽ được biểu đồ)
    const [historyRows] = await db.execute(
      `SELECT
        DATE_FORMAT(study_date, '%Y-%m-%d') as date,
        words_studied as wordsLearned,
        correct_count as quizScore
       FROM user_progress
       WHERE user_id = ?
       ORDER BY study_date ASC`,
      [userId]
    );

    const dailyActivity = historyRows.map(r => ({
      ...r,
      id: r.date,
      userId: userId.toString(),
      grammarCompleted: 0,
      studyTimeMinutes: Math.round(r.wordsLearned * 1.5)
    }));

    res.json({
      streak: currentStreak,
      total_words:    wordStats.total_words    || 0,
      learned_words:  wordStats.learned_words  || 0,
      review_today:   reviewToday.count        || 0,
      accuracy:       accuracyRate,
      daily_activity: dailyActivity
    });

  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

// GET /api/progress_history
exports.getProgressHistory = async (req, res) => {
  try {
    const [rows] = await db.execute(
      `SELECT id, study_date AS date, words_studied AS wordsLearned,
              correct_count AS quizScore
       FROM user_progress
       WHERE user_id = ?
       ORDER BY study_date DESC`,
      [req.user.id]
    );

    // Map thêm các trường còn thiếu mà model Android yêu cầu
    const history = rows.map(r => ({
      ...r,
      userId: req.user.id.toString(),
      date: new Date(r.date).toISOString().split('T')[0],
      grammarCompleted: 0,
      studyTimeMinutes: Math.round(r.wordsLearned * 1.5) // Giả định 1.5 phút/từ
    }));

    res.json(history);
  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

// Tính streak liên tiếp
function calculateStreak(rows) {
  if (rows.length === 0) return 0;

  let streak = 0;
  const today = new Date();
  today.setHours(0, 0, 0, 0);

  for (let i = 0; i < rows.length; i++) {
    const studyDate = new Date(rows[i].study_date);
    studyDate.setHours(0, 0, 0, 0);

    const expected = new Date(today);
    expected.setDate(today.getDate() - i);

    if (studyDate.getTime() === expected.getTime()) {
      streak++;
    } else {
      break; // Chuỗi bị đứt
    }
  }

  return streak;
}