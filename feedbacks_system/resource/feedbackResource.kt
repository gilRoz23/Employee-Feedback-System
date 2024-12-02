package com.hibob.academy.feedbacks_system.resource

import com.hibob.academy.feedbacks_system.FeedbackRequest
import com.hibob.academy.feedbacks_system.service.PermissionService
import com.hibob.academy.feedbacks_system.Role
import com.hibob.academy.feedbacks_system.service.FeedbackService
import com.hibob.academy.feedbacks_system.UserFeedbackFilter
import jakarta.ws.rs.*
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Controller

@Controller
@Path("/api/v1/employee-feedback")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class FeedbackResource(
    private val feedbackService: FeedbackService,
    private val permissionService: PermissionService
) {

    @POST
    @Path("/submit-feedback")
    fun submitFeedback(@Context requestContext: ContainerRequestContext, feedbackRequest: FeedbackRequest): Response {
        val feedbackProvider: Long? = if (feedbackRequest.isAnonymous) null
        else permissionService.extractPropertyAsLong(requestContext, "employeeId")

        val companyId = permissionService.extractPropertyAsLong(requestContext, "companyId")
            ?: return permissionService.badRequestResponse("Missing or invalid companyId")

        val sentimentScore = feedbackService.analyzeSentiment(feedbackRequest.content)

        //like a try and catch
        return runCatching {
            feedbackService.insertFeedback(
                companyId,
                feedbackRequest.content,
                feedbackRequest.isAnonymous,
                feedbackProvider,
                feedbackRequest.department,
                sentimentScore.toLong()
            )
            Response.status(Response.Status.CREATED).entity("Feedback submitted successfully").build()
        }.getOrElse { e ->
            permissionService.handleException(e)
        }
    }

    @GET
    @Path("/get-all-feedbacks")
    fun getAllFeedbacks(@Context requestContext: ContainerRequestContext): Response {
        return permissionService.handleWithPermission(requestContext, listOf(Role.HR, Role.ADMIN)) { companyId ->
            Response.ok(feedbackService.getAllCompanyFeedbacks(companyId)).build()
        }
    }

    @GET
    @Path("/filter-feedbacks")
    fun filterFeedbacks(@Context requestContext: ContainerRequestContext, userFeedbackFilter: UserFeedbackFilter): Response {
        return permissionService.handleWithPermission(requestContext, listOf(Role.HR, Role.ADMIN)) { companyId ->
            Response.ok(feedbackService.filterFeedbacks(companyId, userFeedbackFilter)).build()
        }
    }

    @PATCH
    @Path("/switch-review-status/feedback-id/{feedbackId}")
    fun switchFeedbackStatus(@Context requestContext: ContainerRequestContext, @PathParam("feedbackId") feedbackId: Long): Response {
        return permissionService.handleWithPermission(requestContext, listOf(Role.HR)) {
            runCatching {
                feedbackService.switchFeedbackStatus(feedbackId)
                Response.ok("Switched review status successfully").build()
            }.getOrElse { e ->
                permissionService.handleException(e)
            }
        }
    }

    @GET
    @Path("/check-status/feedback-id/{feedbackId}")
    fun getFeedbackStatus(@Context requestContext: ContainerRequestContext, @PathParam("feedbackId") feedbackId: Long): Response {
        val employeeId = permissionService.extractPropertyAsLong(requestContext, "employeeId")
            ?: return permissionService.forbiddenResponse("Employee ID is missing")

        return runCatching {
            Response.ok(feedbackService.getFeedbackStatus(feedbackId, employeeId)).build()
        }.getOrElse { e ->
            permissionService.handleException(e)
        }
    }
}