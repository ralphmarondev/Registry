package com.ralphmarondev.registry.config

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class CurrentUserProvider {
    fun getCurrentUserId(): Long {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication?.principal.toString().toLong()
    }
}