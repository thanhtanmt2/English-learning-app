const mysql = require('mysql2/promise');

const advancedGrammarData2 = [
  {
    title: "Câu So Sánh (Comparisons)",
    formula: "Hơn: S + V + adj-er/more adj + than + ... | Nhất: S + V + the adj-est/the most adj",
    explanation: "Dùng để so sánh tính chất của 2 hoặc nhiều sự vật, hiện tượng. Tính từ ngắn thêm -er/-est, tính từ dài thêm more/the most.",
    example: "He is taller than me. / She is the most beautiful girl in the class.",
    commonMistakes: "Dùng 'more' với tính từ ngắn (more tall), hoặc quên 'the' trong so sánh nhất.",
    quizzes: [
      { question: "My car is ___ than yours.", options: ["fast", "faster", "more fast", "fastest"], correct: "faster", exp: "Fast là tính từ ngắn, so sánh hơn thêm -er." },
      { question: "This is the ___ book I have ever read.", options: ["interesting", "more interesting", "most interesting", "interestinger"], correct: "most interesting", exp: "Interesting là tính từ dài, so sánh nhất dùng the most." },
      { question: "He plays tennis ___ than his brother.", options: ["good", "better", "best", "more good"], correct: "better", exp: "Good là tính từ bất quy tắc, so sánh hơn là better." },
      { question: "Russia is the ___ country in the world.", options: ["large", "larger", "largest", "most large"], correct: "largest", exp: "Large -> largest trong so sánh nhất." },
      { question: "She is ___ intelligent than her sister.", options: ["more", "most", "much", "very"], correct: "more", exp: "So sánh hơn của tính từ dài dùng more." },
      { question: "Today is ___ than yesterday.", options: ["bad", "badder", "worse", "worst"], correct: "worse", exp: "Bad là tính từ bất quy tắc, so sánh hơn là worse." },
      { question: "This puzzle is ___ difficult than the last one.", options: ["less", "least", "fewer", "few"], correct: "less", exp: "So sánh kém hơn dùng less + tính từ dài." },
      { question: "He is ___ tallest boy in our class.", options: ["a", "an", "the", "no article"], correct: "the", exp: "Luôn dùng the trước so sánh nhất." },
      { question: "Health is ___ important than money.", options: ["more", "most", "much", "very"], correct: "more", exp: "Important là tính từ dài -> more." },
      { question: "Which is the ___ mountain in the world?", options: ["high", "higher", "highest", "most high"], correct: "highest", exp: "So sánh nhất của high là highest." }
    ]
  },
  {
    title: "Đảo Ngữ (Inversion)",
    formula: "Từ phủ định/hạn chế + Trợ động từ + S + V",
    explanation: "Khi các trạng từ phủ định hoặc bán phủ định (Never, Hardly, Seldom, Not only...) đứng đầu câu để nhấn mạnh, ta phải đảo trợ động từ lên trước chủ ngữ.",
    example: "Never have I seen such a beautiful sunset.",
    commonMistakes: "Quên đảo trợ động từ, hoặc chia sai thì của động từ chính sau khi đã mượn trợ động từ.",
    quizzes: [
      { question: "Never ___ such a terrible mistake.", options: ["I made", "did I make", "I did make", "made I"], correct: "did I make", exp: "Đảo ngữ với Never, mượn trợ động từ 'did'." },
      { question: "Hardly ___ the house when it started to rain.", options: ["had he left", "he had left", "he left", "did he leave"], correct: "had he left", exp: "Cấu trúc: Hardly + had + S + V3 + when + S + V2." },
      { question: "Not only ___ well, but she also dances beautifully.", options: ["she sings", "does she sing", "she does sing", "sings she"], correct: "does she sing", exp: "Đảo ngữ với Not only ở HTĐ dùng does." },
      { question: "Seldom ___ to the cinema.", options: ["we go", "do we go", "go we", "we do go"], correct: "do we go", exp: "Seldom mang nghĩa phủ định, cần đảo ngữ do we go." },
      { question: "No sooner ___ the door than the phone rang.", options: ["had I opened", "I had opened", "did I open", "I opened"], correct: "had I opened", exp: "No sooner + had + S + V3 + than + S + V2." },
      { question: "Only later ___ his true intention.", options: ["I realized", "did I realize", "realized I", "I did realize"], correct: "did I realize", exp: "Đảo ngữ với Only later." },
      { question: "Under no circumstances ___ the door.", options: ["you should open", "should you open", "you open", "open you"], correct: "should you open", exp: "Under no circumstances yêu cầu đảo ngữ." },
      { question: "Not until yesterday ___ the truth.", options: ["I knew", "did I know", "I did know", "knew I"], correct: "did I know", exp: "Not until + time/clause + đảo ngữ mệnh đề chính." },
      { question: "So fast ___ that we couldn't catch him.", options: ["he ran", "did he run", "ran he", "he did run"], correct: "did he run", exp: "Cấu trúc So + adj/adv + trợ động từ + S + V." },
      { question: "At no time ___ she complain about the work.", options: ["did", "does", "do", "has"], correct: "did", exp: "Đảo ngữ với At no time (quá khứ)." }
    ]
  },
  {
    title: "Danh Động Từ (Gerund - V-ing)",
    formula: "V-ing đóng vai trò như một danh từ",
    explanation: "Được dùng làm chủ ngữ, tân ngữ sau một số động từ (enjoy, avoid, mind...) hoặc sau giới từ (in, on, at, about...).",
    example: "Swimming is good for your health. / I enjoy reading books.",
    commonMistakes: "Dùng to-V sau giới từ thay vì V-ing.",
    quizzes: [
      { question: "___ is my favorite hobby.", options: ["Read", "Reads", "Reading", "To reading"], correct: "Reading", exp: "V-ing (Reading) làm chủ ngữ." },
      { question: "She enjoys ___ to music.", options: ["listen", "to listen", "listening", "listened"], correct: "listening", exp: "Sau enjoy + V-ing." },
      { question: "He gave up ___ two years ago.", options: ["smoke", "to smoke", "smoking", "smoked"], correct: "smoking", exp: "Sau giới từ (up) phải dùng V-ing." },
      { question: "Would you mind ___ the window?", options: ["open", "to open", "opening", "opened"], correct: "opening", exp: "Sau mind + V-ing." },
      { question: "I look forward to ___ you again.", options: ["see", "seeing", "saw", "seen"], correct: "seeing", exp: "Look forward to + V-ing (đây là giới từ to, không phải to infinitive)." },
      { question: "They suggested ___ by train.", options: ["travel", "to travel", "traveling", "traveled"], correct: "traveling", exp: "Sau suggest + V-ing." },
      { question: "She is afraid of ___ out alone in the dark.", options: ["go", "to go", "going", "went"], correct: "going", exp: "Sau giới từ of + V-ing." },
      { question: "We can't avoid ___ mistakes.", options: ["make", "to make", "making", "made"], correct: "making", exp: "Sau avoid + V-ing." },
      { question: "He left without ___ goodbye.", options: ["say", "to say", "saying", "said"], correct: "saying", exp: "Sau giới từ without + V-ing." },
      { question: "Do you feel like ___ a cup of coffee?", options: ["have", "to have", "having", "had"], correct: "having", exp: "Cấu trúc feel like + V-ing." }
    ]
  },
  {
    title: "Động Từ To-Infinitive (To V)",
    formula: "To + V nguyên mẫu",
    explanation: "Thường đứng sau một số động từ (want, decide, hope, promise...) hoặc dùng để chỉ mục đích.",
    example: "I want to buy a new car. / He went out to buy some milk.",
    commonMistakes: "Dùng V-ing thay vì To V sau các động từ như want, decide.",
    quizzes: [
      { question: "I want ___ an engineer.", options: ["become", "becoming", "to become", "became"], correct: "to become", exp: "Sau want + to V." },
      { question: "She decided ___ abroad for studying.", options: ["go", "going", "to go", "went"], correct: "to go", exp: "Sau decide + to V." },
      { question: "They hope ___ the match tomorrow.", options: ["win", "winning", "to win", "won"], correct: "to win", exp: "Sau hope + to V." },
      { question: "He promised not ___ anyone my secret.", options: ["tell", "telling", "to tell", "told"], correct: "to tell", exp: "Sau promise + to V, phủ định là not to V." },
      { question: "I'm calling ___ a reservation.", options: ["make", "making", "to make", "made"], correct: "to make", exp: "Chỉ mục đích (để làm gì) dùng To V." },
      { question: "It is difficult ___ this problem.", options: ["solve", "solving", "to solve", "solved"], correct: "to solve", exp: "Cấu trúc: It is + adj + to V." },
      { question: "She learned how ___ the piano.", options: ["play", "playing", "to play", "played"], correct: "to play", exp: "Từ để hỏi (how, what, where) + to V." },
      { question: "They agreed ___ the proposal.", options: ["accept", "accepting", "to accept", "accepted"], correct: "to accept", exp: "Sau agree + to V." },
      { question: "We didn't know what ___.", options: ["do", "doing", "to do", "did"], correct: "to do", exp: "Sau từ để hỏi 'what' dùng to V." },
      { question: "I forgot ___ the door when I left.", options: ["lock", "locking", "to lock", "locked"], correct: "to lock", exp: "Forget to V: quên phải làm việc gì (chưa làm)." }
    ]
  }
];

async function seedAdvancedGrammar2() {
  const pool = mysql.createPool({
    host: 'localhost',
    user: 'root',
    password: '123456',
    database: 'minlish_db',
    charset: 'utf8mb4'
  });

  const userId = 1;
  console.log('Bắt đầu chèn dữ liệu ngữ pháp nâng cao phần 2...');

  for (const topic of advancedGrammarData2) {
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

  console.log('Hoàn tất phần 2!');
  process.exit(0);
}

seedAdvancedGrammar2().catch(console.error);
