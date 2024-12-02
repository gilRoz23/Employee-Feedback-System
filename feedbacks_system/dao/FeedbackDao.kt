package com.hibob.academy.feedbacks_system.dao

import com.hibob.academy.feedbacks_system.*
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import org.jooq.impl.DSL
import java.time.LocalDate


@Component
class FeedbackDao(private val sql: DSLContext) {
    private val feedbackTable = FeedbackTable.instance

    private val feedbackMapper = RecordMapper<org.jooq.Record, FeedbackData> { record ->
        FeedbackData(
            id = record[feedbackTable.id],
            companyId = record[feedbackTable.companyId],
            content = record[feedbackTable.content],
            isAnonymous = record[feedbackTable.isAnonymous],
            status = record[feedbackTable.status],
            feedbackProviderId = record[feedbackTable.feedbackProviderId],
            department = Department.valueOf(record[feedbackTable.department].uppercase()),
            timeOfSubmitting = record[feedbackTable.timeOfSubmitting],
            sentimentScore = record[feedbackTable.sentimentScore]
        )
    }

    private val statusMapper = RecordMapper<org.jooq.Record, StatusData> { record ->
        StatusData(
            feedbackProviderId = record[feedbackTable.feedbackProviderId],
            status = record[feedbackTable.status]

        )
    }


    fun insertFeedback(
        companyId: Long,
        content: String,
        isAnonymous: Boolean,
        feedbackProviderId: Long?,
        department: Department,
        timeOfSubmitting: LocalDateTime = LocalDateTime.now(),
        sentimentScore: Long
    ): Long {
        return sql.insertInto(feedbackTable)
            .set(feedbackTable.companyId, companyId)
            .set(feedbackTable.content, content)
            .set(feedbackTable.isAnonymous, isAnonymous)
            .set(feedbackTable.status, false)
            .set(feedbackTable.feedbackProviderId, feedbackProviderId)
            .set(feedbackTable.department, department.name)
            .set(feedbackTable.timeOfSubmitting, timeOfSubmitting)
            .set(feedbackTable.sentimentScore, sentimentScore)
            .returning(feedbackTable.id)
            .fetchOne()!![feedbackTable.id]
    }



    fun deleteFeedback(feedbackId: Long): Int {
        return sql.deleteFrom(feedbackTable)
            .where(feedbackTable.id.eq(feedbackId))
            .execute()
    }

    fun getFeedbackById(feedbackId: Long): FeedbackData? {
        return sql.selectFrom(feedbackTable)
            .where(feedbackTable.id.eq(feedbackId))
            .fetchOne(feedbackMapper)
    }

    fun getAllCompanyFeedbacks(companyId: Long): List<FeedbackData> {
        return sql.selectFrom(feedbackTable)
            .where(feedbackTable.companyId.eq(companyId))
            .fetch(feedbackMapper)
    }


    fun filterFeedbacks(filter: FeedbackFilter): List<FeedbackData> {
        val conditions = listOfNotNull(
            feedbackTable.companyId.eq(filter.companyId),
            filter.isAnonymous?.let { feedbackTable.isAnonymous.eq(it) },
            filter.status?.let { feedbackTable.status.eq(it) },
            filter.feedbackProviderId?.let { feedbackTable.feedbackProviderId.eq(it) },
            filter.department?.let { feedbackTable.department.eq(it.name) },
            filter.timeOfSubmitting?.let { time ->
                val dateToCompare = time.toLocalDate()
                val dateField = DSL.field("DATE({0})", LocalDate::class.java, feedbackTable.timeOfSubmitting)
                dateField.ge(dateToCompare)
            }
        )

        val query = sql.selectFrom(feedbackTable)

        if (conditions.isNotEmpty()) {
            query.where(conditions.reduce(Condition::and))
        }

        query.orderBy(feedbackTable.timeOfSubmitting.desc())

        return query.fetch(feedbackMapper)
    }

    fun switchFeedbackStatus(feedbackId: Long): Int {
        return sql.update(feedbackTable)
            .set(feedbackTable.status, DSL.not(feedbackTable.status))
            .where(feedbackTable.id.eq(feedbackId))
            .execute()
    }

    fun getFeedbackStatus(feedbackId: Long): StatusData? {
        return sql.select(feedbackTable.feedbackProviderId ,feedbackTable.status)
            .from(feedbackTable)
            .where(feedbackTable.id.eq(feedbackId))
            .fetchOne(statusMapper)
    }
}