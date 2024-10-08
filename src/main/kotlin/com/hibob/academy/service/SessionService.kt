package com.hibob.academy.service
import com.hibob.academy.resource.JWTDetails
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.TimeUnit
import javax.crypto.SecretKey

@Component
class SessionService {


// Generate a secure key for HS256
companion object {
    val key: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
}


    fun createJwtToken(jwtDet : JWTDetails): String {
        val now = Date()
        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .claim("email", "${jwtDet.email}")
            .claim("userName", "${jwtDet.userName}")
            .claim("isAdmin", "${jwtDet.isAdmin}")
            .setIssuedAt(now)
            .setExpiration(Date(now.time + TimeUnit.HOURS.toMillis(24)))
            .signWith(SignatureAlgorithm.HS256, key)
            .compact()
    }
}