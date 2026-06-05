const { execSync } = require('child_process');
const path = require('path');

const run = (script) => execSync(`node ${script}`, {
  stdio: 'inherit',
  cwd: path.join(__dirname, '..'),
});

console.log('====================================================');
console.log(' BẮT ĐẦU CÀI ĐẶT DATABASE VÀ DỮ LIỆU MẪU (SEED)');
console.log('====================================================\n');

try {
  console.log('--- Bước 1: Khởi tạo Database & Bảng ---');
  run('scripts/db-create.js');

  console.log('\n--- Bước 2: Seed từ vựng + ngữ pháp cơ bản (CSV) ---');
  run('scripts/seed.js');

  console.log('\n--- Bước 3: Seed ngữ pháp nâng cao + quizzes + patch encoding ---');
  run('scripts/seed-grammar.js');

  console.log('\n====================================================');
  console.log(' ✅ QUÁ TRÌNH CÀI ĐẶT HOÀN TẤT THÀNH CÔNG!');
  console.log(' 🚀 Bây giờ bạn có thể chạy "npm run dev" để bật server.');
  console.log('====================================================\n');
} catch {
  console.error('\n❌ QUÁ TRÌNH CÀI ĐẶT THẤT BẠI. Vui lòng kiểm tra lại lỗi bên trên.');
  process.exit(1);
}
