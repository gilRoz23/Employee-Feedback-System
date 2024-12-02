package com.hibob.academy.feedbacks_system

import com.hibob.academy.feedbacks_system.dao.FeedbackDao
import com.hibob.academy.utils.BobDbTest
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.random.Random

@BobDbTest
class FeedbackDaoTest @Autowired constructor(private val sql: DSLContext) {
    private val feedbackDao = FeedbackDao(sql)
    private val companyId = Random.nextLong()
    private var insertedFeedbackIds = listOf<Long>()

    @AfterEach
    fun cleanup() {
        insertedFeedbackIds.forEach { feedbackId ->
            feedbackDao.deleteFeedback(feedbackId)
        }
        insertedFeedbackIds = listOf()
    }

    @Test
    fun `insert feedback successfully`() {
        val feedbackProviderId = Random.nextLong()
        val feedbackId = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "Great work on the last project!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR,
            timeOfSubmitting = LocalDateTime.now()
        )
        insertedFeedbackIds = insertedFeedbackIds + feedbackId

        val insertedFeedback = feedbackDao.getFeedbackById(feedbackId)

        assertNotNull(insertedFeedback)
        assertEquals(companyId, insertedFeedback?.companyId)
        assertEquals("Great work on the last project!", insertedFeedback?.content)
        assertEquals(false, insertedFeedback?.isAnonymous)
        assertEquals(feedbackProviderId, insertedFeedback?.feedbackProviderId)
        assertEquals(Department.HR, insertedFeedback?.department)
        assertEquals(false, insertedFeedback?.status) // not reviewed yet
        assertTrue(insertedFeedback?.timeOfSubmitting?.isBefore(LocalDateTime.now()) == true)
        assertEquals(feedbackId, insertedFeedback?.id)
    }

    @Test
    fun `insert anonymous feedback`() {
        val feedbackId = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "Someone doesn't flush the toilet consistently!",
            isAnonymous = true,
            feedbackProviderId = null,
            department = Department.HR,
            timeOfSubmitting = LocalDateTime.now()
        )
        insertedFeedbackIds = insertedFeedbackIds + feedbackId

        val insertedFeedback = feedbackDao.getFeedbackById(feedbackId)

        assertNotNull(insertedFeedback)
        assertEquals(companyId, insertedFeedback?.companyId)
        assertEquals("Someone doesn't flush the toilet consistently!", insertedFeedback?.content)
        assertEquals(true, insertedFeedback?.isAnonymous)
        assertEquals(null, insertedFeedback?.feedbackProviderId)
        assertEquals(Department.HR, insertedFeedback?.department)
        assertEquals(false, insertedFeedback?.status) // not reviewed yet
        assertTrue(insertedFeedback?.timeOfSubmitting?.isBefore(LocalDateTime.now()) == true)
        assertEquals(feedbackId, insertedFeedback?.id)
    }

    @Test
    fun `get all feedback by companyId successfully`() {
        val feedbackId1 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "I'm Mr. Meeseeks!",
            isAnonymous = false,
            feedbackProviderId = Random.nextLong(),
            department = Department.IT,
            timeOfSubmitting = LocalDateTime.now()
        )
        val feedbackId2 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "OOOWeeee!",
            isAnonymous = true,
            feedbackProviderId = Random.nextLong(),
            department = Department.HR,
            timeOfSubmitting = LocalDateTime.now()
        )

        val differentCompanyId = Random.nextLong()
        val feedbackId3 = feedbackDao.insertFeedback(
            companyId = differentCompanyId,
            content = "Rick Sanchez, I'm in",
            isAnonymous = false,
            feedbackProviderId = Random.nextLong(),
            department = Department.SALES,
            timeOfSubmitting = LocalDateTime.now()
        )

        insertedFeedbackIds = insertedFeedbackIds + listOf(feedbackId1, feedbackId2, feedbackId3)

        val feedbacks = feedbackDao.getAllCompanyFeedbacks(companyId)

        // Assertions
        assertEquals(2, feedbacks.size)
        assertTrue(feedbacks.all { it.companyId == companyId })

        val feedbackContents = feedbacks.map { it.content }
        assertTrue(feedbackContents.contains("I'm Mr. Meeseeks!"))
        assertTrue(feedbackContents.contains("OOOWeeee!"))
    }

    @Test
    fun `get all feedback by companyId when no feedback exists`() {
        val feedbacks = feedbackDao.getAllCompanyFeedbacks(Random.nextLong())

        assertTrue(feedbacks.isEmpty())
    }

    @Test
    fun `filter feedbacks by isAnonymous true`() {
        val feedbackProviderId = Random.nextLong()
        val anonymousFeedbackIdCompany1 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "Could this BE any more anonymous?",
            isAnonymous = true,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR,
            timeOfSubmitting = LocalDateTime.now()
        )

        val nonAnonymousFeedbackIdCompany1 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "Look at me, I'm not anonymous!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT,
            timeOfSubmitting = LocalDateTime.now()
        )

        val anotherAnonymousFeedbackId = feedbackDao.insertFeedback(
            companyId = companyId + 1,
            content = "I’ll never tell who I am.",
            isAnonymous = true,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT,
            timeOfSubmitting = LocalDateTime.now()
        )
        insertedFeedbackIds = insertedFeedbackIds + anotherAnonymousFeedbackId

        val filterAnonymous = FeedbackFilter(companyId = companyId, isAnonymous = true)
        val filteredAnonymous = feedbackDao.filterFeedbacks(filterAnonymous)

        assertEquals(1, filteredAnonymous.size)
        assertTrue(filteredAnonymous[0].isAnonymous)
    }

    @Test
    fun `filter feedbacks by isAnonymous false`() {
        val feedbackProviderId = Random.nextLong()
        val anonymousFeedbackIdCompany1 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "This is a secret! Shhh!",
            isAnonymous = true,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT,
            timeOfSubmitting = LocalDateTime.now()
        )

        val nonAnonymousFeedbackIdCompany1 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "I want everyone to know my opinion!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT,
            timeOfSubmitting = LocalDateTime.now()
        )

        val anotherNonAnonymousFeedbackId = feedbackDao.insertFeedback(
            companyId = companyId + 1,
            content = "This feedback is not a secret!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT,
            timeOfSubmitting = LocalDateTime.now()
        )
        insertedFeedbackIds = insertedFeedbackIds + anonymousFeedbackIdCompany1 + nonAnonymousFeedbackIdCompany1 + anotherNonAnonymousFeedbackId

        val filterNonAnonymous = FeedbackFilter(companyId = companyId, isAnonymous = false)
        val filteredNonAnonymous = feedbackDao.filterFeedbacks(filterNonAnonymous)

        assertEquals(1, filteredNonAnonymous.size)
        assertFalse(filteredNonAnonymous[0].isAnonymous)
    }

    @Test
    fun `filter feedbacks by feedbackProviderId`() {
        val feedbackProviderId = Random.nextLong()
        val feedbackId1 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "How you doin'? This feedback is from me.",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR,
            timeOfSubmitting = LocalDateTime.now()
        )

        val anotherFeedbackId = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "This one’s not from me; just a regular feedback.",
            isAnonymous = false,
            feedbackProviderId = Random.nextLong(),
            department = Department.HR,
            timeOfSubmitting = LocalDateTime.now()
        )

        val anotherCompanySameId = feedbackDao.insertFeedback(
            companyId = companyId + 1,
            content = "Another great feedback from yours truly!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT,
            timeOfSubmitting = LocalDateTime.now()
        )
        insertedFeedbackIds = insertedFeedbackIds + feedbackId1 + anotherFeedbackId + anotherCompanySameId

        val filter = FeedbackFilter(companyId = companyId, feedbackProviderId = feedbackProviderId)
        val filteredFeedbacks = feedbackDao.filterFeedbacks(filter)

        assertEquals(1, filteredFeedbacks.size)
        assertEquals(feedbackProviderId, filteredFeedbacks[0].feedbackProviderId)
    }


    @Test
    fun `filter feedbacks by department`() {
        val feedbackProviderId = Random.nextLong()

        val feedbackIdHR = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "HR is the best department. Period.",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR,
            timeOfSubmitting = LocalDateTime.now() // time of submitting added
        )

        val anotherFeedbackId = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "IT feedback is also important!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT,
            timeOfSubmitting = LocalDateTime.now() // time of submitting added
        )

        val yetAnotherFeedbackId = feedbackDao.insertFeedback(
            companyId = companyId + 1,
            content = "More HR feedback, because why not?",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR,
            timeOfSubmitting = LocalDateTime.now() // time of submitting added
        )

        insertedFeedbackIds = insertedFeedbackIds + feedbackIdHR + anotherFeedbackId + yetAnotherFeedbackId

        val filterHR = FeedbackFilter(companyId = companyId, department = Department.HR)
        val filteredHRFeedbacks = feedbackDao.filterFeedbacks(filterHR)

        assertEquals(1, filteredHRFeedbacks.size)
        assertEquals(Department.HR, filteredHRFeedbacks[0].department)
    }


    @Test
    fun `filter feedbacks by date`() {
        val feedbackProviderId = Random.nextLong()

        val feedbackId1 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "How you doin'? This feedback is from me.",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR,
            timeOfSubmitting = LocalDateTime.now()
        )

        val anotherFeedbackId = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "This one’s not from me; just a regular feedback.",
            isAnonymous = false,
            feedbackProviderId = Random.nextLong(),
            department = Department.HR,
            timeOfSubmitting = LocalDateTime.now().minusDays(1)
        )

        val anotherCompanySameId = feedbackDao.insertFeedback(
            companyId = companyId + 1,
            content = "Another great feedback from yours truly!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT,
            timeOfSubmitting = LocalDateTime.now().minusDays(1)
        )

        insertedFeedbackIds = insertedFeedbackIds + feedbackId1 + anotherFeedbackId + anotherCompanySameId

        val filter = FeedbackFilter(companyId = companyId, timeOfSubmitting = LocalDateTime.now())

        val filteredFeedbacks = feedbackDao.filterFeedbacks(filter)
        assertEquals(1, filteredFeedbacks.size)
        assertEquals(LocalDate.now(), filteredFeedbacks[0].timeOfSubmitting.toLocalDate())
    }

    @Test
    fun `switch feedback status successfully`() {
        val feedbackProviderId = Random.nextLong()
        val feedbackId = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "This feedback will be switched!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR,
            timeOfSubmitting = LocalDateTime.now()
        )
        insertedFeedbackIds = insertedFeedbackIds + feedbackId

        val rowsAffected = feedbackDao.switchFeedbackStatus(feedbackId)

        assertEquals(1, rowsAffected)

        val updatedStatusData = feedbackDao.getFeedbackStatus(feedbackId)
        assertNotNull(updatedStatusData)
        assertTrue(updatedStatusData!!.status)
    }

    @Test
    fun `switch feedback status for non-existing feedback ID`() {
        val nonExistingFeedbackId = Random.nextLong()

        val rowsAffected = feedbackDao.switchFeedbackStatus(nonExistingFeedbackId)

        assertEquals(0, rowsAffected)
    }

    @Test
    fun `get feedback status successfully`() {
        val feedbackProviderId = Random.nextLong()
        val feedbackId = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "Checking status!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR,
            timeOfSubmitting = LocalDateTime.now()
        )
        insertedFeedbackIds = insertedFeedbackIds + feedbackId

        val statusData = feedbackDao.getFeedbackStatus(feedbackId)

        assertNotNull(statusData)
        assertFalse(statusData!!.status)
        assertEquals(statusData!!.feedbackProviderId, feedbackProviderId)
    }

    @Test
    fun `get feedback status for non-existing feedback ID`() {
        val nonExistingFeedbackId = Random.nextLong()

        val status = feedbackDao.getFeedbackStatus(nonExistingFeedbackId)

        assertNull(status)
    }
}
