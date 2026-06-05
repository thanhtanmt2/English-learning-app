require('dotenv').config({ path: require('path').join(__dirname, '../.env') });
const db = require('../config/db');
const { topics, quizBank, vocabPatches, grammarTitlePatches, grammarExplanationPatches } =
  require('../data/grammar-content.json');

const LETTERS = ['A', 'B', 'C', 'D'];
const USER_ID = 1;

function correctLetter(options, correct) {
  const idx = options.indexOf(correct);
  return idx >= 0 ? LETTERS[idx] : 'A';
}

async function insertTopics() {
  console.log(`\n📚 Seeding ${topics.length} grammar topics với quizzes...`);
  for (const topic of topics) {
    const [existing] = await db.query(
      'SELECT id FROM grammar_notes WHERE title = ? AND user_id = ?',
      [topic.title, USER_ID]
    );

    let noteId;
    if (existing.length === 0) {
      const [result] = await db.query(
        'INSERT INTO grammar_notes (user_id, title, formula, explanation, example, common_mistake) VALUES (?, ?, ?, ?, ?, ?)',
        [USER_ID, topic.title, topic.formula, topic.explanation, topic.example, topic.commonMistakes]
      );
      noteId = result.insertId;
    } else {
      noteId = existing[0].id;
    }

    for (const q of topic.quizzes) {
      const [ex] = await db.query(
        'SELECT id FROM grammar_quizzes WHERE question = ? AND grammar_note_id = ?',
        [q.question, noteId]
      );
      if (ex.length === 0) {
        await db.query(
          'INSERT INTO grammar_quizzes (grammar_note_id, question, option_a, option_b, option_c, option_d, correct_answer, explanation) VALUES (?, ?, ?, ?, ?, ?, ?, ?)',
          [noteId, q.question, q.options[0], q.options[1], q.options[2], q.options[3], correctLetter(q.options, q.correct), q.exp]
        );
      }
    }
    console.log(`  ✓ ${topic.title} (${topic.quizzes.length} quizzes)`);
  }
}

function generateDefault(title, formula, example, index) {
  const templates = [
    {
      q: `Which sentence correctly uses the "${title}" structure?`,
      a: example || `Correct ${title} sentence`, b: `She have go yesterday.`,
      c: `They is playing last week.`, d: `He don't goes to school.`,
      correct: example || `Correct ${title} sentence`,
      exp: `Cấu trúc đúng của ${title}: ${formula || title}`
    },
    {
      q: `What is the correct structure for "${title}"?`,
      a: formula || `S + V`, b: `S + V-ing + had`, c: `S + will + been + V`, d: `S + have + V-ing + been`,
      correct: formula || `S + V`,
      exp: `Cấu trúc ${title}: ${formula || 'xem bài học'}`
    },
    {
      q: `"${example || 'She goes to school.'}" is an example of which grammar?`,
      a: title, b: 'Past Perfect Continuous', c: 'Future Perfect', d: 'Conditional Type 3',
      correct: title,
      exp: `Ví dụ trên thuộc cấu trúc ${title}`
    },
    {
      q: `Which word/phrase is a signal for "${title}"?`,
      a: 'always / every day', b: 'yesterday', c: 'by the time', d: 'if I were',
      correct: 'always / every day',
      exp: `Dấu hiệu nhận biết thì/cấu trúc ${title}`
    },
    {
      q: `Choose the INCORRECT sentence using "${title}":`,
      a: `He don't likes apples.`, b: example || `She reads books.`, c: `They study English.`, d: `We eat lunch at noon.`,
      correct: `He don't likes apples.`,
      exp: `"He don't likes" sai → đúng: "He doesn't like"`
    }
  ];
  return templates[index % 5];
}

async function generateCsvQuizzes() {
  const [notes] = await db.execute('SELECT id, title, formula, example FROM grammar_notes ORDER BY id');
  console.log(`\n🎯 Generating quizzes cho ${notes.length} grammar notes từ CSV...`);

  let inserted = 0;
  for (const note of notes) {
    const grammarPoint = note.title.replace(/\s*\(.*\)$/, '').trim();
    const bankQuizzes = quizBank[grammarPoint];

    for (let i = 0; i < 5; i++) {
      const quiz = bankQuizzes ? bankQuizzes[i] : generateDefault(note.title, note.formula, note.example, i);
      const opts = [quiz.a, quiz.b, quiz.c, quiz.d];

      const [ex] = await db.query(
        'SELECT id FROM grammar_quizzes WHERE question = ? AND grammar_note_id = ?',
        [quiz.q, note.id]
      );
      if (ex.length === 0) {
        await db.execute(
          'INSERT INTO grammar_quizzes (grammar_note_id, question, option_a, option_b, option_c, option_d, correct_answer, explanation) VALUES (?, ?, ?, ?, ?, ?, ?, ?)',
          [note.id, quiz.q, quiz.a, quiz.b, quiz.c, quiz.d, correctLetter(opts, quiz.correct), quiz.exp]
        );
        inserted++;
      }
    }
  }
  console.log(`  ✓ Đã tạo ${inserted} câu hỏi mới`);
}

async function patchEncoding() {
  console.log('\n🔧 Patch dữ liệu tiếng Việt từ CSV bị mất dấu...');

  let vocabFixed = 0;
  for (const [eng, vie] of Object.entries(vocabPatches)) {
    const [r] = await db.execute(
      'UPDATE words SET meaning = ? WHERE LOWER(word) = ? AND meaning != ?',
      [vie, eng.toLowerCase(), vie]
    );
    vocabFixed += r.affectedRows;
  }
  console.log(`  ✓ ${vocabFixed} từ vựng`);

  let titleFixed = 0;
  for (const [broken, correct] of Object.entries(grammarTitlePatches)) {
    const [r] = await db.execute(
      'UPDATE grammar_notes SET title = REPLACE(title, ?, ?) WHERE title LIKE ?',
      [broken, correct, `%${broken}%`]
    );
    titleFixed += r.affectedRows;
  }
  console.log(`  ✓ ${titleFixed} grammar titles`);

  let explFixed = 0;
  for (const [broken, correct] of Object.entries(grammarExplanationPatches)) {
    const [r] = await db.execute(
      'UPDATE grammar_notes SET explanation = ? WHERE explanation = ?',
      [correct, broken]
    );
    explFixed += r.affectedRows;
  }
  console.log(`  ✓ ${explFixed} grammar explanations`);
}

async function main() {
  console.log('=== seed-grammar.js ===');
  await insertTopics();
  await generateCsvQuizzes();
  await patchEncoding();

  const [[{ noteCount }]] = await db.execute('SELECT COUNT(*) AS noteCount FROM grammar_notes');
  const [[{ quizCount }]] = await db.execute('SELECT COUNT(*) AS quizCount FROM grammar_quizzes');
  console.log(`\n✅ Xong! grammar_notes: ${noteCount} | grammar_quizzes: ${quizCount}`);
  process.exit(0);
}

main().catch(err => {
  console.error('❌ Lỗi:', err.message);
  process.exit(1);
});
