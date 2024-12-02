package com.hibob.academy.feedbacks_system

import com.hibob.academy.utils.JooqTable

class FeedbackTable(tableName : String = "feedback") : JooqTable(tableName) {
    val id = createBigIntField("id")
    val companyId = createBigIntField("company_id")
    val content = createVarcharField("content")
    val isAnonymous = createBooleanField("is_anonymous")
    val status = createBooleanField("status")
    val feedbackProviderId = createBigIntField("feedback_provider_id")
    val department = createVarcharField("department")
    val timeOfSubmitting = createLocalDateTimeField("time_of_submitting")
    val sentimentScore = createBigIntField("sentiment_score")


    companion object{
        val instance = FeedbackTable()
    }
}

class EmployeeTable(tableName: String = "employee") : JooqTable(tableName) {
    val id = createBigIntField("id")
    val firstName = createVarcharField("first_name")
    val lastName = createVarcharField("last_name")
    val role = createVarcharField("role")
    val companyId = createBigIntField("company_id")

    companion object {
        val instance = EmployeeTable()
    }
}


class CompanyTable(tableName : String = "company") : JooqTable(tableName) {
    val id = createBigIntField("id")
    val name = createVarcharField("name")


    companion object{
        val instance = CompanyTable()
    }
}

class ResponseTable(tableName : String = "response") : JooqTable(tableName) {
    val id = createBigIntField("id")
    val companyId = createBigIntField("company_id")
    val feedbackId = createBigIntField("feedback_id")
    val content = createVarcharField("content")
    val responserId = createBigIntField("responser_id")
    val timeOfResponding = createLocalDateTimeField("time_of_responsing")


    companion object{
        val instance = ResponseTable()
    }
}