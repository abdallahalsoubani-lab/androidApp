package com.template.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Generic API response wrapper.
 * This can be adapted to match your actual API response format.
 */
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    @SerialName("error_code")
    val errorCode: String? = null,
)
