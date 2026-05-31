const mysql = require('mysql2/promise');
const fs = require('fs');
const path = require('path');

async function seedGrammar() {
  const pool = mysql.createPool({
    host: 'localhost',
    user: 'root',
    password: '123456',
    database: 'minlish_db',
    charset: 'utf8mb4'
  });

  const dbPath = path.join(__dirname, 'mock-api/db.json');
  const data = JSON.parse(fs.readFileSync(dbPath, 'utf8'));

  let userOneId = 1;

  for (const note of data.grammar_notes) {
    // Check if the note already exists
    const [existing] = await pool.query('SELECT id FROM grammar_notes WHERE title = ? AND user_id = ?', [note.title, userOneId]);
    let noteId;

    if (existing.length === 0) {
      const [result] = await pool.query(
        'INSERT INTO grammar_notes (user_id, title, formula, explanation, example, common_mistake) VALUES (?, ?, ?, ?, ?, ?)',
        [userOneId, note.title, note.formula, note.explanation, note.example, note.commonMistakes]
      );
      noteId = result.insertId;
      console.log(`Inserted grammar note: ${note.title} with ID ${noteId}`);
    } else {
      noteId = existing[0].id;
      console.log(`Grammar note ${note.title} already exists with ID ${noteId}`);
    }

    // Insert quizzes for this note
    const quizzes = data.quiz_questions.filter(q => q.grammarNoteId === note.id);
    for (const q of quizzes) {
      const [existingQuiz] = await pool.query('SELECT id FROM grammar_quizzes WHERE question = ? AND grammar_note_id = ?', [q.question, noteId]);
      
      if (existingQuiz.length === 0) {
        // q.options has 4 items
        await pool.query(
          'INSERT INTO grammar_quizzes (grammar_note_id, question, option_a, option_b, option_c, option_d, correct_answer, explanation) VALUES (?, ?, ?, ?, ?, ?, ?, ?)',
          [noteId, q.question, q.options[0]||'', q.options[1]||'', q.options[2]||'', q.options[3]||'', q.correctAnswer, q.explanation]
        );
        console.log(`Inserted quiz for note ${noteId}: ${q.question}`);
      }
    }
  }

  console.log('Seed completed!');
  process.exit(0);
}

seedGrammar().catch(err => {
  console.error(err);
  process.exit(1);
});
