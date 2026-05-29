const db = require('../config/db');

// GET /api/progress
exports.getProgress = async (req, res) => {
  try {
    const userId = req.user.id;

    // Tổng từ đã học
    const [[wordStats]] = await db.execute(
      `SELECT
        COUNT(w.id)                          AS total_words,
        SUM(w.interval_days > 1)             AS learned_words
       FROM words w
       JOIN word_sets ws ON ws.id = w.word_set_id
       WHERE ws.user_id = ?`,
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

    // Tính streak
    const [progressRows] = await db.execute(
      `SELECT study_date FROM user_progress
       WHERE user_id = ? AND words_studied > 0
       ORDER BY study_date DESC`,
      [userId]
    );

    const streak = calculateStreak(progressRows);

    // Số từ cần ôn hôm nay
    const [[reviewToday]] = await db.execute(
      `SELECT COUNT(w.id) AS count
       FROM words w
       JOIN word_sets ws ON ws.id = w.word_set_id
       WHERE ws.user_id = ?
         AND w.next_review_date <= CURDATE()`,
      [userId]
    );

    // Hoạt động 7 ngày gần nhất (cho biểu đồ)
    const [dailyActivity] = await db.execute(
      `SELECT
        study_date,
        words_studied,
        correct_count
       FROM user_progress
       WHERE user_id = ?
         AND study_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
       ORDER BY study_date ASC`,
      [userId]
    );

    const totalStudied = accuracy.total_studied || 0;
    const totalCorrect = accuracy.total_correct || 0;
    const accuracyRate = totalStudied > 0
      ? Math.round((totalCorrect / totalStudied) * 100)
      : 0;

    res.json({
      streak,
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