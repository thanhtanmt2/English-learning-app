const db = require('../config/db');

function normalizeBooleans(row) {
  return {
    ...row,
    daily_reminder: Boolean(row.daily_reminder),
    quiz_reminders: Boolean(row.quiz_reminders),
    progress_updates: Boolean(row.progress_updates),
  };
}

// GET /api/notifications/settings
exports.getSettings = async (req, res) => {
  const userId = req.user.id;
  try {
    const [rows] = await db.execute(
      'SELECT * FROM notification_settings WHERE user_id = ?',
      [userId]
    );

    if (rows.length === 0) {
      // Tạo settings mặc định nếu chưa có
      await db.execute(
        'INSERT INTO notification_settings (user_id) VALUES (?)',
        [userId]
      );
      const [created] = await db.execute(
        'SELECT * FROM notification_settings WHERE user_id = ?',
        [userId]
      );
      return res.json(normalizeBooleans(created[0]));
    }

    res.json(normalizeBooleans(rows[0]));
  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};

// PUT /api/notifications/settings
exports.updateSettings = async (req, res) => {
  const userId = req.user.id;
  const { daily_reminder, reminder_time, quiz_reminders, progress_updates } = req.body;

  try {
    await db.execute(
      `INSERT INTO notification_settings (user_id, daily_reminder, reminder_time, quiz_reminders, progress_updates)
       VALUES (?, ?, ?, ?, ?)
       ON DUPLICATE KEY UPDATE
         daily_reminder = VALUES(daily_reminder),
         reminder_time = VALUES(reminder_time),
         quiz_reminders = VALUES(quiz_reminders),
         progress_updates = VALUES(progress_updates)`,
      [userId, daily_reminder ?? true, reminder_time ?? '08:00:00', quiz_reminders ?? true, progress_updates ?? true]
    );

    const [updated] = await db.execute(
      'SELECT * FROM notification_settings WHERE user_id = ?',
      [userId]
    );
    res.json(normalizeBooleans(updated[0]));
  } catch (err) {
    res.status(500).json({ message: 'Lỗi server', error: err.message });
  }
};
