package com.hibob.academy.feedbacks_system.service

import com.google.cloud.language.v1.Document
import com.google.cloud.language.v1.LanguageServiceClient
import com.hibob.academy.feedbacks_system.*
import com.hibob.academy.feedbacks_system.dao.FeedbackDao
import jakarta.ws.rs.ForbiddenException
import org.springframework.stereotype.Component

@Component
class FeedbackService(private val feedbackDao: FeedbackDao) {
    private val inappropriateWords = listOf(
        "hate", "stupid", "idiot",
        "moron", "crap", "suck", "dumb", "loser",
        "vile", "disgusting", "scum", "hate speech", "abusive",
        "fool", "jerk", "douche", "shut up", "twit",
        "bastard", "faggot", "cunt", "prick", "wanker",
        "slut", "whore", "retard", "piss", "bullshit",
        "nigga", "trash", "scumbag", "foolish", "pendejo",
        "klutz", "freak", "loser", "chump", "simpleton",
        "asshole", "snot", "dumbass", "dickhead", "knob",
        "putz", "waste", "moronic", "lame", "cringe", "fuck"
    )

    fun insertFeedback(
        companyId: Long,
        content: String,
        isAnonymous: Boolean,
        feedbackProviderId: Long?,
        department: Department,
        sentimentScore: Long
    ) {
        validateLength(content)
        validateContent(content)
        feedbackDao.insertFeedback(companyId, content, isAnonymous, feedbackProviderId, department, sentimentScore = sentimentScore)
    }

    private fun validateLength(content: String) {
        if (content.length < 10) {
            throw IllegalArgumentException("feedback is too short.")
        }
    }

    private fun validateContent(content: String) {
        if (inappropriateWords.any { content.lowercase().contains(it.lowercase()) }) {
            throw IllegalArgumentException("feedback contains inappropriate language.")
        }
    }

    fun getAllCompanyFeedbacks(companyId: Long): List<FeedbackData> {
        return feedbackDao.getAllCompanyFeedbacks(companyId)
    }

    fun filterFeedbacks(companyId: Long, userFeedbackFilter: UserFeedbackFilter): List<FeedbackData> {
        val filter = FeedbackFilter(
            companyId,
            userFeedbackFilter.isAnonymous,
            userFeedbackFilter.status,
            userFeedbackFilter.feedbackProviderId,
            userFeedbackFilter.department,
            userFeedbackFilter.timeOfSubmitting
        )
        return feedbackDao.filterFeedbacks(filter)
    }

    fun switchFeedbackStatus(feedbackId: Long) {
        val updatedRows = feedbackDao.switchFeedbackStatus(feedbackId)
        if (updatedRows == 0)
            throw IllegalArgumentException("Feedback does not exist.")
    }

    fun getFeedbackStatus(feedbackId: Long, feedbackProviderId: Long): Boolean {
        val statusData = feedbackDao.getFeedbackStatus(feedbackId)
            ?: throw IllegalArgumentException("Feedback does not exist.")

        return if (statusData.feedbackProviderId != feedbackProviderId) {
            throw ForbiddenException("Access denied.")
        } else {
            statusData.status
        }
    }

    fun analyzeSentiment(text: String): Int {
        LanguageServiceClient.create().use { language ->
            // Create a document containing the text to analyze
            val document = Document.newBuilder()
                .setContent(text)
                .setType(Document.Type.PLAIN_TEXT)
                .build()

            // Analyze sentiment
            val sentiment = language.analyzeSentiment(document).documentSentiment

            // Return sentiment score as integer (positive: 1, neutral: 0, negative: -1)
            return when {
                sentiment.score > 0.25 -> 1
                sentiment.score < -0.25 -> -1
                else -> 0
            }
        }
    }
}