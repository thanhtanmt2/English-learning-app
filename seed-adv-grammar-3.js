const mysql = require('mysql2/promise');

const advancedGrammarData3 = [
  {
    title: "Phân Từ (Present & Past Participles)",
    formula: "Hiện tại phân từ (V-ing) mang nghĩa chủ động / Quá khứ phân từ (V3/ed) mang nghĩa bị động",
    explanation: "Dùng như tính từ để bổ nghĩa cho danh từ, hoặc rút gọn mệnh đề. V-ing miêu tả bản chất sự vật (chủ động gây ra cảm giác). V-ed miêu tả cảm giác của ai đó (bị tác động).",
    example: "The movie was boring. (Bộ phim nhàm chán) / I was bored. (Tôi cảm thấy chán).",
    commonMistakes: "Dùng nhầm lẫn giữa V-ing và V-ed khi nói về cảm giác của người (I am boring -> Tôi là kẻ tẻ nhạt, thay vì I am bored -> Tôi thấy chán).",
    quizzes: [
      { question: "The book is very ___. I can't stop reading it.", options: ["interest", "interested", "interesting", "interests"], correct: "interesting", exp: "Sách gây ra sự hứng thú (bản chất) -> V-ing." },
      { question: "I was ___ by the news.", options: ["shock", "shocking", "shocked", "shocks"], correct: "shocked", exp: "Người cảm thấy sốc do tác động bên ngoài -> V-ed." },
      { question: "It was a ___ journey.", options: ["tire", "tiring", "tired", "tires"], correct: "tiring", exp: "Chuyến đi gây ra sự mệt mỏi -> V-ing." },
      { question: "She is ___ in learning English.", options: ["interest", "interesting", "interested", "interests"], correct: "interested", exp: "Cụm từ: be interested in (cảm thấy hứng thú)." },
      { question: "The ___ boy was crying.", options: ["frighten", "frightening", "frightened", "frightens"], correct: "frightened", exp: "Đứa bé bị làm cho hoảng sợ -> V-ed." },
      { question: "This weather is ___.", options: ["depress", "depressing", "depressed", "depresses"], correct: "depressing", exp: "Thời tiết gây ra sự chán nản -> V-ing." },
      { question: "Are you ___ with your job?", options: ["satisfy", "satisfying", "satisfied", "satisfies"], correct: "satisfied", exp: "Người cảm thấy hài lòng -> V-ed." },
      { question: "The instructions were ___. Nobody understood them.", options: ["confuse", "confusing", "confused", "confuses"], correct: "confusing", exp: "Hướng dẫn gây khó hiểu -> V-ing." },
      { question: "I am ___ because I have nothing to do.", options: ["bore", "boring", "bored", "bores"], correct: "bored", exp: "Cảm giác chán nản của bản thân -> V-ed." },
      { question: "He told a ___ story.", options: ["fascinate", "fascinating", "fascinated", "fascinates"], correct: "fascinating", exp: "Câu chuyện thú vị -> V-ing." }
    ]
  },
  {
    title: "Câu Ước (Wish Clauses)",
    formula: "Hiện tại: S + wish(es) + S + V2/ed | Quá khứ: S + wish(es) + S + had + V3",
    explanation: "Dùng để diễn tả một mong muốn trái ngược với thực tế ở hiện tại hoặc quá khứ.",
    example: "I wish I were rich. (Hiện tại không giàu) / I wish I had passed the exam. (Quá khứ đã trượt).",
    commonMistakes: "Dùng thì hiện tại sau 'wish' để chỉ hiện tại (I wish I have -> Sai). To be dùng 'were' cho mọi ngôi.",
    quizzes: [
      { question: "I don't have a car. I wish I ___ one.", options: ["have", "has", "had", "will have"], correct: "had", exp: "Ước trái hiện tại dùng QKĐ (had)." },
      { question: "She wishes she ___ the answer, but she doesn't.", options: ["know", "knows", "knew", "had known"], correct: "knew", exp: "Ước trái hiện tại dùng QKĐ (knew)." },
      { question: "I failed the test. I wish I ___ harder.", options: ["study", "studied", "had studied", "would study"], correct: "had studied", exp: "Ước trái quá khứ dùng QKHT (had studied)." },
      { question: "It is raining heavily. I wish it ___.", options: ["stop", "stops", "stopped", "will stop"], correct: "stopped", exp: "Ước trái hiện tại dùng QKĐ (stopped) hoặc would stop." },
      { question: "He wishes he ___ a bird to fly.", options: ["is", "was", "were", "had been"], correct: "were", exp: "Ước trái hiện tại với to be dùng were." },
      { question: "I wish you ___ making that noise!", options: ["stop", "will stop", "would stop", "had stopped"], correct: "would stop", exp: "Ước ai đó thay đổi hành động gây khó chịu dùng would + V." },
      { question: "They wish they ___ to the party yesterday.", options: ["go", "went", "had gone", "would go"], correct: "had gone", exp: "Ước cho điều hôm qua (quá khứ) dùng QKHT." },
      { question: "I wish I ___ speak French.", options: ["can", "could", "will", "would"], correct: "could", exp: "Trái thực tế hiện tại với can chuyển thành could." },
      { question: "She wishes she ___ so much cake last night.", options: ["didn't eat", "doesn't eat", "hasn't eaten", "hadn't eaten"], correct: "hadn't eaten", exp: "Trái thực tế quá khứ (last night) dùng phủ định của QKHT." },
      { question: "I wish tomorrow ___ Sunday.", options: ["is", "be", "was", "were"], correct: "were", exp: "To be sau wish dùng were." }
    ]
  },
  {
    title: "Thể Giả Định (Subjunctive Mood)",
    formula: "S1 + suggest/recommend/insist... + that + S2 + (not) + V nguyên mẫu",
    explanation: "Dùng để diễn tả một yêu cầu, đề nghị, mệnh lệnh. Động từ trong mệnh đề that luôn ở dạng nguyên mẫu không to cho mọi ngôi.",
    example: "The doctor suggested that he stop smoking.",
    commonMistakes: "Chia động từ theo chủ ngữ S2 (he stops -> Sai) thay vì để nguyên mẫu (he stop).",
    quizzes: [
      { question: "The teacher insists that everyone ___ on time.", options: ["is", "are", "be", "was"], correct: "be", exp: "To be ở thể giả định luôn là 'be'." },
      { question: "I suggest that she ___ a doctor.", options: ["sees", "see", "saw", "seeing"], correct: "see", exp: "Động từ giữ nguyên mẫu (see) dù chủ ngữ là she." },
      { question: "It is important that he ___ the medicine every day.", options: ["take", "takes", "took", "taking"], correct: "take", exp: "Tính từ important yêu cầu thể giả định (nguyên mẫu)." },
      { question: "They recommended that the project ___ immediately.", options: ["is started", "be started", "starts", "started"], correct: "be started", exp: "Câu bị động ở thể giả định dùng be + V3." },
      { question: "We asked that she ___ late again.", options: ["is not", "not be", "doesn't be", "don't be"], correct: "not be", exp: "Phủ định của thể giả định thêm 'not' trước động từ nguyên mẫu." },
      { question: "It is essential that you ___ there before 8 AM.", options: ["are", "is", "be", "will be"], correct: "be", exp: "To be giữ nguyên là be." },
      { question: "The law requires that all cars ___ checked regularly.", options: ["are", "is", "be", "have"], correct: "be", exp: "Require yêu cầu thể giả định." },
      { question: "He proposed that we ___ a party.", options: ["have", "has", "had", "having"], correct: "have", exp: "Động từ have giữ nguyên mẫu." },
      { question: "My advice is that he ___ his mind.", options: ["changes", "change", "changed", "changing"], correct: "change", exp: "Danh từ advice cũng đi kèm thể giả định." },
      { question: "It is vital that she ___ English.", options: ["study", "studies", "studied", "studying"], correct: "study", exp: "Giữ nguyên mẫu study." }
    ]
  },
  {
    title: "Rút Gọn Mệnh Đề Quan Hệ (Reduced Relative Clauses)",
    formula: "V-ing (nếu chủ động) / V3-ed (nếu bị động) / To V (chỉ mục đích hoặc sau the first, the last)",
    explanation: "Được dùng để làm câu ngắn gọn hơn bằng cách bỏ đại từ quan hệ (who, which, that) và trợ động từ to be.",
    example: "The man standing there is my father. (Thay vì: The man who is standing...)",
    commonMistakes: "Dùng sai V-ing cho câu mang nghĩa bị động và ngược lại.",
    quizzes: [
      { question: "The boy ___ by the window is my brother.", options: ["standing", "stands", "stood", "to stand"], correct: "standing", exp: "Rút gọn mang nghĩa chủ động (cậu bé đang đứng) -> V-ing." },
      { question: "The book ___ by Nam Cao is very famous.", options: ["writing", "writes", "written", "wrote"], correct: "written", exp: "Quyển sách được viết (bị động) -> V3 (written)." },
      { question: "Neil Armstrong was the first man ___ on the moon.", options: ["walking", "walk", "to walk", "walked"], correct: "to walk", exp: "Sau số thứ tự (the first) dùng To V." },
      { question: "The students ___ the test are very smart.", options: ["taking", "taken", "take", "to take"], correct: "taking", exp: "Học sinh đang làm bài (chủ động) -> taking." },
      { question: "The house ___ in 1990 is now a museum.", options: ["building", "builds", "built", "to build"], correct: "built", exp: "Ngôi nhà được xây (bị động) -> built." },
      { question: "I have some homework ___ tonight.", options: ["doing", "to do", "done", "do"], correct: "to do", exp: "Chỉ mục đích/sự cần thiết -> To V." },
      { question: "The road ___ to the village is very narrow.", options: ["leads", "led", "leading", "to lead"], correct: "leading", exp: "Con đường dẫn tới (chủ động) -> leading." },
      { question: "Here is the form ___ by the manager.", options: ["signing", "signed", "sign", "to sign"], correct: "signed", exp: "Đơn được ký (bị động) -> signed." },
      { question: "She is the only person ___ the truth.", options: ["knowing", "known", "to know", "know"], correct: "to know", exp: "Sau the only dùng To V." },
      { question: "The girl ___ the red dress is Mary.", options: ["wearing", "worn", "wears", "to wear"], correct: "wearing", exp: "Cô gái đang mặc váy (chủ động) -> wearing." }
    ]
  }
];

async function seedAdvancedGrammar3() {
  const pool = mysql.createPool({
    host: 'localhost',
    user: 'root',
    password: '123456',
    database: 'minlish_db',
    charset: 'utf8mb4'
  });

  const userId = 1;
  console.log('Bắt đầu chèn dữ liệu ngữ pháp nâng cao phần 3...');

  for (const topic of advancedGrammarData3) {
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

  console.log('Hoàn tất phần 3!');
  process.exit(0);
}

seedAdvancedGrammar3().catch(console.error);
