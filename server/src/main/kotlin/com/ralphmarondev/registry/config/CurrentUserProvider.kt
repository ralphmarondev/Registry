package com.ralphmarondev.registry.config

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class CurrentUserProvider {

    fun getCurrentUserId(): Long {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication?.principal.toString().toLong()
    }

    fun isStaffOrAdmin(): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication ?: return false

        return authentication.authorities.any { authority ->
            val authStr = authority.authority
            authStr.equals(RoleConstants.ROLE_ADMINISTRATOR, ignoreCase = true) ||
                    authStr.equals(RoleConstants.ROLE_STAFF, ignoreCase = true)
        }
    }
}