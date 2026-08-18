package com.ralphmarondev.registry.exception

class InvalidCredentialsException(
    message: String = "Invalid credentials.",
) : RuntimeException(message)