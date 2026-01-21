package com.template.feature.template.domain.model

/**
 * Domain model for template items.
 */
data class TemplateItem(
    val id: String,
    val title: String,
    val description: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)
