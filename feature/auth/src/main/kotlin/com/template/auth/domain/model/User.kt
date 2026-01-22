package com.template.auth.domain.model

/**
 * Domain model for a user.
 */
data class User(
    val id: String,
    val email: String,
    val name: String,
    val avatar: String? = null,
)
