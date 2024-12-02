package com.hibob.academy.feedbacks_system

import com.hibob.academy.feedbacks_system.dao.ResponseDao
import org.junit.jupiter.api.Assertions.*
import com.hibob.academy.utils.BobDbTest
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import kotlin.random.Random

@BobDbTest
class ResponseDaoTest @Autowired constructor(private val sql: DSLContext) {
    private val responseDao = ResponseDao(sql)
    private val companyId = Random.nextLong()
    private val feedbackId = Random.nextLong()
    private var insertedResponseIds = listOf<Long>()

    @AfterEach
    fun cleanup() {
        insertedResponseIds.forEach { responseId ->
            responseDao.deleteResponse(responseId)
        }
        insertedResponseIds = listOf()
    }

    @Test
    fun `insert response successfully`() {
        val responserId = Random.nextLong()
        val responseId = responseDao.insertResponse(
            companyId = companyId,
            feedbackId = feedbackId,
            content = "This is a response to feedback!",
            responserId = responserId,
            timeOfResponding = LocalDateTime.now()
        )
        insertedResponseIds = insertedResponseIds + responseId

        val insertedResponse = responseDao.getResponseById(responseId)

        assertNotNull(insertedResponse)
        assertEquals(companyId, insertedResponse?.companyId)
        assertEquals(feedbackId, insertedResponse?.feedbackId)
        assertEquals("This is a response to feedback!", insertedResponse?.content)
        assertEquals(responserId, insertedResponse?.responserId)
        assertTrue(insertedResponse?.timeOfResponding?.isBefore(LocalDateTime.now()) == true)
        assertEquals(responseId, insertedResponse?.id)
    }

    @Test
    fun `get all responses by companyId successfully`() {
        val responserId = Random.nextLong()
        val responseId1 = responseDao.insertResponse(
            companyId = companyId,
            feedbackId = feedbackId,
            content = "First response to feedback!",
            responserId = responserId,
            timeOfResponding = LocalDateTime.now()
        )
        val responseId2 = responseDao.insertResponse(
            companyId = companyId,
            feedbackId = feedbackId,
            content = "Second response to feedback!",
            responserId = responserId,
            timeOfResponding = LocalDateTime.now()
        )
        insertedResponseIds = insertedResponseIds + listOf(responseId1, responseId2)

        val responses = responseDao.getAllCompanyResponses(companyId)

        assertEquals(2, responses.size)
        assertTrue(responses.all { it.companyId == companyId })

        val responseContents = responses.map { it.content }
        assertTrue(responseContents.contains("First response to feedback!"))
        assertTrue(responseContents.contains("Second response to feedback!"))
    }

    @Test
    fun `get all responses by feedbackId successfully`() {
        val responserId = Random.nextLong()
        val responseId1 = responseDao.insertResponse(
            companyId = companyId,
            feedbackId = feedbackId,
            content = "Response one to feedback!",
            responserId = responserId,
            timeOfResponding = LocalDateTime.now()
        )
        val responseId2 = responseDao.insertResponse(
            companyId = companyId,
            feedbackId = feedbackId,
            content = "Response two to feedback!",
            responserId = responserId,
            timeOfResponding = LocalDateTime.now()
        )
        insertedResponseIds = insertedResponseIds + listOf(responseId1, responseId2)

        val responses = responseDao.getResponseByFeedbackId(feedbackId)

        assertEquals(2, responses.size)
        assertTrue(responses.all { it.feedbackId == feedbackId })

        val responseContents = responses.map { it.content }
        assertTrue(responseContents.contains("Response one to feedback!"))
        assertTrue(responseContents.contains("Response two to feedback!"))
    }
}
