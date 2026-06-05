package com.example.english_learning_app

import com.example.english_learning_app.data.model.AiGeneratedWord
import com.example.english_learning_app.data.model.AiWordListResult
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.junit.Assert.*
import org.junit.Test

/**
 * Test logic parse response của GeminiService mà không cần gọi API thật.
 * Duplicate hàm parseResponse ở đây để test thuần túy (GeminiService là object, khó mock).
 */
class GeminiServiceParseTest {

    private fun parseResponse(rawText: String): AiWordListResult {
        val cleanText = rawText
            .trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

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
        return AiWordListResult(name = name, description = description, tags = tags, words = words)
    }

    private val validJson = """
        {
          "name": "Business English",
          "description": "Essential business vocabulary",
          "tags": ["business", "professional"],
          "words": [
            {
              "word": "negotiate",
              "meaning": "đàm phán",
              "example": "We need to negotiate the contract.",
              "pronunciation": "/nɪˈɡoʊʃieɪt/",
              "partOfSpeech": "verb"
            }
          ]
        }
    """.trimIndent()

    @Test
    fun `parse JSON thuần túy thành công`() {
        val result = parseResponse(validJson)

        assertEquals("Business English", result.name)
        assertEquals("Essential business vocabulary", result.description)
        assertEquals(2, result.tags.size)
        assertEquals(1, result.words.size)
        assertEquals("negotiate", result.words[0].word)
        assertEquals("đàm phán", result.words[0].meaning)
    }

    @Test
    fun `parse JSON bọc trong markdown code fence`() {
        val withFence = "```json\n$validJson\n```"

        val result = parseResponse(withFence)

        assertEquals("Business English", result.name)
        assertEquals(1, result.words.size)
    }

    @Test
    fun `parse JSON bọc trong fence không có json label`() {
        val withFence = "```\n$validJson\n```"

        val result = parseResponse(withFence)

        assertEquals("Business English", result.name)
    }

    @Test(expected = Exception::class)
    fun `parse JSON không hợp lệ ném exception`() {
        parseResponse("this is not json at all")
    }

    @Test
    fun `parse JSON với tags rỗng trả về list rỗng`() {
        val jsonNoTags = """
            {
              "name": "Test",
              "description": "desc",
              "tags": [],
              "words": []
            }
        """.trimIndent()

        val result = parseResponse(jsonNoTags)

        assertEquals(0, result.tags.size)
        assertEquals(0, result.words.size)
    }
}
