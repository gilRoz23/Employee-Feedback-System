package com.hibob.academy.feedbacks_system.service

import com.hibob.academy.feedbacks_system.*
import com.hibob.academy.feedbacks_system.dao.FeedbackDao
import jakarta.ws.rs.ForbiddenException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import kotlin.random.Random

class FeedbackServiceTest {
    private var feedbackDao: FeedbackDao = mock(FeedbackDao::class.java)
    private var feedbackService: FeedbackService = FeedbackService(feedbackDao)

    @Test
    fun `should throw exception for feedback that is too short`() {
        val shortFeedback = "Too short feedback"
        val companyId = 1L
        val isAnonymous = false
        val feedbackProviderId: Long? = null
        val department = Department.HR

        assertThrows<IllegalArgumentException> {
            feedbackService.insertFeedback(companyId, shortFeedback, isAnonymous, feedbackProviderId, department)
        }
    }

    @Test
    fun `should throw exception for feedback containing inappropriate language`() {
        val inappropriateFeedback = "I truly hate this product because it fails to deliver on its promises and has many disappointing flaws."
        val companyId = 1L
        val isAnonymous = false
        val feedbackProviderId: Long? = null
        val department = Department.HR

        assertThrows<IllegalArgumentException> {
            feedbackService.insertFeedback(companyId, inappropriateFeedback, isAnonymous, feedbackProviderId, department)
        }
    }

    @Test
    fun `should insert feedback successfully`() {
        val validFeedback = "This is an exceptional product that I truly enjoy using every day. It meets all my needs and exceeds my expectations!"
        val companyId = 1L
        val isAnonymous = false
        val feedbackProviderId: Long? = null
        val department = Department.HR
        val timeOfSubmitting = LocalDateTime.now()

        doReturn(3L).whenever(feedbackDao).insertFeedback(companyId, validFeedback, isAnonymous, feedbackProviderId, department, timeOfSubmitting)

        assertDoesNotThrow {
            feedbackService.insertFeedback(companyId, validFeedback, isAnonymous, feedbackProviderId, department)
        }
    }

    @Test
    fun `get all company feedbacks successfully`() {
        val companyId = 1L
        val feedbackId1 = Random.nextLong()
        val feedbackId2 = Random.nextLong()
        val feedbackList = listOf(
            FeedbackData(
                id = feedbackId1,
                companyId = companyId,
                content = "I'm Mr. Meeseeks!",
                isAnonymous = false,
                status = true,
                feedbackProviderId = Random.nextLong(),
                department = Department.IT,
                timeOfSubmitting = LocalDateTime.now()
            ),
            FeedbackData(
                id = feedbackId2,
                companyId = companyId,
                content = "OOOWeeee!",
                isAnonymous = true,
                status = false,
                feedbackProviderId = null,
                department = Department.HR,
                timeOfSubmitting = LocalDateTime.now()
            )
        )

        doReturn(feedbackList).whenever(feedbackDao).getAllCompanyFeedbacks(companyId)

        val feedbacks = feedbackService.getAllCompanyFeedbacks(companyId)

        assertEquals(2, feedbacks.size)
        assertTrue(feedbacks.all { it.companyId == companyId })
        assertTrue(feedbacks.any { it.content == "I'm Mr. Meeseeks!" })
        assertTrue(feedbacks.any { it.content == "OOOWeeee!" })

        verify(feedbackDao).getAllCompanyFeedbacks(companyId)
    }

    @Test
    fun `get all company feedbacks returns empty list when no feedback exists`() {
        val companyId = 1L
        doReturn(emptyList<FeedbackData>()).whenever(feedbackDao).getAllCompanyFeedbacks(companyId)

        val feedbacks = feedbackService.getAllCompanyFeedbacks(companyId)

        assertEquals(0, feedbacks.size)

        verify(feedbackDao).getAllCompanyFeedbacks(companyId)
    }

