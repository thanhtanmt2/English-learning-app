console.log('Script bắt đầu chạy...');
const fs   = require('fs');
const csv  = require('csv-parser');
const db   = require('./db');
const bcrypt = require('bcryptjs');
const path = require('path');

async function seed() {
  console.log('Bắt đầu seed data...\n');

  // 1. Tạo user demo
  const hashed = await bcrypt.hash('demo123', 10);
  const [userResult] = await db.execute(
    'INSERT IGNORE INTO users (name, email, password, goal) VALUES (?, ?, ?, ?)',
    ['Demo User', 'demo@minlish.com', hashed, 'general']
  );

  // Lấy userId — dù user đã tồn tại hay mới tạo
  const [[user]] = await db.execute(
    'SELECT id FROM users WHERE email = ?',
    ['demo@minlish.com']
  );
  const userId = user.id;
  console.log(`✓ Demo user: demo@minlish.com / demo123 (id: ${userId})\n`);

  // 2. Đọc CSV, gom theo Topic
  const wordsByTopic = {};

  await new Promise((resolve, reject) => {
    // Sử dụng stream mặc định và để csv-parser tự xử lý BOM
    fs.createReadStream(path.join(__dirname, '../data/vocab.csv'))
      .pipe(csv({ bom: true }))
      .on('data', (row) => {
        // Hàm lọc bỏ ký tự lạ rác
        const cleanStr = (str) => str ? str.replace(/[\uFFFD\uFFFE\uFEFF]/g, "") : "";

        const topic = cleanStr(row['Topic']?.trim());
        if (!topic) return;
        if (!wordsByTopic[topic]) wordsByTopic[topic] = [];
        wordsByTopic[topic].push(row);
      })
      .on('end', resolve)
      .on('error', reject);
  });

  // 3. Tạo Word Set + insert từng từ
  for (const [topic, words] of Object.entries(wordsByTopic)) {

    // Tạo word set
    const [setResult] = await db.execute(
      'INSERT INTO word_sets (user_id, name, description) VALUES (?, ?, ?)',
      [userId, topic, `Từ vựng chủ đề: ${topic}`]
    );
    const wordSetId = setResult.insertId;

    // Insert từng từ
    for (const w of words) {
      const clean = (val) => (val === '-' || val === '' ? null : val?.trim());

      await db.execute(
        `INSERT INTO words (
          word_set_id, word, pronunciation, meaning, example,
          part_of_speech, v2_past, v3_past_participle, plural_form,
          description, collocation, synonyms, antonyms,
          cefr_level, topic, quiz_question
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
        [
          wordSetId,
          clean(w['English']),
          clean(w['IPA']),
          clean(w['Vietnamese']),
          clean(w['Example Sentence']),
          clean(w['Part of Speech']),
          clean(w['V2 (Past)']),
          clean(w['V3 (Past Participle)']),
          clean(w['S/ES']),
          clean(w['Description']),
          clean(w['Collocation']),
          clean(w['Synonyms']),
          clean(w['Antonyms']),
          clean(w['CEFR Level']),
          clean(w['Topic']),
          clean(w['Quiz Question']),
        ]
      );
    }

    console.log(`✓ ${topic.padEnd(30)} → ${words.length} từ`);
  }

  const total = Object.values(wordsByTopic).reduce((s, a) => s + a.length, 0);
  console.log(`\n✅ Seed xong! Tổng: ${total} từ, ${Object.keys(wordsByTopic).length} bộ từ`);

  // 4. Đọc CSV Grammar và Import Ngữ pháp
  const grammarNotes = [];
  const grammarFilePath = path.join(__dirname, '../data/grammar.csv');

  if (fs.existsSync(grammarFilePath)) {
    await new Promise((resolve, reject) => {
      fs.createReadStream(grammarFilePath)
        .pipe(csv({ bom: true }))
        .on('data', (row) => {
          // Xử lý BOM có thể dính vào tên cột đầu tiên
          const grammarKey = Object.keys(row).find(k => k.includes('Grammar Point'));
          if (grammarKey && row[grammarKey]?.trim()) {
            // Lưu lại key chuẩn để xài sau
            row['Grammar Point'] = row[grammarKey];
            grammarNotes.push(row);
          }
        })
        .on('end', resolve)
        .on('error', reject);
    });

    console.log('Bắt đầu thêm Ngữ pháp...');
    const clean = (val) => (val === '-' || val === '' ? null : val?.trim());
    for (const note of grammarNotes) {
      const grammarPoint = note['Grammar Point']?.trim();
      const sentenceType = note['Sentence Type']?.trim();

      const title = sentenceType ? `${grammarPoint} (${sentenceType})` : grammarPoint;

      await db.execute(
        'INSERT INTO grammar_notes (user_id, title, formula, explanation, example, common_mistake) VALUES (?, ?, ?, ?, ?, ?)',
        [
          userId,
          title,
          clean(note['Structure']),      
          clean(note['Meaning (VI)']),  
          clean(note['Grammar Example']),
          null                           
        ]
      );
    }
    console.log(`✓ Đã seed thành công ${grammarNotes.length} chủ điểm ngữ pháp.\n`);
  } else {
    console.log('⚠ Không tìm thấy file grammar.csv, bỏ qua bước seed ngữ pháp.\n');
  }

  process.exit(0);
}

seed().catch((err) => {
  console.error('Lỗi seed:', err.message);
  process.exit(1);
});