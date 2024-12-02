package com.hibob.academy.feedbacks_system.resource

import com.hibob.academy.feedbacks_system.JWTDetails
import com.hibob.academy.feedbacks_system.service.SessionEmployeeService
import jakarta.annotation.PreDestroy
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.*
import org.springframework.stereotype.Controller


@Controller
@Path("api/v1/employee-feedback/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class SessionEmployeeResource(private val sessionEmployeeService: SessionEmployeeService) {
    companion object {
        const val COOKIE_NAME = "cookieVal"
    }

//    @POST
//    @Path("/login")
//    fun createJwtToken(jwtDet: JWTDetails): Response {
//        return try {
//            val token = sessionEmployeeService.createJwtToken(jwtDet)
//            Response.ok("User logged in")
//                .cookie(NewCookie.Builder(COOKIE_NAME).value(token).build())
//                .build()
//        } catch (e: Exception) {
//            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                .entity("${e.message}")
//                .build()
//        }
//    }

    @POST
    @Path("/logout")
    fun logout(): Response {
        return try {
            val cookie = NewCookie.Builder(COOKIE_NAME)
                .value("")
                .path("/api/v1/employee-feedback")
                .maxAge(0)
                .build()


            Response.ok("User logged out")
                .cookie(cookie)
                .build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("${e.message}")
                .build()
        }
    }

    @PreDestroy
    fun forceLogout(): Response{
        return logout()
    }

    @POST
    @Path("/login")
    fun createJwtToken(jwtDet: JWTDetails, @Context headers: HttpHeaders): Response {
        return try {
            // Step 1: Check if a token is already set in the cookies
            val existingToken = headers.cookies[COOKIE_NAME]?.value

            if (existingToken != null) {
                // Step 2: Decode the existing token and validate it
                val payload = sessionEmployeeService.decodeJwtPayload(existingToken)

                // Step 3: Compare the user in the existing token with the user trying to log in
                if (payload["firstname"] == jwtDet.firstname && payload["lastname"] == jwtDet.lastname) {
                    return Response.status(Response.Status.FORBIDDEN)
                        .entity("You are already logged in")
                        .build()
                } else {
                    return Response.status(Response.Status.FORBIDDEN)
                        .entity("You are already logged in with another account. Please log out first.")
                        .build()
                }
            }

            // Step 4: Generate and return a new JWT token since no active session exists
            val token = sessionEmployeeService.createJwtToken(jwtDet)

            // Return the login success response
            Response.ok("User logged in")
                .cookie(NewCookie.Builder(COOKIE_NAME).value(token).build())
                .build()

        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("${e.message}")
                .build()
        }
    }

//        @POST
//        @Path("/logout")
//        fun logout(@Context headers: HttpHeaders): Response {
//            return try {
//                // Step 1: Clear the session and invalidate the cookie
//                val cookie = NewCookie.Builder(COOKIE_NAME)
//                    .value("") // Clear the cookie value
//                    .path("/api/v1/employee-feedback")
//                    .maxAge(0) // Set max age to 0 to invalidate it
//                    .build()
//
//
//                Response.ok("User logged out")
//                    .cookie(cookie)
//                    .build()
//            } catch (e: Exception) {
//                Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                    .entity("${e.message}")
//                    .build()
//            }
//        }
}