    @Test
    fun `should return filtered feedbacks based on isAnonymous`() {
        val companyId = 1L
        val isAnonymous = true
        val feedbackProviderId = Random.nextLong()
        val timeOfSubmitting = LocalDateTime.now()

        val feedbackData1 = FeedbackData(
            id = Random.nextLong(),
            companyId = companyId,
            content = "Anonymous feedback",
            isAnonymous = true,
            status = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT,
            timeOfSubmitting = timeOfSubmitting
        )
        val feedbackData2 = FeedbackData(
            id = Random.nextLong(),
            companyId = companyId,
            content = "Non-anonymous feedback",
            isAnonymous = false,
            status = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR,
            timeOfSubmitting = timeOfSubmitting
        )

        val userFeedbackFilter = UserFeedbackFilter(isAnonymous = true)
        val filter = FeedbackFilter(companyId, userFeedbackFilter.isAnonymous, null, null, null, null)
        doReturn(listOf(feedbackData1)).whenever(feedbackDao).filterFeedbacks(filter)

        val filteredFeedbacks = feedbackService.filterFeedbacks(companyId, userFeedbackFilter)

        assertEquals(1, filteredFeedbacks.size)
        assertTrue(filteredFeedbacks.all { it.isAnonymous })

        verify(feedbackDao).filterFeedbacks(filter)
    }

    @Test
    fun `should filter feedbacks by status`() {
        val companyId = 1L
        val feedbackProviderId: Long? = null
        val timeOfSubmitting = LocalDateTime.now()

        val feedbackData1 = FeedbackData(
            id = 103L,
            companyId = companyId,
            content = "First feedback",
            isAnonymous = true,
            status = true,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT,
            timeOfSubmitting = timeOfSubmitting
        )
        val feedbackData2 = FeedbackData(
            id = 104L,
            companyId = companyId,
            content = "Second feedback",
            isAnonymous = true,
            status = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT,
            timeOfSubmitting = timeOfSubmitting
        )

        val userFeedbackFilter = UserFeedbackFilter(status = true)
        val filter = FeedbackFilter(companyId, null, userFeedbackFilter.status, null, null, null)
        doReturn(listOf(feedbackData1)).whenever(feedbackDao).filterFeedbacks(filter)

        val filteredFeedbacks = feedbackService.filterFeedbacks(companyId, userFeedbackFilter)

        assertEquals(1, filteredFeedbacks.size)
        assertTrue(filteredFeedbacks.all { it.status })

        verify(feedbackDao).filterFeedbacks(filter)
    }

    @Test
    fun `should filter feedbacks by feedback provider id`() {
        val companyId = 1L
        val feedbackProviderId = 200L
        val timeOfSubmitting = LocalDateTime.now()

        val feedbackData1 = FeedbackData(
            id = 105L,
            companyId = companyId,
            content = "Feedback from provider",
            isAnonymous = true,
            status = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT,
            timeOfSubmitting = timeOfSubmitting
        )
        val feedbackData2 = FeedbackData(
            id = 106L,
            companyId = companyId,
            content = "Anonymous feedback",
            isAnonymous = true,
            status = false,
            feedbackProviderId = feedbackProviderId + 1,
            department = Department.IT,
            timeOfSubmitting = timeOfSubmitting
        )

        val userFeedbackFilter = UserFeedbackFilter(feedbackProviderId = feedbackProviderId)
        val filter = FeedbackFilter(companyId, null, null, userFeedbackFilter.feedbackProviderId, null, null)
        doReturn(listOf(feedbackData1)).whenever(feedbackDao).filterFeedbacks(filter)

        val filteredFeedbacks = feedbackService.filterFeedbacks(companyId, userFeedbackFilter)

        assertEquals(1, filteredFeedbacks.size)
        assertTrue(filteredFeedbacks.all { it.feedbackProviderId == feedbackProviderId })

        verify(feedbackDao).filterFeedbacks(filter)
    }

    @Test
    fun `should filter feedbacks by department`() {
        val companyId = 1L
        val department = Department.HR
        val timeOfSubmitting = LocalDateTime.now()

        val feedbackData1 = FeedbackData(
            id = 107L,
            companyId = companyId,
            content = "HR feedback",
            isAnonymous = false,
            status = true,
            feedbackProviderId = null,
            department = department,
            timeOfSubmitting = timeOfSubmitting
        )
        val feedbackData2 = FeedbackData(
            id = 108L,
            companyId = companyId,
            content = "IT feedback",
            isAnonymous = false,
            status = true,
            feedbackProviderId = null,
            department = Department.IT,
            timeOfSubmitting = timeOfSubmitting
        )

        val userFeedbackFilter = UserFeedbackFilter(department = department)
        val filter = FeedbackFilter(companyId, null, null, null, userFeedbackFilter.department, null)
        doReturn(listOf(feedbackData1)).whenever(feedbackDao).filterFeedbacks(filter)

        val filteredFeedbacks = feedbackService.filterFeedbacks(companyId, userFeedbackFilter)

        assertEquals(1, filteredFeedbacks.size)
        assertTrue(filteredFeedbacks.all { it.department == department })

        verify(feedbackDao).filterFeedbacks(filter)
    }

