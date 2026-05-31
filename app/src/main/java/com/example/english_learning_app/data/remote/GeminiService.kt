package com.example.english_learning_app.data.remote

import com.example.english_learning_app.BuildConfig
import com.example.english_learning_app.data.model.AiGeneratedWord
import com.example.english_learning_app.data.model.AiWordListResult
import com.google.ai.client.generativeai.GenerativeModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

object GeminiService {

    private val apiKey: String
        get() = BuildConfig.GEMINI_API_KEY

    private val gson = Gson()

    private fun buildPrompt(topic: String, wordCount: Int): String {
        return """
            Hãy tạo một word list học tiếng Anh về chủ đề "$topic".
            Trả về JSON với định dạng sau (KHÔNG thêm markdown code fence, KHÔNG thêm ```json, chỉ trả về JSON thuần túy bắt đầu bằng dấu {):
            {
              "name": "Tên bộ từ ngắn gọn bằng tiếng Anh",
              "description": "Mô tả ngắn 1 câu về bộ từ này",
              "tags": ["tag1", "tag2", "tag3"],
              "words": [
                {
                  "word": "example",
                  "meaning": "ví dụ (danh từ) - nghĩa tiếng Việt",
                  "example": "This is an example sentence.",
                  "pronunciation": "/ɪɡˈzæmpl/",
                  "partOfSpeech": "noun"
                }
              ]
            }
            Tạo đúng $wordCount từ vựng phổ biến và hữu ích nhất về chủ đề "$topic".
            Đảm bảo phần "meaning" luôn có nghĩa tiếng Việt rõ ràng.
        """.trimIndent()
    }

    suspend fun generateWordList(topic: String, wordCount: Int = 10): AiWordListResult {
        if (apiKey.isBlank() || apiKey == "YOUR_GEMINI_API_KEY") {
            throw IllegalStateException("Chưa cấu hình Gemini API key. Vui lòng thêm GEMINI_API_KEY vào local.properties")
        }

        val model = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey
        )

        val prompt = buildPrompt(topic, wordCount)
        val response = model.generateContent(prompt)
        val rawText = response.text
            ?: throw IllegalStateException("Gemini không trả về kết quả")

        return parseResponse(rawText)
    }

    private fun parseResponse(rawText: String): AiWordListResult {
        // Làm sạch: bỏ markdown code fences nếu có
        val cleanText = rawText
            .trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        return try {
            val json: JsonObject = JsonParser.parseString(cleanText).asJsonObject

            val name = json.get("name")?.asString ?: "AI Generated Word Set"
            val description = json.get("description")?.asString ?: ""
            val tagsArray = json.getAsJsonArray("tags")
            val tags = (0 until (tagsArray?.size() ?: 0)).map { tagsArray[it].asString }

            val wordsArray = json.getAsJsonArray("words")
            val words = (0 until (wordsArray?.size() ?: 0)).map { i ->
                val wordObj = wordsArray[i].asJsonObject
                AiGeneratedWord(
                    word = wordObj.get("word")?.asString ?: "",
                    meaning = wordObj.get("meaning")?.asString ?: "",
                    example = wordObj.get("example")?.asString ?: "",
                    pronunciation = wordObj.get("pronunciation")?.asString ?: "",
                    partOfSpeech = wordObj.get("partOfSpeech")?.asString ?: ""
                )
            }

            AiWordListResult(
                name = name,
                description = description,
                tags = tags,
                words = words
            )
        } catch (e: Exception) {
            throw IllegalStateException("Không thể phân tích phản hồi từ AI: ${e.message}")
        }
    }
}
