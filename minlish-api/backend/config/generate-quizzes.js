// Script tự động sinh 5 câu trắc nghiệm cho mỗi bài ngữ pháp
// Chạy: node config/generate-quizzes.js
const mysql = require('mysql2/promise');
require('dotenv').config();

// ===== NGÂN HÀNG CÂU HỎI THEO GRAMMAR POINT =====
// Mỗi grammar point có 5 câu hỏi thực tế
const quizBank = {
  'Present Simple': [
    { q: 'She ___ to school every day.', a: 'goes', b: 'go', c: 'going', d: 'gone', correct: 'goes', exp: 'Chủ ngữ "She" (ngôi 3 số ít) → V thêm -es: goes' },
    { q: 'They ___ coffee in the morning.', a: 'drinks', b: 'drink', c: 'drinking', d: 'drank', correct: 'drink', exp: '"They" là ngôi 3 số nhiều → V nguyên mẫu' },
    { q: '___ he play football on weekends?', a: 'Do', b: 'Does', c: 'Is', d: 'Are', correct: 'Does', exp: 'Câu hỏi ngôi 3 số ít dùng "Does"' },
    { q: 'Water ___ at 100 degrees Celsius.', a: 'boil', b: 'boils', c: 'boiling', d: 'boiled', correct: 'boils', exp: 'Sự thật hiển nhiên → Present Simple, "Water" ngôi 3 → boils' },
    { q: 'I ___ not like spicy food.', a: 'do', b: 'does', c: 'am', d: 'have', correct: 'do', exp: '"I" dùng trợ động từ "do" trong câu phủ định' },
  ],
  'Present Continuous': [
    { q: 'Look! The children ___ in the park.', a: 'play', b: 'plays', c: 'are playing', d: 'played', correct: 'are playing', exp: '"Look!" → hành động đang xảy ra → Present Continuous' },
    { q: 'She ___ a book right now.', a: 'reads', b: 'is reading', c: 'read', d: 'has read', correct: 'is reading', exp: '"right now" → đang diễn ra → is + V-ing' },
    { q: 'We ___ dinner at the moment.', a: 'cook', b: 'cooks', c: 'are cooking', d: 'cooked', correct: 'are cooking', exp: '"at the moment" → Present Continuous' },
    { q: '___ you listening to me?', a: 'Do', b: 'Are', c: 'Does', d: 'Have', correct: 'Are', exp: 'Câu hỏi Present Continuous: Am/Is/Are + S + V-ing?' },
    { q: 'He ___ not working today.', a: 'does', b: 'is', c: 'has', d: 'do', correct: 'is', exp: 'Phủ định Present Continuous: S + is/am/are + not + V-ing' },
  ],
  'Present Perfect': [
    { q: 'I ___ already finished my homework.', a: 'am', b: 'was', c: 'have', d: 'had', correct: 'have', exp: '"already" → Present Perfect: have/has + V3' },
    { q: 'She ___ never been to Paris.', a: 'have', b: 'has', c: 'is', d: 'was', correct: 'has', exp: '"She" ngôi 3 → dùng "has" + V3' },
    { q: '___ you ever eaten sushi?', a: 'Do', b: 'Have', c: 'Did', d: 'Are', correct: 'Have', exp: '"ever" → Present Perfect → Have + S + V3?' },
    { q: 'They ___ lived here since 2010.', a: 'has', b: 'have', c: 'are', d: 'were', correct: 'have', exp: '"since 2010" → Present Perfect, "They" → have' },
    { q: 'He has ___ three cups of tea today.', a: 'drink', b: 'drinks', c: 'drunk', d: 'drank', correct: 'drunk', exp: 'have/has + V3 (past participle): drunk' },
  ],
  'Present Perfect Continuous': [
    { q: 'She ___ been studying for 3 hours.', a: 'have', b: 'has', c: 'is', d: 'was', correct: 'has', exp: '"She" → has been + V-ing' },
    { q: 'They have been ___ all morning.', a: 'work', b: 'works', c: 'working', d: 'worked', correct: 'working', exp: 'have been + V-ing' },
    { q: 'How long ___ you been waiting?', a: 'do', b: 'have', c: 'are', d: 'did', correct: 'have', exp: 'Câu hỏi: Have/Has + S + been + V-ing?' },
    { q: 'I have been ___ English since 2020.', a: 'learn', b: 'learns', c: 'learning', d: 'learned', correct: 'learning', exp: 'have been + V-ing → learning' },
    { q: 'It ___ been raining since this morning.', a: 'have', b: 'has', c: 'is', d: 'was', correct: 'has', exp: '"It" ngôi 3 → has been + V-ing' },
  ],
  'Past Simple': [
    { q: 'She ___ to the market yesterday.', a: 'goes', b: 'go', c: 'went', d: 'gone', correct: 'went', exp: '"yesterday" → Past Simple: went (V2 của go)' },
    { q: 'They ___ not come to the party last night.', a: 'do', b: 'did', c: 'have', d: 'are', correct: 'did', exp: 'Phủ định Past Simple: did + not + V' },
    { q: '___ you see the movie last week?', a: 'Do', b: 'Did', c: 'Have', d: 'Are', correct: 'Did', exp: 'Câu hỏi Past Simple: Did + S + V?' },
    { q: 'He ___ a letter to his friend.', a: 'write', b: 'writes', c: 'wrote', d: 'written', correct: 'wrote', exp: 'Past Simple: wrote (V2 của write)' },
    { q: 'We ___ in London in 2019.', a: 'live', b: 'lives', c: 'lived', d: 'living', correct: 'lived', exp: '"in 2019" → Past Simple: lived' },
  ],
  'Past Continuous': [
    { q: 'I ___ sleeping when you called.', a: 'am', b: 'was', c: 'were', d: 'have been', correct: 'was', exp: '"I" + was + V-ing → Past Continuous' },
    { q: 'They ___ playing football at 5 PM yesterday.', a: 'was', b: 'were', c: 'are', d: 'have been', correct: 'were', exp: '"They" → were + V-ing' },
    { q: 'She was ___ when the phone rang.', a: 'cook', b: 'cooks', c: 'cooking', d: 'cooked', correct: 'cooking', exp: 'was + V-ing → cooking' },
    { q: 'What ___ you doing at 9 PM?', a: 'was', b: 'were', c: 'are', d: 'did', correct: 'were', exp: '"you" → Were + S + V-ing?' },
    { q: 'While I ___ reading, he was watching TV.', a: 'am', b: 'was', c: 'were', d: 'is', correct: 'was', exp: '"While" + Past Continuous → was reading' },
  ],
  'Past Perfect': [
    { q: 'She ___ already left before I arrived.', a: 'has', b: 'had', c: 'have', d: 'was', correct: 'had', exp: 'Hành động xảy ra trước → Past Perfect: had + V3' },
    { q: 'They had ___ dinner before the guests came.', a: 'eat', b: 'ate', c: 'eaten', d: 'eating', correct: 'eaten', exp: 'had + V3 → eaten' },
    { q: '___ he finished the work before 5 PM?', a: 'Has', b: 'Had', c: 'Did', d: 'Was', correct: 'Had', exp: 'Câu hỏi Past Perfect: Had + S + V3?' },
    { q: 'I had never ___ sushi before that day.', a: 'try', b: 'tries', c: 'tried', d: 'trying', correct: 'tried', exp: 'had + V3 → tried' },
    { q: 'By the time we arrived, the movie ___ started.', a: 'has', b: 'had', c: 'have', d: 'was', correct: 'had', exp: '"By the time" → Past Perfect: had + V3' },
  ],
  'Past Perfect Continuous': [
    { q: 'She had been ___ for 2 hours before it stopped.', a: 'run', b: 'runs', c: 'running', d: 'ran', correct: 'running', exp: 'had been + V-ing → running' },
    { q: 'They ___ been waiting for an hour when the bus came.', a: 'has', b: 'had', c: 'have', d: 'was', correct: 'had', exp: 'Past Perfect Continuous: had been + V-ing' },
    { q: 'How long had you been ___ before you graduated?', a: 'study', b: 'studies', c: 'studying', d: 'studied', correct: 'studying', exp: 'had been + V-ing → studying' },
    { q: 'He had been ___ all day, so he was tired.', a: 'work', b: 'works', c: 'working', d: 'worked', correct: 'working', exp: 'had been + V-ing → working' },
    { q: 'It ___ been snowing for hours before we went out.', a: 'has', b: 'had', c: 'have', d: 'was', correct: 'had', exp: 'Past Perfect Continuous: had been + V-ing' },
  ],
  'Future Simple (will)': [
    { q: 'I ___ help you with your homework.', a: 'will', b: 'am', c: 'do', d: 'have', correct: 'will', exp: 'Quyết định tức thì → will + V' },
    { q: 'She ___ not come to the party tomorrow.', a: 'does', b: 'is', c: 'will', d: 'has', correct: 'will', exp: 'Phủ định: will + not + V' },
    { q: '___ you be at the meeting?', a: 'Do', b: 'Will', c: 'Are', d: 'Have', correct: 'Will', exp: 'Câu hỏi: Will + S + V?' },
    { q: 'It ___ rain tomorrow.', a: 'will', b: 'is', c: 'does', d: 'has', correct: 'will', exp: 'Dự đoán tương lai: will + V' },
    { q: 'They will ___ the project next week.', a: 'finish', b: 'finishes', c: 'finished', d: 'finishing', correct: 'finish', exp: 'will + V nguyên mẫu' },
  ],
  'Future (be going to)': [
    { q: 'I ___ going to study abroad next year.', a: 'will', b: 'am', c: 'have', d: 'do', correct: 'am', exp: '"I" → am going to + V' },
    { q: 'She is ___ to visit her parents.', a: 'go', b: 'goes', c: 'going', d: 'gone', correct: 'going', exp: 'is going to + V' },
    { q: 'They ___ going to buy a new car.', a: 'is', b: 'are', c: 'am', d: 'was', correct: 'are', exp: '"They" → are going to + V' },
    { q: '___ he going to travel to Japan?', a: 'Does', b: 'Is', c: 'Will', d: 'Has', correct: 'Is', exp: 'Câu hỏi: Is/Am/Are + S + going to + V?' },
    { q: 'We are not ___ to move to a new house.', a: 'go', b: 'goes', c: 'going', d: 'gone', correct: 'going', exp: 'Phủ định: am/is/are + not + going to + V' },
  ],
  'Future Continuous': [
    { q: 'This time tomorrow, I ___ be flying to Paris.', a: 'will', b: 'am', c: 'have', d: 'was', correct: 'will', exp: 'Future Continuous: will + be + V-ing' },
    { q: 'She will be ___ at the office at 3 PM.', a: 'work', b: 'works', c: 'working', d: 'worked', correct: 'working', exp: 'will be + V-ing → working' },
    { q: '___ you be waiting for me at the airport?', a: 'Do', b: 'Will', c: 'Are', d: 'Have', correct: 'Will', exp: 'Câu hỏi: Will + S + be + V-ing?' },
    { q: 'They will be ___ dinner when we arrive.', a: 'have', b: 'has', c: 'having', d: 'had', correct: 'having', exp: 'will be + V-ing → having' },
    { q: 'At 8 PM tonight, he ___ be watching TV.', a: 'will', b: 'is', c: 'was', d: 'has', correct: 'will', exp: 'Thời điểm tương lai cụ thể → will be + V-ing' },
  ],
  'Future Perfect': [
    { q: 'By next month, I ___ have finished the course.', a: 'am', b: 'will', c: 'was', d: 'do', correct: 'will', exp: 'Future Perfect: will + have + V3' },
    { q: 'She will have ___ the report by Friday.', a: 'submit', b: 'submits', c: 'submitted', d: 'submitting', correct: 'submitted', exp: 'will have + V3 → submitted' },
    { q: '___ they have arrived by 6 PM?', a: 'Do', b: 'Will', c: 'Have', d: 'Did', correct: 'Will', exp: 'Câu hỏi: Will + S + have + V3?' },
    { q: 'By 2030, he will have ___ here for 10 years.', a: 'live', b: 'lives', c: 'lived', d: 'living', correct: 'lived', exp: 'will have + V3 → lived' },
    { q: 'We will have ___ all the food by then.', a: 'eat', b: 'ate', c: 'eaten', d: 'eating', correct: 'eaten', exp: 'will have + V3 → eaten' },
  ],
  'Future Perfect Continuous': [
    { q: 'By next year, I will have been ___ for 5 years.', a: 'teach', b: 'teaches', c: 'teaching', d: 'taught', correct: 'teaching', exp: 'will have been + V-ing → teaching' },
    { q: 'She ___ have been working here for a decade by 2025.', a: 'is', b: 'will', c: 'has', d: 'was', correct: 'will', exp: 'Future Perfect Continuous: will + have been + V-ing' },
    { q: 'By the time he retires, he will have been ___ for 30 years.', a: 'work', b: 'works', c: 'working', d: 'worked', correct: 'working', exp: 'will have been + V-ing → working' },
    { q: 'How long will you have been ___ by graduation?', a: 'study', b: 'studies', c: 'studying', d: 'studied', correct: 'studying', exp: 'will have been + V-ing → studying' },
    { q: 'They will have been ___ for 3 hours by noon.', a: 'drive', b: 'drives', c: 'driving', d: 'drove', correct: 'driving', exp: 'will have been + V-ing → driving' },
  ],
  'Conditional Type 0': [
    { q: 'If you heat ice, it ___.', a: 'melt', b: 'melts', c: 'melted', d: 'melting', correct: 'melts', exp: 'Conditional 0: If + Present Simple, Present Simple' },
    { q: 'If it ___, the ground gets wet.', a: 'rain', b: 'rains', c: 'rained', d: 'raining', correct: 'rains', exp: 'Sự thật hiển nhiên → Present Simple cả hai vế' },
    { q: 'Plants die if they ___ get water.', a: 'doesn\'t', b: 'don\'t', c: 'didn\'t', d: 'won\'t', correct: 'don\'t', exp: '"Plants" số nhiều → don\'t' },
    { q: 'If you ___ water to 100°C, it boils.', a: 'heat', b: 'heats', c: 'heated', d: 'heating', correct: 'heat', exp: '"you" → V nguyên mẫu' },
    { q: 'Metal ___ if you heat it.', a: 'expand', b: 'expands', c: 'expanded', d: 'expanding', correct: 'expands', exp: '"Metal" ngôi 3 → expands' },
  ],
  'Conditional Type 1': [
    { q: 'If it rains, I ___ stay home.', a: 'will', b: 'would', c: 'had', d: 'am', correct: 'will', exp: 'Conditional 1: If + Present Simple, will + V' },
    { q: 'If you study hard, you ___ pass the exam.', a: 'would', b: 'will', c: 'could', d: 'might have', correct: 'will', exp: 'Điều kiện có thể xảy ra → will + V' },
    { q: 'If she ___ early, she will catch the bus.', a: 'leave', b: 'leaves', c: 'left', d: 'leaving', correct: 'leaves', exp: 'Vế if dùng Present Simple: "she" → leaves' },
    { q: 'We will go to the beach if the weather ___ nice.', a: 'is', b: 'will be', c: 'was', d: 'were', correct: 'is', exp: 'Vế if luôn dùng Present Simple, không dùng will' },
    { q: 'If I ___ time, I will visit you.', a: 'had', b: 'have', c: 'will have', d: 'having', correct: 'have', exp: 'Vế if dùng Present Simple: have' },
  ],
  'Conditional Type 2': [
    { q: 'If I ___ rich, I would travel the world.', a: 'am', b: 'was', c: 'were', d: 'will be', correct: 'were', exp: 'Conditional 2: If + Past Simple (were cho mọi ngôi)' },
    { q: 'If she had a car, she ___ drive to work.', a: 'will', b: 'would', c: 'can', d: 'does', correct: 'would', exp: 'Vế chính: would + V' },
    { q: 'I would buy a house if I ___ enough money.', a: 'have', b: 'has', c: 'had', d: 'having', correct: 'had', exp: 'Vế if dùng Past Simple: had' },
    { q: 'If he ___ harder, he would get better grades.', a: 'study', b: 'studies', c: 'studied', d: 'studying', correct: 'studied', exp: 'Conditional 2: If + V2 → studied' },
    { q: 'What ___ you do if you won the lottery?', a: 'will', b: 'would', c: 'do', d: 'did', correct: 'would', exp: 'Câu hỏi Conditional 2: would + S + V?' },
  ],
  'Conditional Type 3': [
    { q: 'If I had studied, I ___ have passed.', a: 'will', b: 'would', c: 'can', d: 'shall', correct: 'would', exp: 'Conditional 3: would + have + V3' },
    { q: 'If she ___ come, we would have been happier.', a: 'has', b: 'have', c: 'had', d: 'would', correct: 'had', exp: 'Vế if: If + had + V3' },
    { q: 'We would have won if we ___ practiced more.', a: 'has', b: 'have', c: 'had', d: 'would', correct: 'had', exp: 'If + had + V3 → had practiced' },
    { q: 'If he had arrived on time, he ___ have missed the train.', a: 'would', b: 'wouldn\'t', c: 'will', d: 'won\'t', correct: 'wouldn\'t', exp: 'Phủ định: wouldn\'t have + V3' },
    { q: 'If they ___ known, they would have helped.', a: 'has', b: 'have', c: 'had', d: 'would', correct: 'had', exp: 'Conditional 3: If + had + V3' },
  ],
  'Mixed Conditional': [
    { q: 'If I ___ harder in the past, I would have a better job now.', a: 'study', b: 'studied', c: 'had studied', d: 'have studied', correct: 'had studied', exp: 'Mixed: If + had V3 (QK) → would + V (HT)' },
    { q: 'If she were taller, she ___ have been a model.', a: 'will', b: 'would', c: 'can', d: 'shall', correct: 'would', exp: 'Mixed: If + were (HT) → would have V3 (QK)' },
    { q: 'If I had saved money, I ___ be rich now.', a: 'will', b: 'would', c: 'can', d: 'am', correct: 'would', exp: 'Vế kết quả hiện tại: would + V' },
    { q: 'If he ___ lazy, he would have finished it.', a: 'isn\'t', b: 'wasn\'t', c: 'weren\'t', d: 'hasn\'t been', correct: 'weren\'t', exp: 'Tính cách hiện tại (vế if): If + were/weren\'t' },
    { q: 'She would be here now if she ___ missed the flight.', a: 'hasn\'t', b: 'haven\'t', c: 'hadn\'t', d: 'didn\'t', correct: 'hadn\'t', exp: 'Sự kiện QK (vế if): If + hadn\'t + V3' },
  ],
};

