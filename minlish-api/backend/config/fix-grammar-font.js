// Script fix lỗi font tiếng Việt trong bảng grammar_notes
// Chạy: node config/fix-grammar-font.js

const mysql = require('mysql2/promise');
require('dotenv').config();

// Mapping: chuỗi bị lỗi font → chuỗi tiếng Việt đúng
// Áp dụng cho cột title (chứa Sentence Type) và explanation (chứa Meaning VI)
const titleReplacements = {
  'Kh?ng d?nh': 'Khẳng định',
  'Ph? d?nh': 'Phủ định',
  'Nghi v?n': 'Nghi vấn',
};

const explanationReplacements = {
  'Thói quen, s? th?t hi?n nhiên': 'Thói quen, sự thật hiển nhiên',
  'Hành d?ng dang x?y ra lúc nói': 'Hành động đang xảy ra lúc nói',
  'Hành d?ng v?a xong ho?c liên quan hi?n t?i': 'Hành động vừa xong hoặc liên quan hiện tại',
  'Hành d?ng b?t d?u trong QK và v?n ti?p di?n': 'Hành động bắt đầu trong QK và vẫn tiếp diễn',
  'Hành d?ng dã x?y ra và k?t thúc trong quá kh?': 'Hành động đã xảy ra và kết thúc trong quá khứ',
  'Hành d?ng dang x?y ra t?i m?t th?i di?m trong QK': 'Hành động đang xảy ra tại một thời điểm trong QK',
  'Hành d?ng x?y ra tru?c m?t hành d?ng khác trong QK': 'Hành động xảy ra trước một hành động khác trong QK',
  'Hành d?ng kéo dài d?n m?t th?i di?m trong QK': 'Hành động kéo dài đến một thời điểm trong QK',
  'Quy?t d?nh t?c thì ho?c d? doán tuong lai': 'Quyết định tức thì hoặc dự đoán tương lai',
  'K? ho?ch ho?c d? d?nh dã chu?n b?': 'Kế hoạch hoặc dự định đã chuẩn bị',
  'Hành d?ng s? dang x?y ra t?i m?t th?i di?m tuong lai': 'Hành động sẽ đang xảy ra tại một thời điểm tương lai',
  'Hành d?ng hoàn thành tru?c m?t th?i di?m tuong lai': 'Hành động hoàn thành trước một thời điểm tương lai',
  'Hành d?ng liên t?c d?n m?t th?i di?m nh?t d?nh trong tuong lai': 'Hành động liên tục đến một thời điểm nhất định trong tương lai',
  'S? th?t hi?n nhiên, quy lu?t t? nhiên': 'Sự thật hiển nhiên, quy luật tự nhiên',
  'Ði?u ki?n có th? x?y ra trong tuong lai': 'Điều kiện có thể xảy ra trong tương lai',
  'Ði?u ki?n không có th?c ? hi?n t?i': 'Điều kiện không có thực ở hiện tại',
  'Ði?u ki?n không có th?c trong quá kh?': 'Điều kiện không có thực trong quá khứ',
  'K?t h?p di?u ki?n QK (if) v?i k?t qu? HT (would)': 'Kết hợp điều kiện QK (if) với kết quả HT (would)',
  'Kh? nang HT (can) ho?c QK/l?ch s? (could)': 'Khả năng HT (can) hoặc QK/lịch sự (could)',
  'B?t bu?c (must not=c?m; don\'t have to=không c?n)': 'Bắt buộc (must not=cấm; don\'t have to=không cần)',
  'L?i khuyên ho?c di?u nên làm': 'Lời khuyên hoặc điều nên làm',
  'Kh? nang x?y ra ho?c xin phép l?ch s?': 'Khả năng xảy ra hoặc xin phép lịch sự',
  'Thói quen ho?c tr?ng thái trong QK (nay không còn n?a)': 'Thói quen hoặc trạng thái trong QK (nay không còn nữa)',
  'B? d?ng – Hi?n t?i don': 'Bị động – Hiện tại đơn',
  'B? d?ng – Hi?n t?i ti?p di?n': 'Bị động – Hiện tại tiếp diễn',
  'B? d?ng – Quá kh? don': 'Bị động – Quá khứ đơn',
  'B? d?ng – Quá kh? ti?p di?n': 'Bị động – Quá khứ tiếp diễn',
  'B? d?ng – Quá kh? hoàn thành': 'Bị động – Quá khứ hoàn thành',
  'B? d?ng – Hi?n t?i hoàn thành': 'Bị động – Hiện tại hoàn thành',
  'B? d?ng – Tuong lai don (will)': 'Bị động – Tương lai đơn (will)',
  'B? d?ng – Tuong lai (going to)': 'Bị động – Tương lai (going to)',
  'B? d?ng – Tuong lai ti?p di?n (hi?m, dùng trong van vi?t)': 'Bị động – Tương lai tiếp diễn (hiếm, dùng trong văn viết)',
  'Câu tr?n thu?t và câu h?i Yes/No gián ti?p': 'Câu trần thuật và câu hỏi Yes/No gián tiếp',
  'Câu h?i Wh gián ti?p': 'Câu hỏi Wh gián tiếp',
  'U?c mu?n trái v?i th?c t? ? hi?n t?i': 'Ước muốn trái với thực tế ở hiện tại',
  'U?c mu?n trái v?i th?c t? trong quá kh?': 'Ước muốn trái với thực tế trong quá khứ',
  'U?c mu?n v? s? thay d?i trong tuong lai': 'Ước muốn về sự thay đổi trong tương lai',
  'Câu h?i v? ch? ng? – không d?o tr? d?ng t?': 'Câu hỏi về chủ ngữ – không đảo trợ động từ',
  'Câu h?i v? tân ng? – có d?o tr? d?ng t?': 'Câu hỏi về tân ngữ – có đảo trợ động từ',
  'Câu h?i gián ti?p l?ch s? – gi? nguyên tr?t t? S+V': 'Câu hỏi gián tiếp lịch sự – giữ nguyên trật tự S+V',
  'Câu h?i duôi – d?o chi?u kh?ng d?nh/ph? d?nh': 'Câu hỏi đuôi – đảo chiều khẳng định/phủ định',
  'Danh d?ng t? làm ch? ng? ho?c tân ng?': 'Danh động từ làm chủ ngữ hoặc tân ngữ',
  'Dùng di?n t? m?c dích ho?c sau tính t?': 'Dùng diễn tả mục đích hoặc sau tính từ',
  'M?nh d? quan h? xác d?nh (không có d?u ph?y)': 'Mệnh đề quan hệ xác định (không có dấu phẩy)',
  'M?nh d? quan h? không xác d?nh (có d?u ph?y)': 'Mệnh đề quan hệ không xác định (có dấu phẩy)',
  'So sánh hon – tính t? ng?n thêm -er, dài dùng more': 'So sánh hơn – tính từ ngắn thêm -er, dài dùng more',
  'So sánh nh?t – dùng the + -est ho?c the most': 'So sánh nhất – dùng the + -est hoặc the most',
  'Nh?n m?nh hai d?i tu?ng cùng lúc': 'Nhấn mạnh hai đối tượng cùng lúc',
  'L?a ch?n (either/or) ho?c ph? d?nh c? hai (neither/nor)': 'Lựa chọn (either/or) hoặc phủ định cả hai (neither/nor)',
  'Di?n t? s? nhu?ng b? – m?nh d? trái nghia': 'Diễn tả sự nhượng bộ – mệnh đề trái nghĩa',
  'Nhu?ng b? – theo sau là danh t? ho?c V-ing (không có S+V)': 'Nhượng bộ – theo sau là danh từ hoặc V-ing (không có S+V)',
  'too...to = quá...nên không; enough...to = d?...d?': 'too...to = quá...nên không; enough...to = đủ...để',
  'so...that / such...that = d?n m?c mà...': 'so...that / such...that = đến mức mà...',
  'a/an = chua xác d?nh; the = dã xác d?nh; zero = chung chung': 'a/an = chưa xác định; the = đã xác định; zero = chung chung',
  'Danh t? d?m du?c dùng many/few; không d?m du?c dùng much/little': 'Danh từ đếm được dùng many/few; không đếm được dùng much/little',
  'Dùng do/does/did tru?c V d? nh?n m?nh s? th?t': 'Dùng do/does/did trước V để nhấn mạnh sự thật',
  'Câu ch? – nh?n m?nh m?t thành ph?n trong câu': 'Câu chẻ – nhấn mạnh một thành phần trong câu',
  'B? d?ng – Quá kh? hoàn thành ti?p di?n (r?t hi?m dùng)': 'Bị động – Quá khứ hoàn thành tiếp diễn (rất hiếm dùng)',
  'B? d?ng – Tuong lai hoàn thành': 'Bị động – Tương lai hoàn thành',
  'B? d?ng v?i d?ng t? khuy?t thi?u (can/must/should/may...)': 'Bị động với động từ khuyết thiếu (can/must/should/may...)',
  'Câu m?nh l?nh/yêu c?u gián ti?p': 'Câu mệnh lệnh/yêu cầu gián tiếp',
  'C?u trúc sai khi?n – nh?/b?t/d? ai làm gì': 'Cấu trúc sai khiến – nhờ/bắt/để ai làm gì',
  'Ð?n lúc ph?i làm gì; nh?n m?nh s? c?n thi?t': 'Đến lúc phải làm gì; nhấn mạnh sự cần thiết',
  'Ðã d?n lúc ph?i làm gì; nh?n m?nh s? c?n thi?t': 'Đã đến lúc phải làm gì; nhấn mạnh sự cần thiết',
  'would rather = thích hon; had better = nên (có hàm ý c?nh báo)': 'would rather = thích hơn; had better = nên (có hàm ý cảnh báo)',
  'prefer = thích hon (nói chung); would prefer = thích hon (tình hu?ng c? th?)': 'prefer = thích hơn (nói chung); would prefer = thích hơn (tình huống cụ thể)',
  'Ð?o ng? d? nh?n m?nh – dùng v?i tr?ng t?/c?m ph? d?nh d?ng d?u': 'Đảo ngữ để nhấn mạnh – dùng với trạng từ/cụm phủ định đứng đầu',
  'M?nh d? phân t? – rút g?n m?nh d? có cùng ch? ng?': 'Mệnh đề phân từ – rút gọn mệnh đề có cùng chủ ngữ',
  'So = tôi cung v?y (d?ng ý kh?ng d?nh); Neither = tôi cung không (d?ng ý ph? d?nh)': 'So = tôi cũng vậy (đồng ý khẳng định); Neither = tôi cũng không (đồng ý phủ định)',
  'So sánh b?ng – hai d?i tu?ng có m?c d? nhu nhau': 'So sánh bằng – hai đối tượng có mức độ như nhau',
  'Càng...càng – hai so sánh hon liên quan d?n nhau': 'Càng...càng – hai so sánh hơn liên quan đến nhau',
  'Di?n t? nguyên nhân và k?t qu?': 'Diễn tả nguyên nhân và kết quả',
  'Di?n t? m?c dích c?a hành d?ng': 'Diễn tả mục đích của hành động',
  'Di?n t? k?t qu?/h?u qu? c?a hành d?ng tru?c dó': 'Diễn tả kết quả/hậu quả của hành động trước đó',
  'at = th?i di?m chính xác; on = ngày/th?; in = tháng/nam/mùa/kho?ng th?i gian': 'at = thời điểm chính xác; on = ngày/thứ; in = tháng/năm/mùa/khoảng thời gian',
  'at = v? trí c? th?; on = trên b? m?t; in = bên trong': 'at = vị trí cụ thể; on = trên bề mặt; in = bên trong',
  'some = m?t s? (kh?ng d?nh, d? ngh?); any = nào cung (nghi v?n/ph? d?nh); no = không có': 'some = một số (khẳng định, đề nghị); any = nào cũng (nghi vấn/phủ định); no = không có',
};

