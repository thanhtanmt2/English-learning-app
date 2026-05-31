const mysql = require('mysql2/promise');

const advancedGrammarData = [
  {
    title: "Thì Hiện Tại Hoàn Thành (Present Perfect)",
    formula: "S + have/has + V3/ed + O",
    explanation: "Diễn tả hành động bắt đầu ở quá khứ, kéo dài đến hiện tại và có thể tiếp tục ở tương lai. Nhấn mạnh kết quả.",
    example: "I have lived here for 5 years. / She has just finished her homework.",
    commonMistakes: "Nhầm lẫn với Quá khứ đơn (hành động đã chấm dứt hoàn toàn).",
    quizzes: [
      { question: "I ___ to Paris three times.", options: ["go", "went", "have been", "am going"], correct: "have been", exp: "Trải nghiệm tính đến hiện tại dùng HTHT." },
      { question: "She ___ her keys. She can't get into the house.", options: ["lost", "has lost", "loses", "is losing"], correct: "has lost", exp: "Hành động làm mất chìa khóa để lại hậu quả ở hiện tại." },
      { question: "They ___ each other since they were children.", options: ["know", "knew", "have known", "knowing"], correct: "have known", exp: "'Since' + mốc thời gian -> HTHT." },
      { question: "___ you ever eaten sushi?", options: ["Do", "Did", "Have", "Are"], correct: "Have", exp: "Hỏi về trải nghiệm với 'ever' dùng HTHT." },
      { question: "He has worked here ___ 2020.", options: ["since", "for", "in", "at"], correct: "since", exp: "Mốc thời gian 2020 đi với 'since'." },
      { question: "We have been friends ___ 10 years.", options: ["since", "for", "in", "during"], correct: "for", exp: "Khoảng thời gian 10 năm đi với 'for'." },
      { question: "I ___ finished my work yet.", options: ["haven't", "hasn't", "didn't", "don't"], correct: "haven't", exp: "Phủ định HTHT đi với 'I' là haven't, dùng với 'yet'." },
      { question: "She has ___ left the office.", options: ["just", "yet", "ever", "never"], correct: "just", exp: "'Just' (vừa mới) đứng giữa has và V3." },
      { question: "How long ___ you lived here?", options: ["do", "did", "have", "has"], correct: "have", exp: "Câu hỏi How long thường dùng HTHT." },
      { question: "He is the best player I have ___ seen.", options: ["never", "ever", "just", "yet"], correct: "ever", exp: "'Ever' dùng trong câu nhấn mạnh với so sánh nhất." }
    ]
  },
  {
    title: "Câu Điều Kiện Loại 2 (Conditional Type 2)",
    formula: "If + S + V2/ed, S + would/could + V",
    explanation: "Diễn tả một giả thiết trái ngược với thực tế ở hiện tại.",
    example: "If I were you, I would buy that car.",
    commonMistakes: "Quên lùi thì ở mệnh đề If. Dùng 'was' thay vì 'were' (dù 'was' được chấp nhận trong văn nói, nhưng 'were' chuẩn hơn cho mọi ngôi).",
    quizzes: [
      { question: "If I ___ rich, I would travel the world.", options: ["am", "was", "were", "been"], correct: "were", exp: "Câu ĐK loại 2, to be luôn dùng 'were' cho mọi ngôi." },
      { question: "If she ___ harder, she would pass the exam.", options: ["study", "studies", "studied", "studying"], correct: "studied", exp: "Vế If của ĐK2 dùng Quá khứ đơn." },
      { question: "What would you do if you ___ a ghost?", options: ["see", "sees", "saw", "seen"], correct: "saw", exp: "Vế If dùng Quá khứ đơn của see là saw." },
      { question: "If I had a car, I ___ drive to work.", options: ["will", "would", "can", "shall"], correct: "would", exp: "Vế chính của ĐK2 dùng would + V." },
      { question: "He would be happier if he ___ not work so much.", options: ["do", "does", "did", "was"], correct: "did", exp: "Phủ định QKĐ dùng 'did not'." },
      { question: "If they ___ here, they would help us.", options: ["are", "was", "were", "be"], correct: "were", exp: "ĐK2 dùng 'were' cho mọi ngôi." },
      { question: "I ___ buy that house if I were you.", options: ["will", "would", "am going to", "can"], correct: "would", exp: "Lời khuyên 'If I were you' dùng ĐK2." },
      { question: "If he knew her number, he ___ call her.", options: ["will", "would", "can", "may"], correct: "would", exp: "Knew (V2) -> ĐK2 -> dùng would." },
      { question: "She could win the race if she ___ faster.", options: ["run", "runs", "ran", "running"], correct: "ran", exp: "Vế chính dùng could -> ĐK2 -> vế If dùng ran (V2)." },
      { question: "If we ___ wings, we could fly.", options: ["have", "has", "had", "having"], correct: "had", exp: "Trái thực tế hiện tại -> ĐK2 -> dùng had (V2)." }
    ]
  },
  {
    title: "Câu Điều Kiện Loại 3 (Conditional Type 3)",
    formula: "If + S + had + V3/ed, S + would/could + have + V3/ed",
    explanation: "Diễn tả một giả thiết trái ngược với thực tế ở quá khứ (thường mang ý tiếc nuối).",
    example: "If I had studied harder, I would have passed the exam.",
    commonMistakes: "Lắp sai công thức (would have V3) vào mệnh đề If.",
    quizzes: [
      { question: "If I had known you were in hospital, I ___ you.", options: ["would visit", "will visit", "would have visited", "visited"], correct: "would have visited", exp: "Vế If dùng quá khứ hoàn thành -> ĐK3 -> dùng would have V3." },
      { question: "If she ___ the train, she would have arrived on time.", options: ["catch", "caught", "had caught", "has caught"], correct: "had caught", exp: "Vế chính dùng would have V3 -> ĐK3 -> vế If dùng had + V3." },
      { question: "They wouldn't have got lost if they ___ a map.", options: ["have", "had", "had had", "having"], correct: "had had", exp: "ĐK3: had (trợ động từ) + had (V3 của have)." },
      { question: "If he had driven more carefully, he ___ an accident.", options: ["wouldn't have had", "won't have", "didn't have", "doesn't have"], correct: "wouldn't have had", exp: "Trái thực tế quá khứ -> ĐK3." },
      { question: "I would have helped you if you ___ me.", options: ["ask", "asked", "had asked", "has asked"], correct: "had asked", exp: "Vế If của ĐK3 dùng QKHT." },
      { question: "If we had left earlier, we ___ the bus.", options: ["wouldn't miss", "won't miss", "wouldn't have missed", "didn't miss"], correct: "wouldn't have missed", exp: "ĐK3 vế chính dùng would have V3." },
      { question: "She ___ passed the exam if she had studied.", options: ["will", "would", "would have", "had"], correct: "would have", exp: "Vế chính ĐK3 cần would have." },
      { question: "If you had told me, I ___ something.", options: ["could do", "can do", "could have done", "did"], correct: "could have done", exp: "ĐK3 dùng could have V3 chỉ khả năng trong quá khứ." },
      { question: "If I ___ the answer, I would have raised my hand.", options: ["know", "knew", "had known", "have known"], correct: "had known", exp: "Mệnh đề If của ĐK3 dùng had known." },
      { question: "We would have gone to the beach if it ___ raining.", options: ["stopped", "had stopped", "has stopped", "stops"], correct: "had stopped", exp: "ĐK3 dùng QKHT ở vế If." }
    ]
  },
  {
    title: "Câu Bị Động (Passive Voice)",
    formula: "S + to be (chia theo thì) + V3/ed + (by O)",
    explanation: "Dùng khi muốn nhấn mạnh vào đối tượng chịu tác động của hành động thay vì người thực hiện hành động.",
    example: "The house was built in 1990.",
    commonMistakes: "Chia sai thì của động từ 'to be' hoặc quên chuyển động từ chính sang dạng Phân từ 2 (V3/ed).",
    quizzes: [
      { question: "This house ___ in 1990.", options: ["builds", "built", "is built", "was built"], correct: "was built", exp: "Hành động ở quá khứ (1990), nhà bị xây -> Bị động QKĐ." },
      { question: "English ___ all over the world.", options: ["speaks", "is speaking", "is spoken", "was spoken"], correct: "is spoken", exp: "Sự thật ở hiện tại, tiếng Anh được nói -> Bị động HTĐ." },
      { question: "My car ___ yesterday.", options: ["steals", "stole", "is stolen", "was stolen"], correct: "was stolen", exp: "Quá khứ (yesterday), xe bị trộm -> was stolen." },
      { question: "The letter ___ by Mary tomorrow.", options: ["will send", "will be sent", "is sent", "sends"], correct: "will be sent", exp: "Tương lai (tomorrow), thư được gửi -> Bị động TLĐ (will be V3)." },
      { question: "A new bridge ___ across the river at the moment.", options: ["builds", "is building", "is being built", "built"], correct: "is being built", exp: "Đang xảy ra (at the moment) -> Bị động HTTD (is being V3)." },
      { question: "The window ___ by the boys.", options: ["has broken", "has been broken", "is breaking", "broke"], correct: "has been broken", exp: "Bị động thì HTHT (has been V3)." },
      { question: "The work must ___ before 5 PM.", options: ["finish", "finished", "be finished", "be finish"], correct: "be finished", exp: "Sau động từ khiếm khuyết (must) dùng be + V3." },
      { question: "I ___ a gift by my friend.", options: ["gave", "give", "was given", "am giving"], correct: "was given", exp: "Bị động quá khứ (được tặng quà)." },
      { question: "The room ___ every day.", options: ["cleans", "is clean", "is cleaned", "cleaned"], correct: "is cleaned", exp: "Thói quen lặp lại (every day) -> Bị động HTĐ." },
      { question: "The cake ___ by my mother.", options: ["made", "make", "was made", "is making"], correct: "was made", exp: "Bánh được làm bởi mẹ -> was made." }
    ]
  },
  {
    title: "Câu Hỏi Đuôi (Tag Questions)",
    formula: "Khẳng định, phủ định? / Phủ định, khẳng định?",
    explanation: "Là dạng câu hỏi ngắn ở cuối câu trần thuật để xác nhận lại thông tin. Nếu mệnh đề chính khẳng định, đuôi phủ định và ngược lại.",
    example: "You are a student, aren't you?",
    commonMistakes: "Quên mượn trợ động từ, hoặc chia sai thể khẳng định/phủ định. Lưu ý: 'I am' -> 'aren't I', 'Let's' -> 'shall we'.",
    quizzes: [
      { question: "You are coming to the party, ___?", options: ["are you", "aren't you", "do you", "don't you"], correct: "aren't you", exp: "Vế đầu khẳng định 'are' -> đuôi phủ định 'aren't'." },
      { question: "She doesn't like coffee, ___?", options: ["does she", "doesn't she", "is she", "isn't she"], correct: "does she", exp: "Vế đầu phủ định 'doesn't' -> đuôi khẳng định 'does'." },
      { question: "He went to the cinema yesterday, ___?", options: ["did he", "didn't he", "went he", "wasn't he"], correct: "didn't he", exp: "Vế đầu khẳng định QKĐ (went) -> mượn trợ động từ didn't." },
      { question: "I am late, ___?", options: ["am I", "amn't I", "aren't I", "don't I"], correct: "aren't I", exp: "Trường hợp đặc biệt: I am -> aren't I." },
      { question: "Let's go for a walk, ___?", options: ["will we", "shall we", "do we", "don't we"], correct: "shall we", exp: "Câu rủ rê Let's -> đuôi luôn là shall we." },
      { question: "They have finished the work, ___?", options: ["do they", "don't they", "have they", "haven't they"], correct: "haven't they", exp: "Vế đầu dùng HTHT 'have' -> đuôi 'haven't'." },
      { question: "Nobody called, ___?", options: ["did they", "didn't they", "did nobody", "didn't nobody"], correct: "did they", exp: "Nobody mang nghĩa phủ định, chủ ngữ thay bằng 'they' -> đuôi khẳng định 'did they'." },
      { question: "Close the door, ___?", options: ["do you", "don't you", "will you", "shall you"], correct: "will you", exp: "Câu mệnh lệnh -> đuôi dùng will you." },
      { question: "She can swim well, ___?", options: ["can she", "can't she", "does she", "doesn't she"], correct: "can't she", exp: "Vế đầu dùng can -> đuôi can't." },
      { question: "There is a cat on the roof, ___?", options: ["is it", "isn't it", "is there", "isn't there"], correct: "isn't there", exp: "Chủ ngữ là There -> đuôi dùng there." }
    ]
  }
];