// Hàm tạo câu hỏi mặc định cho các grammar point chưa có trong bank
function generateDefault(title, formula, example, index) {
  const templates = [
    {
      q: `Which sentence correctly uses the "${title}" structure?`,
      a: example || `Correct ${title} sentence`,
      b: `She have go yesterday.`,
      c: `They is playing last week.`,
      d: `He don't goes to school.`,
      correct: example || `Correct ${title} sentence`,
      exp: `Câu trúc đúng của ${title}: ${formula || title}`
    },
    {
      q: `What is the correct structure for "${title}"?`,
      a: formula || `S + V`,
      b: `S + V-ing + had`,
      c: `S + will + been + V`,
      d: `S + have + V-ing + been`,
      correct: formula || `S + V`,
      exp: `Cấu trúc ${title}: ${formula || 'xem bài học'}`
    },
    {
      q: `"${example || 'She goes to school.'}" is an example of which grammar?`,
      a: title,
      b: 'Past Perfect Continuous',
      c: 'Future Perfect',
      d: 'Conditional Type 3',
      correct: title,
      exp: `Ví dụ trên thuộc cấu trúc ${title}`
    },
    {
      q: `Which word/phrase is a signal for "${title}"?`,
      a: 'always / every day',
      b: 'yesterday',
      c: 'by the time',
      d: 'if I were',
      correct: 'always / every day',
      exp: `Dấu hiệu nhận biết thì/cấu trúc ${title}`
    },
    {
      q: `Choose the INCORRECT sentence using "${title}":`,
      a: `He don't likes apples.`,
      b: example || `She reads books.`,
      c: `They study English.`,
      d: `We eat lunch at noon.`,
      correct: `He don't likes apples.`,
      exp: `"He don't likes" sai → đúng: "He doesn't like"`
    }
  ];
  return templates[index % 5];
}