async function fix() {
  const pool = mysql.createPool({
    host: process.env.DB_HOST || 'localhost',
    port: process.env.DB_PORT || 3306,
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASSWORD || '123456',
    database: process.env.DB_NAME || 'minlish_db',
    charset: 'utf8mb4'
  });

  console.log('Đang kết nối database...\n');

  // 1. Fix cột title (chứa Sentence Type trong ngoặc)
  let fixedTitle = 0;
  for (const [broken, correct] of Object.entries(titleReplacements)) {
    const [result] = await pool.execute(
      `UPDATE grammar_notes SET title = REPLACE(title, ?, ?) WHERE title LIKE ?`,
      [broken, correct, `%${broken}%`]
    );
    if (result.affectedRows > 0) {
      fixedTitle += result.affectedRows;
      console.log(`✓ Title: "${broken}" → "${correct}" (${result.affectedRows} dòng)`);
    }
  }

  // 2. Fix cột explanation (chứa Meaning VI)
  let fixedExplanation = 0;
  for (const [broken, correct] of Object.entries(explanationReplacements)) {
    const [result] = await pool.execute(
      `UPDATE grammar_notes SET explanation = ? WHERE explanation = ?`,
      [correct, broken]
    );
    if (result.affectedRows > 0) {
      fixedExplanation += result.affectedRows;
      console.log(`✓ Explanation: "${broken.substring(0, 40)}..." → "${correct.substring(0, 40)}..." (${result.affectedRows} dòng)`);
    }
  }

  console.log(`\n✅ Đã fix ${fixedTitle} dòng title, ${fixedExplanation} dòng explanation!`);
  process.exit(0);
}

fix().catch(err => { console.error('Lỗi:', err.message); process.exit(1); });