    @Test
    fun `should filter feedbacks by time of submitting`() {
        val companyId = 1L
        val timeOfSubmitting = LocalDateTime.now()
        val pastTime = timeOfSubmitting.minusDays(1)

        val feedbackData1 = FeedbackData(
            id = 109L,
            companyId = companyId,
            content = "Feedback from yesterday",
            isAnonymous = false,
            status = true,
            feedbackProviderId = null,
            department = Department.HR,
            timeOfSubmitting = pastTime
        )
        val feedbackData2 = FeedbackData(
            id = 110L,
            companyId = companyId,
            content = "Feedback from today",
            isAnonymous = false,
            status = true,
            feedbackProviderId = null,
            department = Department.HR,
            timeOfSubmitting = timeOfSubmitting
        )

        val userFeedbackFilter = UserFeedbackFilter(timeOfSubmitting = timeOfSubmitting)
        val filter = FeedbackFilter(companyId, null, null, null, null, userFeedbackFilter.timeOfSubmitting)
        doReturn(listOf(feedbackData2)).whenever(feedbackDao).filterFeedbacks(filter)

        val filteredFeedbacks = feedbackService.filterFeedbacks(companyId, userFeedbackFilter)

        assertEquals(1, filteredFeedbacks.size)
        assertTrue(filteredFeedbacks.all { it.timeOfSubmitting.isEqual(timeOfSubmitting) })

        verify(feedbackDao).filterFeedbacks(filter)
    }

    @Test
    fun `should throw exception when switching status for non-existing feedback`() {
        val feedbackId = 1L
        doReturn(0).whenever(feedbackDao).switchFeedbackStatus(feedbackId)

        assertThrows<IllegalArgumentException> {
            feedbackService.switchFeedbackStatus(feedbackId)
        }

        verify(feedbackDao).switchFeedbackStatus(feedbackId)
    }

    @Test
    fun `should successfully switch feedback status`() {
        val feedbackId = 1L
        doReturn(1).whenever(feedbackDao).switchFeedbackStatus(feedbackId)

        assertDoesNotThrow {
            feedbackService.switchFeedbackStatus(feedbackId)
        }

        verify(feedbackDao).switchFeedbackStatus(feedbackId)
    }

    @Test
    fun `should throw exception when feedback does not exist`() {
        val feedbackId = 1L
        val feedbackProviderId = 200L

        doReturn(null).whenever(feedbackDao).getFeedbackStatus(feedbackId)

        assertThrows<IllegalArgumentException> {
            feedbackService.getFeedbackStatus(feedbackId, feedbackProviderId)
        }

        verify(feedbackDao).getFeedbackStatus(feedbackId)
    }

    @Test
    fun `should throw exception when access is denied for feedback provider`() {
        val feedbackId = 1L
        val feedbackProviderId = 200L
        val otherProviderId = 201L

        val statusData = mock(StatusData::class.java)
        doReturn(otherProviderId).whenever(statusData).feedbackProviderId
        doReturn(statusData).whenever(feedbackDao).getFeedbackStatus(feedbackId)

        assertThrows<ForbiddenException> {
            feedbackService.getFeedbackStatus(feedbackId, feedbackProviderId)
        }

        verify(feedbackDao).getFeedbackStatus(feedbackId)
    }

    @Test
    fun `should return feedback status when access is granted`() {
        val feedbackId = 1L
        val feedbackProviderId = 200L
        val statusData = mock(StatusData::class.java)

        doReturn(feedbackProviderId).whenever(statusData).feedbackProviderId
        doReturn(true).whenever(statusData).status
        doReturn(statusData).whenever(feedbackDao).getFeedbackStatus(feedbackId)

        val status = feedbackService.getFeedbackStatus(feedbackId, feedbackProviderId)

        assertTrue(status)
        verify(feedbackDao).getFeedbackStatus(feedbackId)
    }
}