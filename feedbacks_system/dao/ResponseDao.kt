package com.hibob.academy.feedbacks_system.dao

import com.hibob.academy.feedbacks_system.ResponseData
import com.hibob.academy.feedbacks_system.ResponseTable
import org.jooq.DSLContext
import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ResponseDao(private val sql: DSLContext) {
    private val responseTable = ResponseTable.instance

    private val responseMapper = RecordMapper<org.jooq.Record, ResponseData> { record ->
        ResponseData(
            id = record[responseTable.id],
            companyId = record[responseTable.companyId],
            feedbackId = record[responseTable.feedbackId],
            content = record[responseTable.content],
            responserId = record[responseTable.responserId],
            timeOfResponding = record[responseTable.timeOfResponding]
        )
    }


    fun insertResponse(
        companyId: Long,
        feedbackId: Long,
        content: String,
        responserId: Long?,
        timeOfResponding: LocalDateTime = LocalDateTime.now()
    ): Long {
        return sql.insertInto(responseTable)
            .set(responseTable.companyId, companyId)
            .set(responseTable.feedbackId, feedbackId)
            .set(responseTable.content, content)
            .set(responseTable.responserId, responserId)
            .set(responseTable.timeOfResponding, timeOfResponding)
            .returning(responseTable.id)
            .fetchOne()!![responseTable.id]
    }

    fun deleteResponse(responseId: Long): Int {
        return sql.deleteFrom(responseTable)
            .where(responseTable.id.eq(responseId))
            .execute()
    }

    fun getResponseById(responseId: Long): ResponseData? {
        return sql.selectFrom(responseTable)
            .where(responseTable.id.eq(responseId))
            .fetchOne(responseMapper)
    }


    fun getAllCompanyResponses(companyId: Long): List<ResponseData> {
        return sql.selectFrom(responseTable)
            .where(responseTable.companyId.eq(companyId))
            .orderBy(responseTable.timeOfResponding.asc())
            .fetch(responseMapper)
    }

    fun getResponseByFeedbackId(feedbackId: Long): List<ResponseData> {
        return sql.selectFrom(responseTable)
            .where(responseTable.feedbackId.eq(feedbackId))
            .orderBy(responseTable.timeOfResponding.asc())
            .fetch(responseMapper)
    }
}