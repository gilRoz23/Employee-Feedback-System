package com.hibob.academy.feedbacks_system.service

import com.hibob.academy.feedbacks_system.*
import com.hibob.academy.feedbacks_system.dao.FeedbackDao
import com.hibob.academy.feedbacks_system.dao.ResponseDao
import org.springframework.stereotype.Component
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

@Component
class ResponseService(private val responseDao: ResponseDao, private val feedbackDao: FeedbackDao) {

    companion object {
        val openAIKey = System.getenv("OPENAI_API_KEY")

    }

    fun insertResponse(companyId: Long, responserId: Long?,  responseRequest: ResponseRequest){
        val feedback = feedbackDao.getFeedbackById(responseRequest.feedbackId)
        if(feedback == null || feedback.isAnonymous){
            throw IllegalArgumentException("Request denied")
        }
        responseDao.insertResponse(companyId, responseRequest.feedbackId, responseRequest.content, responserId)
    }

    fun getAllCompanyResponses(companyId: Long): List<ResponseData> {
        return responseDao.getAllCompanyResponses(companyId)
    }

    fun getResponseByFeedbackId(feedbackId: Long): List<ResponseData> {
        return responseDao.getResponseByFeedbackId(feedbackId)
    }

    fun generateDynamicReply(feedback: String?): String {
        val url = "https://api.openai.com/v1/chat/completions"
        val client = OkHttpClient()

        // Construct the prompt for OpenAI GPT-3.5
        val prompt = """
        You are a helpful HR assistant. Based on the following employee feedback, suggest a professional and empathetic response.
        Feedback: "$feedback"
        Please generate a unique and personalized reply.
    """.trimIndent()

        // Prepare the request body
        val json = JSONObject().apply {
            put("model", "gpt-3.5-turbo")  // You can use gpt-3.5-turbo for lower cost
            put("messages", listOf(
                JSONObject().put("role", "system").put("content", "You are a helpful HR assistant."),
                JSONObject().put("role", "user").put("content", prompt)
            ))
            put("max_tokens", 150)  // Adjust max tokens if necessary
        }

        // Create the request
        val requestBody = RequestBody.create("application/json".toMediaType(), json.toString())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Authorization", "Bearer $openAIKey")
            .build()

        // Make the request and get the response
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Unexpected code $response")
            val responseBody = response.body?.string()
            val responseJson = JSONObject(responseBody ?: "")
            return responseJson.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
        }
    }
}