async function generateQuizzes() {
  const pool = mysql.createPool({
    host: process.env.DB_HOST || 'localhost',
    port: process.env.DB_PORT || 3306,
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASSWORD || '123456',
    database: process.env.DB_NAME || 'minlish_db',
    charset: 'utf8mb4'
  });

  console.log('Đang kết nối database...\n');

  // Lấy tất cả grammar notes
  const [notes] = await pool.execute('SELECT id, title, formula, example FROM grammar_notes ORDER BY id');
  console.log(`Tìm thấy ${notes.length} bài ngữ pháp.\n`);

  // Xóa quiz cũ (nếu có) để tránh trùng lặp
  await pool.execute('DELETE FROM grammar_quizzes');
  console.log('Đã xóa dữ liệu quiz cũ.\n');

  let totalInserted = 0;

  for (const note of notes) {
    // Tìm grammar point gốc (bỏ phần trong ngoặc)
    const grammarPoint = note.title.replace(/\s*\(.*\)$/, '').trim();

    // Lấy quiz từ bank nếu có, không thì dùng mặc định
    const bankQuizzes = quizBank[grammarPoint];

    for (let i = 0; i < 5; i++) {
      let quiz;
      if (bankQuizzes) {
        quiz = bankQuizzes[i];
      } else {
        quiz = generateDefault(note.title, note.formula, note.example, i);
      }

      await pool.execute(
        `INSERT INTO grammar_quizzes (grammar_note_id, question, option_a, option_b, option_c, option_d, correct_answer, explanation)
         VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
        [note.id, quiz.q, quiz.a, quiz.b, quiz.c, quiz.d, quiz.correct, quiz.exp]
      );
      totalInserted++;
    }
    console.log(`✓ ${note.title} → 5 câu hỏi`);
  }

  console.log(`\n✅ Hoàn tất! Đã tạo ${totalInserted} câu hỏi cho ${notes.length} bài ngữ pháp.`);
  process.exit(0);
}

generateQuizzes().catch(err => { console.error('Lỗi:', err.message); process.exit(1); });
