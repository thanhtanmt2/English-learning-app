const mysql = require('mysql2/promise');

const grammarData = [
  {
    title: "Thì Hiện Tại Đơn (Present Simple)",
    formula: "S + V(s/es) + O",
    explanation: "Dùng để diễn tả một thói quen, một hành động lặp đi lặp lại hoặc một chân lý, sự thật hiển nhiên.",
    example: "She goes to school every day. / The sun rises in the East.",
    commonMistakes: "Quên thêm s/es vào động từ khi chủ ngữ là ngôi thứ 3 số ít (he, she, it).",
    quizzes: [
      { question: "He ___ to school every day.", options: ["go", "goes", "going", "went"], correct: "goes", exp: "Chủ ngữ 'He' ngôi thứ 3 số ít nên động từ thêm 'es'." },
      { question: "They ___ playing football.", options: ["likes", "like", "liking", "liked"], correct: "like", exp: "Chủ ngữ 'They' số nhiều nên động từ nguyên mẫu." },
      { question: "Water ___ at 100 degrees Celsius.", options: ["boil", "boils", "boiling", "boiled"], correct: "boils", exp: "Sự thật hiển nhiên, chủ ngữ 'Water' không đếm được tính là số ít." },
      { question: "I usually ___ up at 6 AM.", options: ["get", "gets", "getting", "got"], correct: "get", exp: "Chủ ngữ 'I' đi với động từ nguyên mẫu." },
      { question: "___ she live in London?", options: ["Do", "Does", "Is", "Are"], correct: "Does", exp: "Câu hỏi thì HTĐ với chủ ngữ 'she' mượn trợ động từ 'Does'." },
      { question: "My mother ___ not work on Sundays.", options: ["do", "does", "is", "has"], correct: "does", exp: "Câu phủ định thì HTĐ, chủ ngữ số ít dùng 'does not'." },
      { question: "Cats ___ milk.", options: ["drinks", "drink", "drinking", "drank"], correct: "drink", exp: "Chủ ngữ 'Cats' số nhiều nên động từ nguyên mẫu." },
      { question: "The train ___ at 8 PM.", options: ["leave", "leaves", "leaving", "left"], correct: "leaves", exp: "Lịch trình tàu xe dùng thì HTĐ, 'train' số ít." },
      { question: "___ you like pizza?", options: ["Do", "Does", "Are", "Have"], correct: "Do", exp: "Câu hỏi với 'you' dùng trợ động từ 'Do'." },
      { question: "She always ___ her homework.", options: ["do", "does", "doing", "did"], correct: "does", exp: "Trạng từ 'always', chủ ngữ 'She' -> does." }
    ]
  },
  {
    title: "Thì Quá Khứ Đơn (Past Simple)",
    formula: "S + V2/ed + O",
    explanation: "Diễn tả hành động đã xảy ra và chấm dứt hoàn toàn trong quá khứ.",
    example: "I visited my grandparents last weekend.",
    commonMistakes: "Quên đưa động từ về quá khứ (V2/ed) hoặc nhầm lẫn động từ bất quy tắc.",
    quizzes: [
      { question: "I ___ a great movie yesterday.", options: ["see", "saw", "seeing", "seen"], correct: "saw", exp: "Yesterday (hôm qua) -> Quá khứ đơn của see là saw." },
      { question: "They ___ to Paris in 2019.", options: ["go", "goes", "went", "gone"], correct: "went", exp: "Năm 2019 ở quá khứ -> went." },
      { question: "She ___ not study hard for the exam.", options: ["do", "does", "did", "was"], correct: "did", exp: "Trợ động từ quá khứ đơn ở câu phủ định là 'did'." },
      { question: "___ you play tennis last Sunday?", options: ["Do", "Did", "Were", "Are"], correct: "Did", exp: "Last Sunday -> Câu hỏi dùng 'Did'." },
      { question: "We ___ very tired after the trip.", options: ["were", "was", "are", "did"], correct: "were", exp: "To be ở quá khứ với chủ ngữ 'We' là 'were'." },
      { question: "He ___ a new car last month.", options: ["buy", "buys", "bought", "buying"], correct: "bought", exp: "Last month -> V2 của buy là bought." },
      { question: "The dog ___ loudly last night.", options: ["bark", "barks", "barked", "barking"], correct: "barked", exp: "Động từ có quy tắc thêm -ed." },
      { question: "What ___ you do yesterday?", options: ["do", "did", "are", "were"], correct: "did", exp: "Câu hỏi quá khứ dùng trợ động từ did." },
      { question: "I ___ my keys this morning.", options: ["lose", "loses", "lost", "losing"], correct: "lost", exp: "This morning (đã qua) -> quá khứ lost." },
      { question: "They ___ happy with the result.", options: ["wasn't", "weren't", "didn't", "aren't"], correct: "weren't", exp: "Phủ định của to be số nhiều ở quá khứ là weren't." }
    ]
  },
  {
    title: "Mạo từ (Articles: a, an, the)",
    formula: "A/An + Danh từ đếm được số ít (chưa xác định); The + Danh từ xác định",
    explanation: "'A/An' dùng cho danh từ đếm được số ít, nhắc đến lần đầu. 'An' đứng trước nguyên âm (u,e,o,a,i). 'The' dùng cho danh từ đã xác định hoặc duy nhất.",
    example: "I have an apple. The apple is red.",
    commonMistakes: "Dùng 'a/an' với danh từ số nhiều hoặc không đếm được. Dùng 'the' bừa bãi.",
    quizzes: [
      { question: "I saw ___ elephant at the zoo.", options: ["a", "an", "the", "no article"], correct: "an", exp: "Elephant bắt đầu bằng nguyên âm 'e'." },
      { question: "___ sun is shining brightly today.", options: ["A", "An", "The", "No article"], correct: "The", exp: "Mặt trời là duy nhất nên dùng The." },
      { question: "She is ___ doctor.", options: ["a", "an", "the", "no article"], correct: "a", exp: "Chỉ nghề nghiệp dùng a/an." },
      { question: "I need ___ hour to finish this.", options: ["a", "an", "the", "no article"], correct: "an", exp: "Chữ 'h' trong 'hour' bị câm, âm đầu là nguyên âm." },
      { question: "Can you pass me ___ salt, please?", options: ["a", "an", "the", "no article"], correct: "the", exp: "Lọ muối trên bàn cả 2 người đều biết rõ, dùng the." },
      { question: "He plays ___ piano very well.", options: ["a", "an", "the", "no article"], correct: "the", exp: "Trước tên nhạc cụ dùng The." },
      { question: "We visited ___ Paris last year.", options: ["a", "an", "the", "no article"], correct: "no article", exp: "Không dùng mạo từ trước tên thành phố/quốc gia." },
      { question: "I bought a book. ___ book is very interesting.", options: ["A", "An", "The", "No article"], correct: "The", exp: "Quyển sách được nhắc đến lần thứ 2 nên dùng The." },
      { question: "She is carrying ___ umbrella.", options: ["a", "an", "the", "no article"], correct: "an", exp: "Umbrella bắt đầu bằng nguyên âm 'u'." },
      { question: "Honesty is ___ best policy.", options: ["a", "an", "the", "no article"], correct: "the", exp: "Trước so sánh nhất luôn dùng The." }
    ]
  },
  {
    title: "Câu Điều Kiện Loại 1 (Conditional Type 1)",
    formula: "If + S + V(hiện tại), S + will + V(nguyên mẫu)",
    explanation: "Diễn tả một điều kiện có thể xảy ra ở hiện tại hoặc tương lai.",
    example: "If it rains, I will stay at home.",
    commonMistakes: "Dùng 'will' ở cả hai vế (If it will rain, I will stay... -> Sai).",
    quizzes: [
      { question: "If you study hard, you ___ the exam.", options: ["pass", "will pass", "passed", "would pass"], correct: "will pass", exp: "Vế chính của ĐK loại 1 dùng will + V." },
      { question: "If it ___, we will cancel the trip.", options: ["rains", "will rain", "rained", "rain"], correct: "rains", exp: "Vế If của ĐK loại 1 dùng thì Hiện tại đơn." },
      { question: "She will be late if she ___ hurry.", options: ["don't", "doesn't", "won't", "didn't"], correct: "doesn't", exp: "Vế If ở hiện tại đơn, chủ ngữ 'she' dùng 'doesn't'." },
      { question: "If I have enough money, I ___ a new phone.", options: ["buy", "bought", "will buy", "would buy"], correct: "will buy", exp: "Vế chính dùng will + V." },
      { question: "We will go to the beach if the weather ___ nice.", options: ["is", "be", "will be", "was"], correct: "is", exp: "Vế If dùng thì hiện tại đơn." },
      { question: "If you ___ early, you'll miss the train.", options: ["don't leave", "won't leave", "not leave", "didn't leave"], correct: "don't leave", exp: "Vế If dùng hiện tại đơn (don't leave)." },
      { question: "What will you do if you ___ the keys?", options: ["lose", "will lose", "lost", "loses"], correct: "lose", exp: "Vế If dùng hiện tại đơn (lose)." },
      { question: "If he calls me, I ___ him the truth.", options: ["tell", "will tell", "told", "tells"], correct: "will tell", exp: "Vế chính dùng will." },
      { question: "They will be angry if we ___ late.", options: ["are", "will be", "were", "is"], correct: "are", exp: "Vế If dùng hiện tại đơn (are)." },
      { question: "If you mix blue and yellow, you ___ green.", options: ["get", "will get", "got", "would get"], correct: "get", exp: "Sự thật hiển nhiên có thể dùng HTĐ ở cả 2 vế (Zero Conditional) hoặc ĐK1." }
    ]
  },
  {
    title: "Thì Hiện Tại Tiếp Diễn (Present Continuous)",
    formula: "S + am/is/are + V-ing + O",
    explanation: "Diễn tả hành động đang xảy ra ngay tại thời điểm nói hoặc một kế hoạch trong tương lai gần.",
    example: "I am studying English right now.",
    commonMistakes: "Quên to be (am/is/are) hoặc dùng sai cho các động từ chỉ nhận thức (know, understand).",
    quizzes: [
      { question: "Listen! The bird ___.", options: ["sing", "sings", "is singing", "sang"], correct: "is singing", exp: "Listen! -> Dấu hiệu hành động đang xảy ra." },
      { question: "I ___ a letter to my friend at the moment.", options: ["write", "am writing", "writes", "writing"], correct: "am writing", exp: "At the moment -> Hiện tại tiếp diễn." },
      { question: "They ___ football in the yard now.", options: ["play", "plays", "are playing", "playing"], correct: "are playing", exp: "Now -> Hiện tại tiếp diễn, 'They' đi với 'are'." },
      { question: "She ___ to music right now.", options: ["listens", "is listening", "listening", "listened"], correct: "is listening", exp: "Right now -> Hiện tại tiếp diễn." },
      { question: "We ___ TV tonight.", options: ["watch", "are watching", "watches", "watched"], correct: "are watching", exp: "Kế hoạch tương lai gần có thể dùng HTTD." },
      { question: "Look! The bus ___.", options: ["comes", "is coming", "come", "coming"], correct: "is coming", exp: "Look! -> Hành động đang xảy ra." },
      { question: "He ___ a shower at present.", options: ["takes", "is taking", "take", "taking"], correct: "is taking", exp: "At present = bây giờ." },
      { question: "What ___ you ___?", options: ["do/do", "are/doing", "did/do", "is/doing"], correct: "are/doing", exp: "Hỏi ai đó đang làm gì." },
      { question: "I ___ (not) sleeping, I'm reading.", options: ["am not", "don't", "isn't", "aren't"], correct: "am not", exp: "Phủ định của 'I am' là 'I am not'." },
      { question: "___ she working today?", options: ["Do", "Does", "Is", "Are"], correct: "Is", exp: "Câu hỏi HTTD đảo to be lên trước." }
    ]
  }
];

