package com.hibob.academy.feedbacks_system.service

import com.hibob.academy.feedbacks_system.*
import com.hibob.academy.feedbacks_system.dao.FeedbackDao
import com.hibob.academy.feedbacks_system.dao.ResponseDao
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.mockito.kotlin.doReturn
import java.time.LocalDateTime
import kotlin.random.Random
import org.mockito.kotlin.whenever

class ResponseServiceTest {

    private val responseDao: ResponseDao = mock(ResponseDao::class.java)
    private val feedbackDao: FeedbackDao = mock(FeedbackDao::class.java)
    private val responseService: ResponseService = ResponseService(responseDao, feedbackDao)

    @Test
    fun `should insert response successfully`() {
        val companyId = 1L
        val feedbackId = Random.nextLong()
        val content = "This is a response to feedback."
        val responserId: Long? = null

        val responseRequest = ResponseRequest(content, feedbackId)

        val feedback = mock(FeedbackData::class.java)
        whenever(feedbackDao.getFeedbackById(feedbackId)).thenReturn(feedback)
        whenever(feedback.isAnonymous).thenReturn(false)

        doReturn(3L).whenever(responseDao).insertResponse(companyId, feedbackId, content, responserId)

        assertDoesNotThrow {
            responseService.insertResponse(companyId, responserId, responseRequest)
        }
    }

    @Test
    fun `insert response denied for anonymous feedback`() {
        val feedbackId = Random.nextLong()

        // Mock anonymous feedback
        val feedback = mock(FeedbackData::class.java)
        whenever(feedbackDao.getFeedbackById(feedbackId)).thenReturn(feedback)
        whenever(feedback.isAnonymous).thenReturn(true)

        val responseRequest = ResponseRequest("This should not be allowed.", feedbackId)

        assertThrows<IllegalArgumentException> {
            responseService.insertResponse(1L, null, responseRequest)
        }
    }

    @Test
    fun `insert response denied for non-existent feedback`() {
        val feedbackId = Random.nextLong()

        // Mock the retrieval of non-existent feedback
        whenever(feedbackDao.getFeedbackById(feedbackId)).thenReturn(null)

        val responseRequest = ResponseRequest("This feedback does not exist.", feedbackId)

        assertThrows<IllegalArgumentException> {
            responseService.insertResponse(1L, null, responseRequest)
        }
    }

    @Test
    fun `get all company responses successfully`() {
        val companyId = 1L
        val responseId1 = Random.nextLong()
        val responseId2 = Random.nextLong()
        val response1 = ResponseData(
            id = responseId1,
            companyId = companyId,
            feedbackId = 1L,
            content = "First response",
            responserId = null,
            timeOfResponding = LocalDateTime.now()
        )
        val response2 = ResponseData(
            id = responseId2,
            companyId = companyId+1,
            feedbackId = 1L,
            content = "Second response",
            responserId = null,
            timeOfResponding = LocalDateTime.now()
        )
        val responseList = listOf(response1)

        doReturn(responseList).whenever(responseDao).getAllCompanyResponses(companyId)

        val responses = responseService.getAllCompanyResponses(companyId)

        assertEquals(1, responses.size)
        assertTrue(responses.any { it.content == "First response" })

        verify(responseDao).getAllCompanyResponses(companyId)
    }

    @Test
    fun `get all company responses returns empty list when no responses exist`() {
        val companyId = 1L
        doReturn(emptyList<ResponseData>()).whenever(responseDao).getAllCompanyResponses(companyId)

        val responses = responseService.getAllCompanyResponses(companyId)

        assertEquals(0, responses.size)

        verify(responseDao).getAllCompanyResponses(companyId)
    }

    @Test
    fun `get responses by feedback ID successfully`() {
        val feedbackId = Random.nextLong()
        val responseId1 = Random.nextLong()
        val responseId2 = Random.nextLong()
        val response1 = ResponseData(
            id = responseId1,
            companyId = 1L,
            feedbackId = feedbackId,
            content = "Response to feedback",
            responserId = null,
            timeOfResponding = LocalDateTime.now()
        )
        val response2 = ResponseData(
            id = responseId2,
            companyId = 1L,
            feedbackId = feedbackId+1,
            content = "Response to another feedback",
            responserId = null,
            timeOfResponding = LocalDateTime.now()
        )
        val responseList = listOf(response1)

        doReturn(responseList).whenever(responseDao).getResponseByFeedbackId(feedbackId)

        val responses = responseService.getResponseByFeedbackId(feedbackId)

        assertEquals(1, responses.size)
        assertTrue(responses.any { it.content == "Response to feedback" })

        verify(responseDao).getResponseByFeedbackId(feedbackId)
    }

    @Test
    fun `get responses by feedback ID returns empty list when no responses exist`() {
        val feedbackId = Random.nextLong()
        doReturn(emptyList<ResponseData>()).whenever(responseDao).getResponseByFeedbackId(feedbackId)

        val responses = responseService.getResponseByFeedbackId(feedbackId)

        assertEquals(0, responses.size)

        verify(responseDao).getResponseByFeedbackId(feedbackId)
    }
}
