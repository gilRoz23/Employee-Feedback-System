package com.hibob.academy.feedbacks_system.service

import com.hibob.academy.feedbacks_system.*
import com.hibob.academy.feedbacks_system.EmployeeData
import com.hibob.academy.feedbacks_system.dao.CompanyDao
import com.hibob.academy.feedbacks_system.dao.EmployeeDao
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.TimeUnit
import javax.crypto.SecretKey
import java.util.Base64
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

@Component
class SessionEmployeeService(private val companyDao: CompanyDao, private val employeeDao: EmployeeDao) {
    // Generate a secure key for HS256
    companion object {
        val key: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    }


    fun createJwtToken(jwtDet: JWTDetails): String {
        val now = Date()
        val companyId: Long
        val employeeData: EmployeeData

        try {
            companyId = getCompanyByName(jwtDet.companyName).id
        } catch (e: IllegalArgumentException) {
            throw e
        }

        try {
            employeeData = getEmployee(jwtDet.firstname, jwtDet.lastname, companyId)
        } catch (e: IllegalArgumentException) {
            throw e
        }

        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .claim("firstname", jwtDet.firstname)
            .claim("lastname", jwtDet.lastname)
            .claim("companyId", companyId)
            .claim("employeeId", employeeData.id)
            .claim("role", employeeData.role)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + TimeUnit.HOURS.toMillis(24)))
            .signWith(SignatureAlgorithm.HS256, key)
            .compact()
    }

    fun getEmployee(firstname: String, lastname: String, companyId: Long): EmployeeData {
        val employeeData = employeeDao.getEmployee(firstname, lastname, companyId)
            ?: throw IllegalStateException("invalid details.")
        return employeeData
    }

    fun getCompanyByName(companyName: String): CompanyData {
        val companyData = companyDao.getCompanyByName(companyName)
            ?: throw IllegalStateException("invalid details.")
        return companyData
    }

    fun decodeJwtPayload(jwt: String): Map<String, Any> {
        try {
            // Split the JWT into its three parts: header, payload, and signature
            val parts = jwt.split(".")
            if (parts.size != 3) {
                throw IllegalArgumentException("Invalid JWT structure")
            }

            // Decode the payload (second part)
            val payloadJson = String(Base64.getUrlDecoder().decode(parts[1]))

            // Parse the payload JSON into a Map
            val objectMapper = jacksonObjectMapper()
            return objectMapper.readValue(payloadJson)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to decode JWT payload", e)
        }
    }
}