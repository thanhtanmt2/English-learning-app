// Script fix lỗi font tiếng Việt trong database
// Chạy: node fix-vietnamese.js

const mysql = require('mysql2/promise');

const map = {
  "hello":"xin chào","goodbye":"tạm biệt","thank you":"cảm ơn","please":"vui lòng",
  "water":"nước","food":"thức ăn","house":"nhà","book":"sách","eat":"ăn","drink":"uống",
  "sleep":"ngủ","work":"làm việc","happy":"vui","sad":"buồn","big":"lớn","small":"nhỏ",
  "beautiful":"đẹp","good":"tốt","mother":"mẹ","friend":"bạn","love":"yêu thích",
  "hate":"ghét","like":"thích","family":"gia đình","school":"trường học",
  "teacher":"giáo viên","student":"học sinh","doctor":"bác sĩ","hospital":"bệnh viện",
  "music":"âm nhạc","song":"bài hát","dance":"nhảy","play":"chơi","run":"chạy",
  "walk":"đi bộ","jump":"nhảy","sit":"ngồi","stand":"đứng","ride":"đi xe",
  "car":"ô tô","bike":"xe đạp","tree":"cây","flower":"hoa","sun":"mặt trời",
  "moon":"mặt trăng","star":"sao","sky":"bầu trời","cloud":"mây","rain":"mưa",
  "snow":"tuyết","fire":"lửa","ice":"đá","wind":"gió","mountain":"núi","river":"sông",
  "sea":"biển","beach":"bãi biển","city":"thành phố","country":"nước","money":"tiền",
  "time":"thời gian","hour":"giờ","day":"ngày","night":"đêm","morning":"sáng",
  "afternoon":"chiều","red":"đỏ","blue":"xanh da trời","green":"xanh lá","apple":"táo",
  "dog":"chó","cat":"mèo","bird":"chim","fish":"cá","table":"bàn","chair":"ghế",
  "door":"cửa","window":"cửa sổ","phone":"điện thoại","computer":"máy tính",
  "shirt":"áo sơ mi","shoe":"giày","bag":"túi","pen":"bút","paper":"giấy",
  "road":"con đường","bridge":"cây cầu","market":"chợ","garden":"vườn","speak":"nói",
  "write":"viết","read":"đọc","listen":"nghe","cook":"nấu ăn","clean":"dọn dẹp",
  "open":"mở","close":"đóng","study":"học","teach":"dạy","buy":"mua","sell":"bán",
  "come":"đến","go":"đi","give":"cho","take":"lấy","young":"trẻ","old":"già / cũ",
  "tall":"cao","short":"thấp / ngắn","fast":"nhanh","slow":"chậm","hot":"nóng",
  "cold":"lạnh","easy":"dễ","difficult":"khó","always":"luôn luôn",
  "never":"không bao giờ","sometimes":"đôi khi","very":"rất","father":"bố",
  "brother":"anh/em trai","sister":"chị/em gái","child":"đứa trẻ","eye":"mắt",
  "hand":"bàn tay","head":"đầu","heart":"trái tim","hair":"tóc","rice":"cơm / gạo",
  "bread":"bánh mì","egg":"trứng","milk":"sữa","coffee":"cà phê","tea":"trà",
  "room":"phòng","floor":"sàn / tầng","wall":"bức tường","key":"chìa khóa",
  "light":"ánh sáng / đèn","airport":"sân bay","train":"tàu hỏa","bus":"xe buýt",
  "ticket":"vé","newspaper":"báo","help":"giúp đỡ","call":"gọi điện","think":"nghĩ",
  "know":"biết","want":"muốn","need":"cần","try":"cố gắng","find":"tìm","meet":"gặp",
  "send":"gửi","ask":"hỏi","answer":"trả lời","forget":"quên","remember":"nhớ",
  "arrive":"đến nơi","new":"mới","free":"miễn phí / tự do","busy":"bận rộn",
  "tired":"mệt mỏi","hungry":"đói","strong":"mạnh mẽ","dark":"tối",
  "quiet":"yên tĩnh","wrong":"sai","correct":"đúng","name":"tên","number":"số",
  "color":"màu sắc","street":"đường phố","shop":"cửa hàng","class":"lớp học",
  "sport":"thể thao","movie":"phim","hotel":"khách sạn","office":"văn phòng",
  "letter":"lá thư","price":"giá cả","job":"công việc","photo":"ảnh",
  "language":"ngôn ngữ","weather":"thời tiết","animal":"động vật","idea":"ý tưởng",
  "problem":"vấn đề","place":"nơi chốn","way":"cách, đường","word":"từ",
  "minute":"phút","year":"năm","week":"tuần","age":"tuổi","size":"kích cỡ",
  "dream":"giấc mơ","smile":"mỉm cười","cry":"khóc","laugh":"cười lớn",
  "wait":"chờ đợi","stop":"dừng lại","start":"bắt đầu","finish":"hoàn thành",
  "travel":"du lịch","return":"trở về","change":"thay đổi","choose":"lựa chọn",
  "use":"sử dụng","watch":"xem, nhìn","learn":"học","tell":"kể, nói",
  "show":"chỉ, cho xem","bring":"mang đến","keep":"giữ","move":"di chuyển",
  "pay":"trả tiền","dirty":"bẩn","sick":"bệnh","healthy":"khỏe mạnh",
  "safe":"an toàn","dangerous":"nguy hiểm","rich":"giàu có","poor":"nghèo",
  "long":"dài","wide":"rộng","heavy":"nặng","late":"muộn, trễ","early":"sớm",
  "right":"đúng, phải","left":"trái","full":"no, đầy","empty":"rỗng",
  "same":"giống nhau","different":"khác nhau","together":"cùng nhau",
  "again":"lại, một lần nữa","also":"cũng","here":"ở đây","there":"ở đó",
  "now":"bây giờ","today":"hôm nay","yesterday":"hôm qua","tomorrow":"ngày mai",
  "maybe":"có lẽ","map":"bản đồ","test":"bài kiểm tra","game":"trò chơi",
  "message":"tin nhắn","picture":"bức tranh","internet":"internet",
  "email":"thư điện tử","website":"trang web","password":"mật khẩu",
  "download":"tải xuống","elephant":"con voi","tiger":"con hổ","rabbit":"con thỏ",
  "yellow":"màu vàng","white":"màu trắng","black":"màu đen","orange":"màu cam",
  "pink":"màu hồng","dress":"váy đầm","jacket":"áo khoác","hat":"cái mũ",
  "glasses":"kính mắt","fruit":"trái cây","vegetable":"rau củ","soup":"súp / canh",
  "sugar":"đường","salt":"muối","meat":"thịt","bed":"cái giường","kitchen":"nhà bếp",
  "bathroom":"phòng tắm","mirror":"gương","nose":"cái mũi","mouth":"cái miệng",
  "ear":"cái tai","tooth":"cái răng","leg":"cái chân","university":"trường đại học",
  "exam":"kỳ thi","homework":"bài tập về nhà","library":"thư viện","salary":"lương",
  "bank":"ngân hàng","meeting":"cuộc họp","airplane":"máy bay","passport":"hộ chiếu",
  "concert":"buổi hòa nhạc","hobby":"sở thích","vacation":"kỳ nghỉ",
  "earthquake":"động đất","flood":"lũ lụt","forest":"rừng","storm":"bão",
  "angry":"tức giận"
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

  console.log('Đang kết nối database...');
  
  let updated = 0;
  for (const [eng, vie] of Object.entries(map)) {
    const [result] = await pool.execute(
      'UPDATE words SET meaning = ? WHERE LOWER(word) = ?',
      [vie, eng.toLowerCase()]
    );
    if (result.affectedRows > 0) {
      updated += result.affectedRows;
      console.log(`✓ ${eng} → ${vie}`);
    }
  }

  console.log(`\n✅ Đã fix ${updated} từ!`);
  process.exit(0);
}

fix().catch(err => { console.error('Lỗi:', err.message); process.exit(1); });