async function seedData() {
  const pool = mysql.createPool({
    host: 'localhost',
    user: 'root',
    password: '123456',
    database: 'minlish_db',
    charset: 'utf8mb4'
  });

  const userId = 1; // Demo User / Global

  console.log('Bắt đầu chèn dữ liệu ngữ pháp...');

  for (const topic of grammarData) {
    // Check if topic exists
    const [existing] = await pool.query('SELECT id FROM grammar_notes WHERE title = ? AND user_id = ?', [topic.title, userId]);
    let noteId;

    if (existing.length === 0) {
      const [result] = await pool.query(
        'INSERT INTO grammar_notes (user_id, title, formula, explanation, example, common_mistake) VALUES (?, ?, ?, ?, ?, ?)',
        [userId, topic.title, topic.formula, topic.explanation, topic.example, topic.commonMistakes]
      );
      noteId = result.insertId;
      console.log(`Đã thêm bài học: ${topic.title}`);
    } else {
      noteId = existing[0].id;
      console.log(`Bài học đã tồn tại: ${topic.title}`);
    }

    // Insert 10 quizzes
    for (const q of topic.quizzes) {
      const [exQuiz] = await pool.query('SELECT id FROM grammar_quizzes WHERE question = ? AND grammar_note_id = ?', [q.question, noteId]);
      if (exQuiz.length === 0) {
        await pool.query(
          'INSERT INTO grammar_quizzes (grammar_note_id, question, option_a, option_b, option_c, option_d, correct_answer, explanation) VALUES (?, ?, ?, ?, ?, ?, ?, ?)',
          [noteId, q.question, q.options[0], q.options[1], q.options[2], q.options[3], q.correct, q.exp]
        );
      }
    }
    console.log(`Đã thêm 10 câu trắc nghiệm cho bài: ${topic.title}`);
  }

  console.log('Hoàn tất toàn bộ!');
  process.exit(0);
}

seedData().catch(err => {
  console.error(err);
  process.exit(1);
});
