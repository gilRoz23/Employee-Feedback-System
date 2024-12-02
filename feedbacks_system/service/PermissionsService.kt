package com.hibob.academy.feedbacks_system.service

import com.hibob.academy.feedbacks_system.Role
import jakarta.ws.rs.ForbiddenException
import org.springframework.stereotype.Component
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.core.Response

@Component
class PermissionService {
    fun extractPropertyAsLong(requestContext: ContainerRequestContext, propertyName: String): Long? {
        return requestContext.getProperty(propertyName)?.toString()?.toLongOrNull()
    }

    fun extractPropertyAsString(requestContext: ContainerRequestContext, propertyName: String): String? {
        return requestContext.getProperty(propertyName)?.toString()
    }

    fun validatePermission(role: String, permissions: List<Role>): Boolean {
        return permissions.contains(Role.valueOf(role.uppercase()))
    }

    fun handleWithPermission(requestContext: ContainerRequestContext, allowedRoles: List<Role>, action: (Long) -> Response): Response {
        val role = extractPropertyAsString(requestContext, "role") ?: return forbiddenResponse("Role is missing")
        val companyId = extractPropertyAsLong(requestContext, "companyId")
            ?: return badRequestResponse("Missing or invalid companyId")

        return if (validatePermission(role, allowedRoles)) {
            action(companyId)
        } else {
            forbiddenResponse("Access denied for role: $role")
        }
    }

    fun handleException(e: Throwable): Response {
        return when (e) {
            is IllegalArgumentException -> Response.status(Response.Status.BAD_REQUEST).entity(e.message).build()
            is ForbiddenException -> Response.status(Response.Status.FORBIDDEN).entity(e.message).build()
            else -> Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An unexpected error occurred").build()
        }
    }

    fun forbiddenResponse(message: String): Response =
        Response.status(Response.Status.FORBIDDEN).entity(message).build()

    fun badRequestResponse(message: String): Response =
        Response.status(Response.Status.BAD_REQUEST).entity(message).build()
}