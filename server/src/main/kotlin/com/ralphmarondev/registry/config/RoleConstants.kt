package com.ralphmarondev.registry.config

object RoleConstants {
    const val ADMINISTRATOR = "ADMINISTRATOR"
    const val STAFF = "STAFF"
    const val FAMILY_ADMIN = "FAMILYADMIN"

    // Authority representation (with ROLE_ prefix for custom security checks)
    const val ROLE_ADMINISTRATOR = "ROLE_$ADMINISTRATOR"
    const val ROLE_STAFF = "ROLE_$STAFF"
    const val ROLE_FAMILY_ADMIN = "ROLE_$FAMILY_ADMIN"
}