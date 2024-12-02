package com.hibob.academy.feedbacks_system

import java.time.LocalDateTime

enum class Department {
    HR,
    IT,
    SALES,
    MARKETING,
    FINANCE,
    OPERATIONS,
    RESEARCH_AND_DEVELOPMENT,
    CUSTOMER_SERVICE,
    LEGAL,
    OTHER
}


data class FeedbackData(
    val id: Long,
    val companyId: Long,
    val content: String,
    val isAnonymous: Boolean,
    val status: Boolean,
    val feedbackProviderId: Long?,
    val department: Department,
    val timeOfSubmitting: LocalDateTime,
    val sentimentScore: Long
)

data class EmployeeData(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val role: String,
    val companyId: Long
)


data class CompanyData(
    val id: Long,
    val name: String
)


data class FeedbackFilter(
    val companyId: Long,
    val isAnonymous: Boolean? = null,
    val status: Boolean? = null,
    val feedbackProviderId: Long? = null,
    val department: Department? = null,
    val timeOfSubmitting: LocalDateTime? = null
)

data class UserFeedbackFilter(
    val isAnonymous: Boolean? = null,
    val status: Boolean? = null,
    val feedbackProviderId: Long? = null,
    val department: Department? = null,
    val timeOfSubmitting: LocalDateTime? = null
)


data class FeedbackRequest(
    val content: String,
    val isAnonymous: Boolean,
    val department: Department
)

data class JWTDetails(val firstname: String, val lastname: String, val companyName: String)

enum class Role{
    MANAGER,
    ADMIN,
    HR,
    EMPLOYEE
}


data class ResponseData(
    val id: Long,
    val companyId: Long,
    val feedbackId: Long,
    val content: String,
    val responserId: Long?,
    val timeOfResponding: LocalDateTime
)

data class ResponseRequest(
    val content: String,
    val feedbackId: Long
)

data class StatusData(
    val status: Boolean,
    val feedbackProviderId: Long?
)
