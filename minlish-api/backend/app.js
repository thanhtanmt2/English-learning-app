const express = require('express');
const cors    = require('cors');
require('dotenv').config();

const app = express();

// Middleware — phải đặt trước routes
app.use(cors());
app.use(express.json());

// Thêm dòng log này để kiểm tra kết nối
app.use((req, res, next) => {
  console.log(`>>> [${new Date().toLocaleTimeString()}] ${req.method} ${req.url}`);
  next();
});

require('./config/db');

// Routes — sẽ thêm dần sau
app.use('/api/auth',     require('./routes/auth'));
app.use('/api',          require('./routes/vocab'));
app.use('/api',          require('./routes/grammar'));
app.use('/api',          require('./routes/progress'));

// Health check — test xem server có chạy không
app.get('/', (req, res) => {
  res.json({ message: 'MinLish API đang chạy!' });
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server chạy tại http://localhost:${PORT}`);
});