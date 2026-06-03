package com.example.english_learning_app

/**
 * Cấu hình API cho ứng dụng MinLish.
 *
 * - Khi test trên Android Emulator: dùng BASE_URL_EMULATOR (10.0.2.2 trỏ về máy tính thật)
 * - Khi test trên điện thoại thật:  dùng BASE_URL_REAL_DEVICE
 *   (thay xxx bằng IP WiFi máy tính của bạn, lấy bằng lệnh `ipconfig` trên Windows)
 *
 * Cách lấy IP WiFi: mở CMD → gõ `ipconfig` → tìm dòng "IPv4 Address"
 */
object AppConfig {

    // ✅ Dùng cái này khi chạy trên Emulator (Máy ảo)
    const val BASE_URL = "http://10.0.2.2:3000/api/"

    // 💡 Dùng cái này khi chạy trên điện thoại thật (cùng mạng WiFi với máy tính)
    // const val BASE_URL = "http://192.168.1.xxx:3000/api/v1/"
}