async function seedAdvancedGrammar() {
  const pool = mysql.createPool({
    host: 'localhost',
    user: 'root',
    password: '123456',
    database: 'minlish_db',
    charset: 'utf8mb4'
  });

  const userId = 1;

  console.log('Bắt đầu chèn dữ liệu ngữ pháp nâng cao phần 1...');

  for (const topic of advancedGrammarData) {
    const [existing] = await pool.query('SELECT id FROM grammar_notes WHERE title = ? AND user_id = ?', [topic.title, userId]);
    let noteId;

    if (existing.length === 0) {
      const [result] = await pool.query(
        'INSERT INTO grammar_notes (user_id, title, formula, explanation, example, common_mistake) VALUES (?, ?, ?, ?, ?, ?)',
        [userId, topic.title, topic.formula, topic.explanation, topic.example, topic.commonMistakes]
      );
      noteId = result.insertId;
      console.log(`Đã thêm: ${topic.title}`);
    } else {
      noteId = existing[0].id;
      console.log(`Đã tồn tại: ${topic.title}`);
    }

    for (const q of topic.quizzes) {
      const [exQuiz] = await pool.query('SELECT id FROM grammar_quizzes WHERE question = ? AND grammar_note_id = ?', [q.question, noteId]);
      if (exQuiz.length === 0) {
        await pool.query(
          'INSERT INTO grammar_quizzes (grammar_note_id, question, option_a, option_b, option_c, option_d, correct_answer, explanation) VALUES (?, ?, ?, ?, ?, ?, ?, ?)',
          [noteId, q.question, q.options[0], q.options[1], q.options[2], q.options[3], q.correct, q.exp]
        );
      }
    }
  }

  console.log('Hoàn tất phần 1!');
  process.exit(0);
}

seedAdvancedGrammar().catch(console.error);
