# Tài liệu Kiến trúc & Flow Code — English Learning App

> **Dành cho:** Tất cả thành viên trong team — đọc file này để hiểu toàn bộ codebase trước khi debug hoặc thêm tính năng mới.

---

## Mục lục

1. [Tổng quan kiến trúc](#1-tổng-quan-kiến-trúc)
2. [Sơ đồ luồng dữ liệu](#2-sơ-đồ-luồng-dữ-liệu)
3. [Backend — Database Schema](#3-backend--database-schema)
4. [Backend — API Endpoints](#4-backend--api-endpoints)
5. [Backend — Business Logic](#5-backend--business-logic)
6. [Tầng Data Android — Models, Network, Repository](#6-tầng-data-android--models-network-repository)
7. [Tầng Data Android — Local Storage](#7-tầng-data-android--local-storage)
8. [Dependency Injection (Hilt)](#8-dependency-injection-hilt)
9. [Xác thực (Authentication)](#9-xác-thực-authentication)
10. [Điều hướng (Navigation)](#10-điều-hướng-navigation)
11. [Background Workers & Notifications](#11-background-workers--notifications)
12. [Tính năng: Từ vựng (Vocabulary)](#12-tính-năng-từ-vựng-vocabulary)
13. [Tính năng: Học — Flashcard & Dictation](#13-tính-năng-học--flashcard--dictation)
14. [Tính năng: Ngữ pháp (Grammar + Quiz)](#14-tính-năng-ngữ-pháp-grammar--quiz)
15. [Tính năng: Tiến độ & Dashboard (Home)](#15-tính-năng-tiến-độ--dashboard-home)
16. [Tính năng: AI Word Generator](#16-tính-năng-ai-word-generator)
17. [Tính năng: Cài đặt & Hồ sơ (Me)](#17-tính-năng-cài-đặt--hồ-sơ-me)
18. [Thuật toán SM-2 (Spaced Repetition)](#18-thuật-toán-sm-2-spaced-repetition)
19. [Lỗi tiềm ẩn cần lưu ý](#19-lỗi-tiềm-ẩn-cần-lưu-ý)

---

## 1. Tổng quan kiến trúc

App áp dụng mô hình **MVVM (Model–View–ViewModel)** kết hợp **Clean Architecture** theo kiểu đơn giản hóa.

```
┌─────────────────────────────────────────────┐
│  UI Layer (Jetpack Compose)                 │
│  Screens + ViewModels                       │
│  → chỉ hiển thị uiState, không có logic    │
└────────────────┬────────────────────────────┘
                 │ observe StateFlow
┌────────────────▼────────────────────────────┐
│  Domain / Business Logic                    │
│  ViewModels (HiltViewModel)                 │
│  → xử lý logic, gọi Repository             │
└────────────────┬────────────────────────────┘
                 │ suspend fun calls
┌────────────────▼────────────────────────────┐
│  Data Layer                                 │
│  Repositories → ApiService (Retrofit)       │
│             ↓                               │
│  Local: Room DB + Preferences              │
│  TokenManager (EncryptedSharedPreferences)  │
└─────────────────────────────────────────────┘
```

**Nguyên tắc cốt lõi:**

- **Screen** (Composable) chỉ được đọc `uiState` và gọi hàm ViewModel — không chứa logic nghiệp vụ.
- **ViewModel** xử lý logic, gọi Repository/ApiService, cập nhật `_uiState`.
- **Repository** là lớp trung gian giữa ViewModel và API/DB — giúp ViewModel không biết HTTP hay SQL đang được dùng.
- **Offline support**: Khi không có mạng, SM-2 review được đưa vào hàng đợi `PendingReviewEntity` (Room), tự đồng bộ khi có mạng lại.

**Tech stack:**

| Thành phần           | Thư viện / Chi tiết                                     |
| -------------------- | ------------------------------------------------------- |
| UI                   | Jetpack Compose + Material3                             |
| Navigation           | Navigation Compose                                      |
| State management     | Kotlin StateFlow + Coroutines                           |
| Dependency Injection | Hilt 2.56 (cấu hình trong `AppModule.kt`)               |
| HTTP Client          | Retrofit 2.11 + OkHttp                                  |
| JSON Parsing         | Gson                                                    |
| Local DB             | Room v3 (WordSet, Word, GrammarNote, PendingReview)     |
| Local Storage        | EncryptedSharedPreferences (Token) + DataStore (các settings) |
| Background Tasks     | WorkManager (DailyReminder, QuizReminder, ProgressUpdate) |
| AI                   | Google Generative AI — Gemini 2.5 Flash                 |
| Auth Google          | Credential Manager + GoogleId                           |
| Backend              | Node.js + Express 5 + MySQL2 + JWT + Nodemailer         |

> **Lưu ý:** `RetrofitClient.kt` và `AppConfig.kt` đã bị **xóa**. Tất cả initialization hiện nằm trong `di/AppModule.kt`.

---

## 2. Sơ đồ luồng dữ liệu

### Luồng tổng quát (mọi tính năng đều theo mẫu này)

```
User nhấn nút
    │
    ▼
Screen gọi viewModel.someFunction()
    │
    ▼
ViewModel cập nhật: _uiState.update { it.copy(isLoading = true) }
    │
    ▼
ViewModel gọi: repository.doSomething() [suspend, chạy trên coroutine]
    │
    ▼
Repository gọi: apiService.someEndpoint()
    │   └── Nếu offline → lưu vào Room (PendingReviewDao) thay vì gửi API
    │
    ▼
Retrofit + OkHttp gửi HTTP request
    │        ▲
    │        └── AuthInterceptor tự động chèn "Authorization: Bearer $token"
    │
    ▼
Backend (Node.js/Express) xác thực JWT, xử lý logic, trả về JSON
    │
    ▼
Gson parse JSON → Kotlin data class
    │
    ▼
Repository trả về kết quả cho ViewModel
    │
    ▼
ViewModel: _uiState.update { it.copy(isLoading = false, data = result) }
    │
    ▼
Screen tự động re-render nhờ collectAsState()
```

### Luồng Token (JWT)

```
Đăng nhập thành công
    │
    ▼
AuthViewModel.login() nhận authResponse.token
    │
    ▼
TokenManager.saveToken(token)
    ├── Lưu vào RAM (cachedToken) — dùng cho request ngay lập tức
    └── Lưu vào EncryptedSharedPreferences — persist khi tắt app (AES256-GCM)
    │
    ▼
Mỗi request tiếp theo:
AuthInterceptor.intercept()
    ├── Bỏ qua nếu là /auth/* route
    └── Đọc TokenManager.getToken() → thêm header "Authorization: Bearer ..."
```

---

## 3. Backend — Database Schema

Backend dùng **MySQL** với charset **utf8mb4** để hỗ trợ tiếng Việt.

### Bảng `users`

| Column       | Type         | Ghi chú                              |
| ------------ | ------------ | ------------------------------------ |
| id           | INT, PK, AI  |                                      |
| name         | VARCHAR(100) | NOT NULL                             |
| email        | VARCHAR(150) | NOT NULL, UNIQUE                     |
| password     | VARCHAR(255) | NULL (Google login không có password) |
| google_id    | VARCHAR(255) | UNIQUE, dùng cho Google OAuth        |
| avatar       | VARCHAR(255) |                                      |
| goal         | VARCHAR(100) | DEFAULT 'general'                    |
| level        | VARCHAR(50)  | Trình độ tiếng Anh                   |
| created_at   | TIMESTAMP    | DEFAULT CURRENT_TIMESTAMP            |

### Bảng `word_sets`

| Column      | Type         | Ghi chú                                   |
| ----------- | ------------ | ----------------------------------------- |
| id          | INT, PK, AI  |                                           |
| user_id     | INT, FK      | NULL = bộ từ mặc định (is_default = TRUE) |
| name        | VARCHAR(100) | NOT NULL                                  |
| description | VARCHAR(255) |                                           |
| is_default  | BOOLEAN      | DEFAULT FALSE                             |
| created_at  | TIMESTAMP    |                                           |

### Bảng `words` (có SM-2 columns)

| Column               | Type         | Ghi chú                              |
| -------------------- | ------------ | ------------------------------------ |
| id                   | INT, PK, AI  |                                      |
| word_set_id          | INT, FK      | NOT NULL → word_sets.id (cascade)    |
| word                 | VARCHAR(100) | Từ tiếng Anh                         |
| meaning              | TEXT         | Nghĩa tiếng Việt                     |
| pronunciation        | VARCHAR(100) |                                      |
| example              | TEXT         | Câu ví dụ                            |
| part_of_speech       | VARCHAR(50)  | Loại từ (noun, verb, ...)            |
| v2_past              | VARCHAR(100) | Dạng V2 (quá khứ đơn)               |
| v3_past_participle   | VARCHAR(100) | Dạng V3                              |
| plural_form          | VARCHAR(100) |                                      |
| description          | TEXT         |                                      |
| collocation          | TEXT         |                                      |
| synonyms             | TEXT         |                                      |
| antonyms             | TEXT         |                                      |
| cefr_level           | VARCHAR(10)  | A1, A2, B1, B2, C1, C2               |
| topic                | VARCHAR(100) |                                      |
| quiz_question        | TEXT         |                                      |
| **ease_factor**      | FLOAT        | **SM-2** — DEFAULT 2.5               |
| **interval_days**    | INT          | **SM-2** — DEFAULT 1                 |
| **next_review_date** | DATE         | **SM-2** — DEFAULT CURRENT_DATE      |
| created_at           | TIMESTAMP    |                                      |

### Bảng `grammar_notes`

| Column         | Type         | Ghi chú                       |
| -------------- | ------------ | ----------------------------- |
| id             | INT, PK, AI  |                               |
| user_id        | INT, FK      | NULL = note mặc định          |
| title          | VARCHAR(150) | NOT NULL                      |
| formula        | TEXT         | Công thức ngữ pháp            |
| explanation    | TEXT         |                               |
| example        | TEXT         |                               |
| common_mistake | TEXT         | Lỗi thường gặp                |
| is_default     | BOOLEAN      | DEFAULT FALSE                 |
| created_at     | TIMESTAMP    |                               |

### Bảng `grammar_quizzes`

| Column          | Type        | Ghi chú                  |
| --------------- | ----------- | ------------------------ |
| id              | INT, PK, AI |                          |
| grammar_note_id | INT, FK     | → grammar_notes.id       |
| question        | TEXT        | NOT NULL                 |
| option_a/b/c/d  | VARCHAR(255)| NOT NULL                 |
| correct_answer  | CHAR(1)     | 'A' \| 'B' \| 'C' \| 'D' |
| explanation     | TEXT        |                          |
| created_at      | TIMESTAMP   |                          |

### Bảng `user_progress` (học hằng ngày)

| Column        | Type | Ghi chú                                         |
| ------------- | ---- | ----------------------------------------------- |
| id            | INT  |                                                 |
| user_id       | INT  | FK → users                                      |
| study_date    | DATE | NOT NULL — UNIQUE với user_id (1 record/ngày)   |
| words_studied | INT  | Tổng từ đã review trong ngày                    |
| correct_count | INT  | Số từ đúng (quality >= 3)                       |

### Bảng `user_grammar_progress`

| Column          | Type | Ghi chú                               |
| --------------- | ---- | ------------------------------------- |
| user_id         | INT  | FK → users                            |
| grammar_note_id | INT  | FK → grammar_notes                    |
| highest_score   | INT  | Điểm cao nhất đạt được               |
| total_questions | INT  | Tổng số câu hỏi khi đạt điểm cao nhất |
| UNIQUE          |      | (user_id, grammar_note_id)            |

### Bảng `notification_settings`

| Column           | Type    | Ghi chú                  |
| ---------------- | ------- | ------------------------ |
| user_id          | INT     | FK → users, UNIQUE       |
| daily_reminder   | BOOLEAN | DEFAULT TRUE             |
| reminder_time    | TIME    | DEFAULT '08:00:00'       |
| quiz_reminders   | BOOLEAN | DEFAULT TRUE             |
| progress_updates | BOOLEAN | DEFAULT TRUE             |

### Bảng `otp_codes`

| Column     | Type                        | Ghi chú                        |
| ---------- | --------------------------- | ------------------------------ |
| id         | INT, PK                     |                                |
| email      | VARCHAR(150)                |                                |
| code       | VARCHAR(6)                  | 6 chữ số ngẫu nhiên            |
| type       | ENUM('register','reset_password') |                         |
| expires_at | DATETIME                    | Hết hạn sau 10 phút            |
| used       | BOOLEAN                     | DEFAULT FALSE                  |

---

## 4. Backend — API Endpoints

Base URL: `http://<server>:3000/api/`

Tất cả endpoints (trừ `/auth/*` và `/users/*`) đều yêu cầu header:
```
Authorization: Bearer <JWT_TOKEN>
```

### Auth (`/auth`)

| Method | Endpoint                   | Mô tả                                    |
| ------ | -------------------------- | ---------------------------------------- |
| POST   | `/auth/login`              | Đăng nhập email/password                 |
| POST   | `/auth/register`           | Đăng ký trực tiếp (không OTP)            |
| POST   | `/auth/google`             | Đăng nhập Google (gửi idToken)           |
| POST   | `/auth/register/send-otp`  | Gửi OTP 6 chữ số tới email để đăng ký   |
| POST   | `/auth/register/verify-otp`| Xác minh OTP nhận được                   |
| POST   | `/auth/register/complete`  | Hoàn tất đăng ký sau khi OTP hợp lệ     |
| POST   | `/auth/forgot-password`    | Gửi OTP reset mật khẩu                  |
| POST   | `/auth/reset-password`     | Đặt lại mật khẩu bằng OTP               |

### Users

| Method | Endpoint      | Mô tả                                      |
| ------ | ------------- | ------------------------------------------ |
| PATCH  | `/users/{id}` | Cập nhật profile (name, goal, level)       |

### Vocabulary

| Method | Endpoint                | Mô tả                                      |
| ------ | ----------------------- | ------------------------------------------ |
| GET    | `/wordsets`             | Danh sách bộ từ (kèm total/learned counts) |
| POST   | `/wordsets`             | Tạo bộ từ mới                              |
| GET    | `/wordsets/{id}`        | Chi tiết 1 bộ từ                           |
| PUT    | `/wordsets/{id}`        | Cập nhật bộ từ                             |
| DELETE | `/wordsets/{id}`        | Xóa bộ từ (cascade xóa words)             |
| GET    | `/wordsets/{id}/words`  | Lấy từ trong bộ từ cụ thể                  |
| GET    | `/words`                | Lấy tất cả từ user có quyền truy cập       |
| GET    | `/words/{id}`           | Lấy 1 từ theo ID                           |
| POST   | `/words`                | Thêm từ mới vào bộ từ                      |
| PUT    | `/words/{id}`           | Cập nhật từ                                |
| DELETE | `/words/{id}`           | Xóa từ                                     |
| GET    | `/words/review`         | Lấy từ cần ôn hôm nay (SM-2)              |
| POST   | `/words/{id}/review`    | Gửi kết quả ôn tập — cập nhật SM-2         |

### Grammar

| Method | Endpoint                  | Mô tả                                   |
| ------ | ------------------------- | --------------------------------------- |
| GET    | `/grammar`                | Danh sách bài học + điểm quiz (LEFT JOIN)|
| POST   | `/grammar`                | Thêm bài học mới                        |
| GET    | `/grammar/{id}`           | Chi tiết 1 bài học                      |
| PUT    | `/grammar/{id}`           | Cập nhật bài học                        |
| DELETE | `/grammar/{id}`           | Xóa bài học                             |
| GET    | `/quiz_questions?noteId=` | 10 câu hỏi ngẫu nhiên (lọc theo noteId) |
| POST   | `/grammar/{id}/score`     | Nộp điểm quiz (chỉ lưu nếu điểm mới cao hơn) |

### Progress

| Method | Endpoint             | Mô tả                                       |
| ------ | -------------------- | ------------------------------------------- |
| GET    | `/progress`          | Tổng quan: streak, learnedWords, accuracy, dailyActivity |
| GET    | `/progress_history`  | Lịch sử chi tiết theo ngày                  |

### Notifications

| Method | Endpoint                    | Mô tả                        |
| ------ | --------------------------- | ---------------------------- |
| GET    | `/notifications/settings`   | Lấy cài đặt thông báo        |
| PUT    | `/notifications/settings`   | Cập nhật cài đặt thông báo   |

---

## 5. Backend — Business Logic

### Access Control cho WordSets/Words

```
User có thể truy cập một word set khi:
    ├── word_set.user_id == req.user.id  (set của chính họ)
    └── word_set.is_default == TRUE       (set mặc định của app)

Tương tự với Grammar Notes (user_id == req.user.id OR is_default)
Default notes có user_id = NULL
```

### SM-2 trên Backend (vocabularyController.reviewWord)

```javascript
// Input: quality (0-5)
// Lấy ease_factor và interval_days hiện tại từ DB

// 1. Cập nhật interval
if (quality >= 3) {
    if (interval === 0)     interval = 1
    else if (interval === 1) interval = 6
    else                    interval = Math.round(interval * easeFactor)
} else {
    interval = 1  // sai → ôn lại ngay hôm sau
}

// 2. Cập nhật ease factor
easeFactor = easeFactor + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02))
if (easeFactor < 1.3) easeFactor = 1.3  // không nhỏ hơn 1.3

// 3. Tính ngày ôn tiếp theo
next_review_date = TODAY + interval ngày

// 4. Ghi progress hằng ngày (upsert)
INSERT INTO user_progress (user_id, study_date, words_studied, correct_count)
VALUES (?, TODAY, 1, quality >= 3 ? 1 : 0)
ON DUPLICATE KEY UPDATE
    words_studied = words_studied + 1,
    correct_count = correct_count + (quality >= 3 ? 1 : 0)
```

### Tính Streak

```
Lấy tất cả study_date của user ORDER BY DESC
Duyệt từ hôm nay ngược về quá khứ:
    ├── Nếu có record ngày hôm đó → streak++
    └── Không có → dừng lại

Streak = số ngày liên tiếp có học (tính đến hôm nay)
```

### Tính Accuracy

```
Tổng correct_count / tổng words_studied trong 7 ngày gần nhất × 100%
```

### Grammar Quiz Score

```
POST /grammar/{id}/score gọi submitQuizScore(noteId, score, total)
    │
    ├── Nếu chưa có record → INSERT (highest_score = score)
    └── Nếu đã có record   → UPDATE chỉ khi score > highest_score (giữ điểm cao nhất)
```

### OTP Flow

```
generateOtp() → 6 chữ số ngẫu nhiên
saveOtp(email, code, type)
    ├── Xóa OTP cũ chưa dùng của email này
    └── INSERT mới với expires_at = NOW() + 10 phút

verifyOtp(email, code, type)
    └── Kiểm tra: exists, not expired, not used → mark used = TRUE
```

---

## 6. Tầng Data Android — Models, Network, Repository

### 6.1 Data Models (`data/model/`)

#### Auth Models

```kotlin
data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String, val goal: String)
data class AuthResponse(val token: String, val user: User)
data class User(val id: String, val name: String, val email: String, val goal: String?, val level: String?, val avatarUrl: String?)
data class UpdateProfilePayload(val name: String, val goal: String?, val level: String?)
// OTP + Reset Password:
data class ResetPasswordRequest(val email: String, val otp: String, val newPassword: String)
data class MessageResponse(val message: String)
```

> **Tại sao `UpdateProfilePayload` không có email/id?** Backend endpoint PATCH `/users/{id}` — nếu gửi email thì có thể ghi đè email bằng chuỗi rỗng. Dùng payload riêng để chỉ gửi các field cho phép thay đổi.

#### Vocabulary Models

```kotlin
data class Word(
    val id: Int,
    val wordSetId: Int,
    val word: String,
    val meaning: String,
    val example: String?,
    val pronunciation: String?,
    val partOfSpeech: String?,
    val easeFactor: Double?,
    val intervalDays: Int?,
    val nextReviewDate: String?
) {
    // isLearned = true khi intervalDays > 1 (đã ôn tập thành công ≥ 1 lần)
    val isLearned: Boolean get() = (intervalDays ?: 1) > 1
}

data class WordSet(
    val id: Int,
    val userId: String?,
    val name: String,
    val description: String?,
    val totalWords: Int?,
    val learnedWords: Int?,
    val isDefault: Boolean?
)

data class AiGeneratedWord(val word: String, val meaning: String, val example: String?)
```

> **Tại sao `intervalDays ?: 1`?** Khi từ mới tạo, backend chưa có SM-2 data nên trả `null`. Giả định interval = 1 → `isLearned = false`.

#### Grammar Models

```kotlin
@Parcelize
data class GrammarNote(
    val id: String,
    val title: String,
    val category: String?,
    val level: String?,
    val formula: String,
    val explanation: String,
    val example: String,
    val commonMistakes: String,
    val easeFactor: Double,
    val interval: Int,
    val nextReviewDate: String?,
    val highestScore: Int?,
    val totalQuestions: Int?
) : Parcelable

data class QuizQuestion(
    val id: Int,
    val grammarNoteId: Int,
    val question: String,
    val optionA: String, val optionB: String, val optionC: String, val optionD: String,
    val correctAnswer: String,
    val explanation: String?,
    val difficulty: String?
)
```

> **`@Parcelize`**: Cho phép truyền `GrammarNote` qua Navigation `savedStateHandle` như Parcelable.

#### Progress Models

```kotlin
data class ProgressOverview(
    val streak: Int,
    val accuracyRate: Int,
    val learnedWords: Int,
    val totalWords: Int,
    val reviewToday: Int,
    val dailyActivity: List<ProgressRecord>
)

data class ProgressRecord(
    val date: String,       // "2025-06-03"
    val studyTime: Int,     // phút học (= wordsLearned * 1.5 — backend tính)
    val wordsLearned: Int,
    val quizScore: Int
)
```

### 6.2 Network Layer (`data/remote/`)

#### `AuthInterceptor.kt`

```
Mỗi HTTP request
    │
    ├── path chứa /auth/ ?
    │       └── YES → cho qua (không cần token)
    │       └── NO  → lấy token từ TokenManager.getToken()
    │                   ├── null/rỗng → gửi không có token (sẽ nhận 401)
    │                   └── có token  → thêm header "Authorization: Bearer $token"
    │
    ▼
chain.proceed(request)
```

#### `ApiService.kt`

Interface Retrofit định nghĩa toàn bộ 33 endpoints. Xem bảng đầy đủ ở [Mục 4](#4-backend--api-endpoints).

Lưu ý kỹ thuật:
- Tất cả `suspend fun` — dùng với Coroutines
- `@Body` cho POST/PUT, `@Path` cho route params, `@Query` cho query string

### 6.3 Repository Layer (`data/repository/`)

#### `VocabularyRepository`

```
loadWordSets()          → apiService.getWordSets()
loadWords(wordSetId)    → apiService.getWordsInSet(id) hoặc getWords()
createWordSet/updateWordSet/deleteWordSet → CRUD trên /wordsets
createWord/updateWord/deleteWord         → CRUD trên /words
submitReview(wordId, quality)
    ├── Có mạng → apiService.reviewWord(id, {"quality": quality})
    └── Mất mạng → lưu vào PendingReviewDao (Room)
syncPendingReviews()    → gửi các review đang pending khi có mạng lại
```

#### `GrammarRepository`

```
getGrammarNotes()           → apiService.getGrammarNotes()
getQuizQuestions(noteId)    → apiService.getGrammarQuizzes(noteId)
addGrammarNote/updateGrammarNote/deleteGrammarNote → CRUD /grammar
submitQuizScore(id, score, total) → apiService.submitGrammarQuizScore(id, payload)
```

#### `HomeRepository`

```
loadHomeData()
    └── apiService.getProgressOverview() → ProgressOverview
```

---

## 7. Tầng Data Android — Local Storage

### 7.1 Room Database — `AppDatabase` (version 3)

Các Entity:

| Entity              | Mô tả                                        |
| ------------------- | -------------------------------------------- |
| `WordSetEntity`     | Cache danh sách bộ từ offline                |
| `WordEntity`        | Cache từ vựng offline                         |
| `GrammarNoteEntity` | Cache bài học ngữ pháp offline               |
| `PendingReviewEntity` | Hàng đợi SM-2 review khi mất mạng          |

> **`fallbackToDestructiveMigration`**: Khi schema thay đổi mà không có migration, DB sẽ bị xóa và tạo lại. Chú ý khi nâng version Room.

### 7.2 TokenManager

```kotlin
// Dùng EncryptedSharedPreferences (AES256-GCM) — an toàn hơn plain SharedPreferences
@Volatile private var cachedToken: String? = null  // RAM cache để AuthInterceptor đọc nhanh

fun saveToken(token: String)  // lưu RAM + EncryptedSharedPreferences
fun getToken(): String?       // đọc từ RAM cache trước, fallback về EncryptedSharedPreferences
fun clearToken()              // xóa cả RAM và EncryptedSharedPreferences
```

### 7.3 Các Preference Managers

| Class                  | Storage   | Lưu gì                                   |
| ---------------------- | --------- | ---------------------------------------- |
| `LanguagePreferences`  | DataStore | Ngôn ngữ hiển thị (vi / en)              |
| `ThemePreferences`     | DataStore | Dark mode on/off                         |
| `ServerPreferences`    | DataStore | Base URL của server (hỗ trợ dev/prod)    |
| `ProfilePreferences`   | DataStore | Avatar URI của user                       |

---

## 8. Dependency Injection (Hilt)

```
@HiltAndroidApp MyApplication
    │
    ├── AppModule (@Module @InstallIn(SingletonComponent))
    │       ├── @Provides TokenManager (Singleton)
    │       ├── @Provides OkHttpClient + AuthInterceptor (Singleton)
    │       ├── @Provides Retrofit — base URL từ ServerPreferences (Singleton)
    │       ├── @Provides ApiService (Singleton)
    │       ├── @Provides VocabularyRepository(apiService) (Singleton)
    │       ├── @Provides HomeRepository(apiService) (Singleton)
    │       ├── @Provides GrammarRepository(apiService) (Singleton)
    │       ├── @Provides NotificationRepository(apiService) (Singleton)
    │       ├── @Provides AppDatabase (Room, Singleton)
    │       └── @Provides DAOs: WordSetDao, WordDao, GrammarNoteDao, PendingReviewDao
    │
    └── @AndroidEntryPoint MainActivity
            └── Hilt inject AuthViewModel, NavController, ...
```

Tất cả ViewModel dùng `@HiltViewModel + @Inject constructor`:

```kotlin
@HiltViewModel
class FlashcardViewModel @Inject constructor(
    private val repository: VocabularyRepository  // Hilt tự inject
) : ViewModel()
```

**`AuthViewModel`** đặc biệt — extends `AndroidViewModel` vì cần `Application` context cho `CredentialManager`:

```kotlin
class AuthViewModel @Inject constructor(
    application: Application,
    private val apiService: ApiService
) : AndroidViewModel(application)
```

---

## 9. Xác thực (Authentication)

### 9.1 Đăng nhập (Email/Password)

```
LoginScreen → User nhập email + password
    │
    ▼
AuthViewModel.login()
    ├── Validate: email rỗng? password rỗng? email thiếu "@"?
    │       └── Lỗi → cập nhật errorMessage
    ├── _uiState.update { isLoading = true }
    ├── apiService.login(LoginRequest(email, password))
    │
    ├── Thành công:
    │       tokenManager.saveToken(authResponse.token)
    │       _uiState.update { currentUser = user, isLoginSuccess = true }
    │
    └── Thất bại (HttpException):
            parseError(e) → đọc errorBody JSON → _uiState.update { errorMessage = "..." }
    │
    ▼
LoginScreen observe isLoginSuccess = true
    └── navigate("main") { popUpTo("login") { inclusive = true } }
        (xóa login khỏi back stack — không thể Back về màn login)
```

### 9.2 Đăng ký có OTP (flow khuyến nghị)

```
RegisterScreen → User nhập name, email, password, goal, level
    │
    │ Nhấn "Gửi OTP"
    ▼
AuthViewModel.sendRegisterOtp(email)
    └── POST /auth/register/send-otp → Backend gửi email chứa OTP 6 số
    │
    ▼
navigate("otp_verify/register")  — OtpVerifyScreen
    │
    │ User nhập OTP
    ▼
AuthViewModel.verifyRegisterOtp(email, otp)
    └── POST /auth/register/verify-otp → xác nhận OTP hợp lệ
    │
    ▼
AuthViewModel.registerWithOtp(name, email, password, otp, goal, level)
    └── POST /auth/register/complete → tạo tài khoản + trả JWT
    │
    ▼
tokenManager.saveToken(token) → navigate("main")
```

### 9.3 Quên mật khẩu

```
ForgotPasswordScreen → User nhập email
    │
    ▼
AuthViewModel.forgotPassword(email)
    └── POST /auth/forgot-password → gửi OTP qua email
    │
    ▼
navigate("otp_verify/reset_password")
    │
    │ User nhập OTP + mật khẩu mới
    ▼
AuthViewModel.resetPassword(email, otp, newPassword)
    └── POST /auth/reset-password
    │
    ▼
navigate("login") — đăng nhập lại với mật khẩu mới
```

### 9.4 Đăng nhập Google

```
LoginScreen → User nhấn "Đăng nhập Google"
    │
    ▼
CredentialManager.getCredential(GetGoogleIdOption(serverClientId = "..."))
    └── OS hiển thị Google Account Picker
    │
    ▼ User chọn account
GoogleIdTokenCredential.createFrom() → lấy idToken (JWT do Google ký)
    │
    ▼
AuthViewModel.loginWithGoogleReal(idToken)
    └── POST /auth/google {"idToken": idToken}
        └── Backend verify idToken với Google → upsert user → trả JWT của app
```

### 9.5 Đăng xuất

```
MeScreen → User nhấn "Đăng xuất"
    │
    ▼
AuthViewModel.logout()
    ├── tokenManager.clearToken()
    ├── _uiState.update { AuthUiState() }  — reset về state ban đầu
    └── CredentialManager.clearCredentialState()  — Google sign-out
    │
    ▼
AppNavHost observe currentUser = null
    └── navigate("login") { popUpTo(0) { inclusive = true } }
```

---

## 10. Điều hướng (Navigation)

### Cấu trúc 2 cấp

```
AppNavHost (rootNavController)
├── "login"                           → LoginScreen
├── "register"                        → RegisterScreen
├── "forgot_password"                 → ForgotPasswordScreen
├── "otp_verify/{mode}"               → OtpVerifyScreen (mode: register | reset_password)
│
├── "main"                            → MainWithBottomNav
│       ├── Tab HOME       → HomeScreen
│       ├── Tab VOCABULARY → WordSetListScreen
│       ├── Tab GRAMMAR    → GrammarListScreen  (auto-refresh khi tab được chọn lại)
│       └── Tab ME         → MeScreen
│
├── "add_edit_grammar?id={id}"        → AddEditGrammarScreen
├── "grammar_detail"                  → GrammarDetailScreen
├── "grammar_quiz/{noteId}"           → GrammarQuizScreen
├── "progress"                        → ProgressScreen
├── "progress_detail/{date}"          → ProgressDetailScreen
├── "word_set_list"                   → WordSetListScreen
├── "word_list/{wordSetId}"           → WordListScreen
├── "add_edit_word/{wordSetId}"       → AddEditWordScreen (tạo mới)
├── "add_edit_word/{wordSetId}/{wordId}" → AddEditWordScreen (sửa)
├── "flashcard?wordSetId={wordSetId}" → FlashcardScreen
├── "dictation?wordSetId={wordSetId}" → DictationScreen
├── "word_quiz?wordSetIds={...}&count={...}" → WordQuizScreen
├── "word_quiz_setup"                 → WordQuizSetupScreen
├── "add_word_set"                    → AddWordSetScreen
├── "ai_word_list"                    → AiWordListScreen
├── "edit_word_set/{wordSetId}"       → EditWordSetScreen
├── "edit_profile"                    → ProfileScreen
├── "language_settings"               → LanguageSettingsScreen
├── "notification_settings"           → NotificationSettingsScreen
├── "server_settings"                 → ServerSettingsScreen
└── "about_app"                       → AboutScreen
```

### Cách truyền dữ liệu giữa screens

**Cách 1 — Route argument (ID, số, chuỗi đơn giản):**

```kotlin
// Định nghĩa
route = "word_list/{wordSetId}"
arguments = listOf(navArgument("wordSetId") { type = NavType.StringType })
// Navigate
navController.navigate("word_list/$wordSetId")
// Nhận
backStackEntry.arguments?.getString("wordSetId")
```

**Cách 2 — savedStateHandle (object phức tạp như GrammarNote):**

```kotlin
// Gửi (GrammarListScreen)
navController.currentBackStackEntry?.savedStateHandle?.set("note", note)
navController.navigate("grammar_detail")

// Nhận (AppNavigation — GrammarDetailScreen)
val note = navController.previousBackStackEntry?.savedStateHandle?.get<GrammarNote>("note")
```

> `GrammarNote` phải `@Parcelize` để dùng được với savedStateHandle.

### Fix: Back navigation khi đổi Word Set trong Flashcard

**Vấn đề:** `word_list/1 → flashcard`, đổi sang WS 2, ấn Back → về `word_list/1` (sai).

**Giải pháp — callback `onWordSetChanged`:**

```kotlin
FlashcardScreen(
    wordSetId = ...,
    onWordSetChanged = { newId ->
        navController.navigate("word_list/$newId") {
            popUpTo("word_list/{wordSetId}") { inclusive = true }
        }
    }
)
```

Back stack sau khi đổi:
```
Trước: ... → word_list/1 → flashcard
Sau:   ... → word_list/2 → flashcard  ✓
```

---

## 11. Background Workers & Notifications

App dùng **WorkManager** để lên lịch nhắc nhở định kỳ.

### Các Worker

| Worker                  | Lịch chạy           | Nội dung thông báo            |
| ----------------------- | ------------------- | ----------------------------- |
| `DailyReminderWorker`   | Hằng ngày, 8 AM     | Nhắc học từ vựng mới           |
| `QuizReminderWorker`    | Hằng ngày, 8 PM     | Nhắc làm grammar quiz          |
| `ProgressUpdateWorker`  | Chủ nhật, 9 AM      | Tổng kết tiến độ tuần          |

Tất cả Workers đều `@HiltWorker` để có thể inject dependencies.

### `NotificationHelper.kt`

```kotlin
// Lên lịch nhắc học hằng ngày (có thể tùy chỉnh giờ)
NotificationHelper.scheduleReminder(context, time = "08:00")

// Lên lịch nhắc quiz 8 PM
NotificationHelper.scheduleQuizReminder(context)

// Lên lịch tổng kết tiến độ Chủ nhật 9 AM
NotificationHelper.scheduleProgressUpdate(context)
```

Dùng `PeriodicWorkRequest` với `ExistingPeriodicWorkPolicy.UPDATE` — thay thế work đang chờ nếu đã tồn tại (tránh duplicate).

### Flow khi User thay đổi cài đặt thông báo

```
NotificationSettingsScreen → User bật/tắt hoặc đổi giờ
    │
    ▼
NotificationSettingsViewModel.updateSettings()
    ├── PUT /notifications/settings → lưu lên backend
    └── NotificationHelper.scheduleReminder() hoặc cancel WorkManager task
```

---

## 12. Tính năng: Từ vựng (Vocabulary)

### 12.1 Danh sách bộ từ — WordSetListScreen

```
WordSetListScreen xuất hiện
    │
    ▼
LaunchedEffect(refresh) { viewModel.load(force = refresh) }
    │
    ▼
WordSetListViewModel.load(force)
    ├── !force && wordSets.isNotEmpty() → dùng cache trong ViewModel
    └── gọi repository.loadWordSets() → backend filter theo JWT

Mỗi WordSet Card hiển thị:
    ├── Tên bộ từ
    ├── learnedWords / totalWords
    └── Nút: Học (Flashcard), Quiz, Xóa
```

### 12.2 Danh sách từ — WordListScreen

```
Hiển thị mỗi từ:
    ├── word.word (tiếng Anh, in đậm)
    ├── Badge "✓ Đã học" nếu word.isLearned (intervalDays > 1)
    ├── word.meaning + word.example
    └── Nút "Nghe" (TTS) và "Xóa"
```

### 12.3 Word Quiz — Setup & Screen

**WordQuizSetupScreen:** Cho phép chọn nhiều bộ từ + số câu.

**WordQuizViewModel.load():**

```
load(wordSetIds = ["1","2"], requestedCount = 15)
    │
    ▼
Fetch words từ từng wordSetId song song
    │
    ▼
buildQuestions(allWords, count)
    ├── allWords.shuffled().take(count)
    └── Mỗi từ: ngẫu nhiên EN_TO_VI hoặc VI_TO_EN
        └── buildOptions(): 1 đúng + 3 sai ngẫu nhiên
```

**Giao diện:**
- `HorizontalPager`: vuốt qua lại câu hỏi
- `LazyRow`: điều hướng câu, màu xanh = đã chọn
- Nộp bài khi đã chọn đủ tất cả câu
- Kết quả: option đúng → xanh, sai → đỏ

---

## 13. Tính năng: Học — Flashcard & Dictation

### 13.1 FlashcardViewModel

**State chính:**

```kotlin
data class FlashcardUiState(
    val isLoading: Boolean,
    val wordSets: List<WordSet>,   // danh sách để chọn
    val wordSet: WordSet?,         // bộ từ đang học
    val words: List<Word>,
    val currentIndex: Int,
    val errorMessage: String?
)
```

**Vòng đời:**

```
1. load()             → fetch wordSets
2. selectWordSet()    → fetch words, reset index = 0
3. nextWord()         → currentIndex = (current + 1) % words.size (vòng lặp)
4. submitReview(quality = 4)
        ├── POST words/{id}/review {"quality": 4}
        └── Optimistic update: intervalDays = 2 → isLearned = true ngay lập tức
```

**Animation lật thẻ:**

```kotlin
var isFlipped by remember { mutableStateOf(false) }
val rotation by animateFloatAsState(targetValue = if (isFlipped) 180f else 0f)

// rotation <= 90f → Mặt trước: word.word
// rotation > 90f  → Mặt sau: meaning + example (graphicsLayer { rotationY = 180f })
```

### 13.2 DictationViewModel

State bổ sung:

```kotlin
val answer: String             // text user đang gõ
val feedback: DictationFeedback?   // CORRECT | WRONG
val sessionScore: Int          // số câu đúng phiên này
val sessionTotal: Int          // tổng câu đã thử
```

**checkAnswer():**

```
isCorrect = answer.trim().lowercase() == word.word.trim().lowercase()
    ├── Đúng: quality = 5, submitReview(5), feedback = CORRECT, sessionScore++
    └── Sai:  quality = 0, submitReview(0), feedback = WRONG
sessionTotal++
```

> **Lưu ý:** `submitReview()` chạy async (coroutine), còn `feedback` cập nhật synchronous ngay lập tức. Nếu API thất bại, feedback đã hiển thị nhưng SM-2 trên server chưa được cập nhật — đây là đánh đổi chấp nhận được để UX mượt mà.

---

## 14. Tính năng: Ngữ pháp (Grammar + Quiz)

### 14.1 GrammarListScreen

```
LaunchedEffect(Unit) { viewModel.fetchGrammarNotes() }

Mỗi GrammarNote Card:
    ├── Tiêu đề + category
    ├── highestScore > 0 → hiển thị "X/Y" điểm cao nhất
    ├── Card màu xanh nhạt nếu đã quiz
    └── Icons: ✏️ Edit, 🗑️ Delete

onClick Card:
    navController.currentBackStackEntry.savedStateHandle["note"] = note
    navController.navigate("grammar_detail")

GrammarListScreen auto-refresh khi tab Grammar được chọn lại (Bug #6 đã fix):
    LaunchedEffect(isFocused) { if (isFocused) viewModel.fetchGrammarNotes() }
```

### 14.2 GrammarDetailScreen → Quiz

```
Hiển thị: title, category, formula, explanation, example, commonMistakes
Button "Bắt đầu Quiz" → navigate("grammar_quiz/$noteId")

GrammarQuizScreen:
    ├── fetchQuizQuestions(noteId) → GET /quiz_questions?noteId=X
    ├── User chọn đáp án A/B/C/D cho từng câu
    └── Nộp bài → submitQuizScore(noteId, correct, total)
                → POST /grammar/{id}/score
```

### 14.3 AddEditGrammarScreen (Create / Edit mode)

```
noteId == null (Create):
    LaunchedEffect { viewModel.clearEditingState() }  → Form rỗng

noteId != null (Edit):
    LaunchedEffect(noteId) { viewModel.loadNoteForEdit(noteId) }
        ├── Fetch note mới nhất từ server
        └── Populate: editingTitle, editingCategory, ..., isEditDataLoaded = true

hasUnsavedChanges detection:
    Mỗi lần user gõ → computeHasChanges()
        ├── originalNote == null → true nếu bất kỳ field nào không rỗng
        └── có originalNote   → so sánh từng field

Back Button:
    hasUnsavedChanges = true → Dialog "Bỏ qua thay đổi?"
    hasUnsavedChanges = false → popBackStack() ngay

onDispose → viewModel.clearEditingState()
```

---

## 15. Tính năng: Tiến độ & Dashboard (Home)

### HomeScreen

**Dữ liệu hiển thị:**

```
streak        → "🔥 X ngày liên tiếp"
accuracyRate  → "X% chính xác"
learnedWords  → "X từ đã học / totalWords tổng"
reviewToday   → "X từ cần ôn hôm nay"
dailyActivity → Bar chart 7 ngày gần nhất
```

**Level estimate (tính trong UI — không có API):**

```kotlin
val estimatedLevel = when {
    learnedWords < 500  -> "A1"
    learnedWords < 1000 -> "A2"
    learnedWords < 2000 -> "B1"
    learnedWords < 4000 -> "B2"
    learnedWords < 8000 -> "C1"
    else                -> "C2"
}
```

**HomeViewModel.load():**
```
GET /progress → ProgressOverview
    └── map trực tiếp vào uiState fields
```

Guard chống duplicate fetch:
```kotlin
if (_uiState.value.isLoading || (_uiState.value.streak > 0 && !force)) return
```

### ProgressScreen

```
ProgressViewModel.load()
    ├── GET /progress      → tổng quan
    └── GET /progress_history → List<ProgressRecord>

Hiển thị: streak, accuracy, learnedWords + bar chart toàn bộ lịch sử
Click record → ProgressDetailScreen(date)
```

---

## 16. Tính năng: AI Word Generator

### Flow Gemini AI

```
AiWordListScreen → User nhập topic + số từ muốn tạo
    │
    ▼
viewModel.generateWordList(topic, count)
    │
    ▼
GeminiService.generateWordList(topic, count)
    ├── Model: gemini-2.5-flash
    ├── Prompt: "Generate {count} English vocabulary words about [{topic}].
    │            Return as JSON array: [{word, meaning, example}]"
    ├── Parse response:
    │       ├── Strip markdown code fences (```json ... ```)
    │       └── Gson.fromJson → List<AiGeneratedWord>
    └── Throw IllegalStateException nếu API key chưa cấu hình
    │
    ▼
AiWordListViewModel cập nhật uiState.generatedWords

User xem/chỉnh sửa danh sách → nhấn "Lưu"
    │
    ▼
viewModel.saveAll(wordSetName, description)
    ├── POST /wordsets → tạo bộ từ mới
    └── forEach word: POST /words {word, meaning, example, wordSetId}
    │
    ▼
navigate("word_set_list?refresh=true")
```

---

## 17. Tính năng: Cài đặt & Hồ sơ (Me)

### MeScreen

```
Hiển thị: Avatar (chữ cái đầu), tên, email (từ authViewModel.uiState.currentUser)

Menu:
├── "Chỉnh sửa hồ sơ"  → navigate("edit_profile")
├── "Thông báo"         → navigate("notification_settings")
├── "Ngôn ngữ"          → navigate("language_settings")
├── "Cài đặt Server"    → navigate("server_settings")
├── "Giới thiệu"        → navigate("about_app")
└── "Đăng xuất"         → authViewModel.logout()
```

### ProfileScreen

```
Form: name, goal, level
    │
    ▼
viewModel.updateProfile(name, goal, level)
    └── PATCH /users/{id} với UpdateProfilePayload (không có email/id — Bug #7 fix)
    │
    ▼
isUpdateSuccess = true → popBackStack()
```

### ServerSettingsScreen (chỉ dành cho dev)

```
Cho phép thay đổi base URL của Retrofit runtime
    └── Lưu vào ServerPreferences (DataStore)
    └── Retrofit instance tạo mới với URL mới khi app restart
```

### LanguageSettingsScreen

```
Đọc ngôn ngữ: LanguagePreferences.languageFlow() (DataStore)
User chọn "Tiếng Việt" hoặc "English"
    └── LanguagePreferences.setLanguage(lang)

MainActivity.onCreate():
    └── AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(lang))
        → Toàn bộ Compose re-render với ngôn ngữ mới
```

---

## 18. Thuật toán SM-2 (Spaced Repetition)

App dùng **SM-2** để quyết định khi nào cần ôn tập lại từ. Backend xử lý toàn bộ tính toán; Android chỉ gửi `quality` (0–5).

### Ý nghĩa quality

| Quality | Mô tả               | Khi nào dùng                          |
| ------- | ------------------- | ------------------------------------- |
| 0       | Không nhớ gì        | Dictation: trả lời sai                |
| 1       | Biết mờ, nhầm nhiều | —                                     |
| 2       | Biết nhưng sai      | —                                     |
| 3       | Nhớ nhưng khó       | —                                     |
| 4       | Nhớ tốt             | Flashcard: next sau khi đã lật thẻ    |
| 5       | Nhớ hoàn toàn       | Dictation: trả lời đúng               |

### Cách tính (backend — xem chi tiết Mục 5)

```
quality < 3  → interval reset về 1 (ôn lại ngày mai)
quality >= 3 →
    interval = 1        nếu interval cũ = 0
    interval = 6        nếu interval cũ = 1
    interval = round(interval * ease_factor)  trường hợp còn lại

ease_factor += 0.1 - (5 - quality) × (0.08 + (5 - quality) × 0.02)
ease_factor = max(1.3, ease_factor)

next_review_date = today + interval ngày
```

### Optimistic Update trên Android

Thay vì reload toàn bộ word list sau review, app dùng **optimistic update**:

```kotlin
// FlashcardViewModel.submitReview() và DictationViewModel.checkAnswer()
if (quality >= 3) {
    // Giả định thành công → set intervalDays = 2 ngay → isLearned = true → badge hiện ngay
    val updated = state.words.toMutableList().also { list ->
        val idx = list.indexOfFirst { it.id == word.id }
        if (idx >= 0) list[idx] = list[idx].copy(intervalDays = 2)
    }
    _uiState.update { it.copy(words = updated) }
}
// API call chạy song song ở background — nếu thất bại chỉ log, không hoàn tác UI
```

### Offline Queue

```kotlin
// VocabularyRepository.submitReview()
try {
    apiService.reviewWord(wordId, ReviewPayload(quality))
} catch (e: IOException) {
    // Mất mạng → lưu vào Room để sync sau
    pendingReviewDao.insert(PendingReviewEntity(wordId, quality, timestamp))
}

// Khi có mạng lại, gọi syncPendingReviews()
syncPendingReviews()
    └── Lấy tất cả pending → gửi lần lượt → xóa khỏi DB khi thành công
```

---

## 19. Lỗi tiềm ẩn cần lưu ý

### Bug #1 — Back navigation khi đổi Word Set
**Vấn đề:** `word_list/1 → flashcard`, đổi WS, ấn Back → về `word_list/1` (cũ).
**Giải pháp:** Callback `onWordSetChanged` popUpTo rồi navigate đến `word_list/2` — xem Mục 10.
**Trạng thái:** Đã fix.

### Bug #2 — HomeViewModel tải dữ liệu 2 lần
**Vấn đề:** Khi navigate về HomeScreen, `load()` được gọi lại dù đã có data.
**Giải pháp:** Guard check `isLoading || (streak > 0 && !force)` ở đầu hàm load().
**Trạng thái:** Đã fix.

### Bug #3 — `intervalDays ?: 1` cho từ mới tạo
**Vấn đề:** Từ mới backend trả `intervalDays = null`, nếu so sánh `null > 1` → crash.
**Giải pháp:** `val isLearned: Boolean get() = (intervalDays ?: 1) > 1`
**Trạng thái:** Đã fix.

### Bug #4 — AddEditGrammarScreen load data sai khi Edit
**Vấn đề:** Mở Edit → form hiển thị data cũ hoặc rỗng do state không reset đúng lúc.
**Giải pháp:** `isEditDataLoaded` flag + `LaunchedEffect(isEditDataLoaded)` để đợi data sẵn sàng trước khi fill form.
**Trạng thái:** Đã fix.

### Bug #5 — Từ "Đã học" không reset khi ôn tập sai
**Vấn đề:** Sau khi submit quality=0 (sai), frontend vẫn hiển thị badge "✓ Đã học" vì chỉ optimistic update khi quality >= 3.
**Trạng thái:** Đây là behavior có chủ ý — không hoàn tác optimistic update để tránh UI giật.

### Bug #6 — GrammarListScreen không refresh khi tab được chọn lại
**Vấn đề:** Thêm/xóa grammar note rồi switch tab khác và back → danh sách vẫn cũ.
**Giải pháp:** `LaunchedEffect(isFocused)` trong `MainBottomNav.kt` — gọi `fetchGrammarNotes()` mỗi khi tab Grammar được focus.
**Trạng thái:** Đã fix.

### Bug #7 — UpdateProfilePayload ghi đè email thành rỗng
**Vấn đề:** Khi cập nhật profile, nếu gửi object User đầy đủ với `email = ""` thì backend ghi đè mất email.
**Giải pháp:** Tạo `UpdateProfilePayload(name, goal, level)` riêng — không chứa email/id.
**Trạng thái:** Đã fix.

### Vấn đề tiềm ẩn chưa fix

**TokenManager trong AuthInterceptor:**
Mỗi request intercepted đều tạo `TokenManager` instance mới. Không phải lỗi nghiêm trọng (EncryptedSharedPreferences được OS cache), nhưng không tối ưu. Giải pháp lý tưởng: inject `TokenManager` singleton vào `AuthInterceptor` qua Hilt.

**Room `fallbackToDestructiveMigration`:**
Khi nâng version schema mà thiếu migration, toàn bộ local cache bị xóa. Người dùng sẽ mất queue pending reviews. Cần thêm migration thực sự khi lên production.

**Gemini API key hardcoded:**
`GeminiService` throw `IllegalStateException` nếu không có API key. Nên validate sớm hơn (khi khởi động app) thay vì chờ user nhấn "Generate".
