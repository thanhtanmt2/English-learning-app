const { execSync } = require('child_process');

console.log('====================================================');
console.log(' BẮT ĐẦU CÀI ĐẶT DATABASE VÀ DỮ LIỆU MẪU (SEED)');
console.log('====================================================\n');

try {
  console.log('--- Bước 1: Khởi tạo Database & Bảng ---');
  // Chạy file create-db.js để tạo database minlish và import schema.sql
  execSync('node scripts/db-create.js', { stdio: 'inherit', cwd: require('path').join(__dirname, '..') });

  console.log('\n--- Bước 2: Đổ dữ liệu mẫu (Seed Data) ---');
  // Chạy file seed.js để thêm từ vựng, ngữ pháp, user test
  execSync('node scripts/seed.js', { stdio: 'inherit', cwd: require('path').join(__dirname, '..') });

  console.log('\n--- Bước 3: Tạo bài tập trắc nghiệm Ngữ pháp (Quizzes) ---');
  execSync('node scripts/generate-quizzes.js', { stdio: 'inherit', cwd: require('path').join(__dirname, '..') });

  console.log('\n--- Bước 4: Thêm dữ liệu Ngữ pháp nâng cao ---');
  execSync('node scripts/seed-full-grammar.js', { stdio: 'inherit', cwd: require('path').join(__dirname, '..') });
  execSync('node scripts/seed-adv-grammar-1.js', { stdio: 'inherit', cwd: require('path').join(__dirname, '..') });
  execSync('node scripts/seed-adv-grammar-2.js', { stdio: 'inherit', cwd: require('path').join(__dirname, '..') });
  execSync('node scripts/seed-adv-grammar-3.js', { stdio: 'inherit', cwd: require('path').join(__dirname, '..') });

  console.log('\n--- Bước 5: Sửa lỗi Font chữ Tiếng Việt (Từ vựng, User) ---');
  execSync('node scripts/fix-vietnamese.js', { stdio: 'inherit', cwd: require('path').join(__dirname, '..') });

  console.log('\n--- Bước 6: Sửa lỗi Font Ngữ pháp (Grammar Font Fix) ---');
  execSync('node scripts/fix-grammar-font.js', { stdio: 'inherit', cwd: require('path').join(__dirname, '..') });

  console.log('\n--- Bước 7: Sửa lỗi Font Quiz Câu hỏi (Quiz Question Fix) ---');
  execSync('node scripts/fix-grammar.js', { stdio: 'inherit', cwd: require('path').join(__dirname, '..') });

  console.log('\n--- Bước 8: Tạo lại Quiz Câu hỏi chuẩn (Quiz Question Generation) ---');
  execSync('node scripts/fix-grammar2.js', { stdio: 'inherit', cwd: require('path').join(__dirname, '..') });

  console.log('\n====================================================');
  console.log(' ✅ QUÁ TRÌNH CÀI ĐẶT HOÀN TẤT THÀNH CÔNG!');
  console.log(' 🚀 Bây giờ bạn có thể chạy "npm run dev" để bật server.');
  console.log('====================================================\n');
} catch (error) {
  console.error('\n❌ QUÁ TRÌNH CÀI ĐẶT THẤT BẠI. Vui lòng kiểm tra lại lỗi bên trên.');
  process.exit(1);
}
