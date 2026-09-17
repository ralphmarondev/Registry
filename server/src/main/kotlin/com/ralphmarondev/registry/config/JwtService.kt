package com.ralphmarondev.registry.config

import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService {
    private val key: SecretKey = Jwts.SIG.HS256.key().build()
    private val accessTokenExpirationMs: Long = 1000L * 60 * 60 // 1 hour
    private val refreshTokenExpirationMs: Long = 1000L * 60 * 60 * 24 * 7 // 7 days

    fun generateAccessToken(userId: Long, username: String, role: String): String {
        val now = Date()
        val expiry = Date(now.time + accessTokenExpirationMs)
        return Jwts.builder()
            .subject(userId.toString())
            .claim("username", username)
            .claim("role", role)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    fun generateRefreshToken(userId: Long, username: String): String {
        val now = Date()
        val expiry = Date(now.time + refreshTokenExpirationMs)
        return Jwts.builder()
            .subject(userId.toString())
            .claim("username", username)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    fun extractUserId(token: String): Long {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload.subject.toLong()
    }

    fun extractUsername(token: String): String {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
            .get("username", String::class.java)
    }

    fun extractRole(token: String): String {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
            .get("role", String::class.java)
    }

    fun isTokenValid(token: String): Boolean {
        return try {
            val claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
            !claims.payload.expiration.before(Date())
        } catch (_: Exception) {
            false
        }
    }
}