const { execSync } = require('child_process');

console.log('====================================================');
console.log(' BẮT ĐẦU CÀI ĐẶT DATABASE VÀ DỮ LIỆU MẪU (SEED)');
console.log('====================================================\n');

try {
  console.log('--- Bước 1: Khởi tạo Database & Bảng ---');
  // Chạy file create-db.js để tạo database minlish và import schema.sql
  execSync('node create-db.js', { stdio: 'inherit' });

  console.log('\n--- Bước 2: Đổ dữ liệu mẫu (Seed Data) ---');
  // Chạy file seed.js để thêm từ vựng, ngữ pháp, user test
  execSync('node config/seed.js', { stdio: 'inherit' });

  console.log('\n====================================================');
  console.log(' ✅ QUÁ TRÌNH CÀI ĐẶT HOÀN TẤT THÀNH CÔNG!');
  console.log(' 🚀 Bây giờ bạn có thể chạy "npm run dev" để bật server.');
  console.log('====================================================\n');
} catch (error) {
  console.error('\n❌ QUÁ TRÌNH CÀI ĐẶT THẤT BẠI. Vui lòng kiểm tra lại lỗi bên trên.');
  process.exit(1);
}
