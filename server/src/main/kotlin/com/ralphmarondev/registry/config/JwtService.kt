package com.ralphmarondev.registry.config

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService {
    private val key = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    private val accessTokenExpirationMs: Long = 1000L * 60 * 60 // 1 hour
    private val refreshTokenExpirationMs: Long = 1000L * 60 * 60 * 24 * 7 // 7 days

    fun generateAccessToken(userId: Long, username: String): String {
        val now = Date()
        val expiry = Date(now.time + accessTokenExpirationMs)
        return Jwts.builder()
            .setSubject(userId.toString())
            .claim("username", username)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key)
            .compact()
    }

    fun generateRefreshToken(userId: Long, username: String): String {
        val now = Date()
        val expiry = Date(now.time + refreshTokenExpirationMs)
        return Jwts.builder()
            .setSubject(userId.toString())
            .claim("username", username)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key)
            .compact()
    }

    fun extractUserId(token: String): Long {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body.subject.toLong()
    }

    fun extractUsername(token: String): String {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
            .get("username", String::class.java)
    }

    fun isTokenValid(token: String): Boolean {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
            !claims.body.expiration.before(Date())
        } catch (_: Exception) {
            false
        }
    }
}