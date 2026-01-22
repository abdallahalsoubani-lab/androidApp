package com.template.analytics

/**
 * Sealed class representing all analytics events in the application.
 */
sealed class AnalyticsEvent(
    val name: String,
    val params: Map<String, Any> = emptyMap(),
) {
    // Auth events
    data class LoginAttempt(val email: String) :
        AnalyticsEvent("login_attempt", mapOf("email" to email))

    data class LoginSuccess(val userId: String) :
        AnalyticsEvent("login_success", mapOf("user_id" to userId))

    data class LoginFailure(val reason: String) :
        AnalyticsEvent("login_failure", mapOf("reason" to reason))

    object LogoutEvent : AnalyticsEvent("logout")

    // Template events
    data class TemplateListViewed(val count: Int) :
        AnalyticsEvent("template_list_viewed", mapOf("count" to count))

    data class TemplateDetailViewed(val templateId: String) :
        AnalyticsEvent("template_detail_viewed", mapOf("template_id" to templateId))

    data class TemplateCreated(val templateId: String) :
        AnalyticsEvent("template_created", mapOf("template_id" to templateId))

    data class TemplateUpdated(val templateId: String) :
        AnalyticsEvent("template_updated", mapOf("template_id" to templateId))

    data class TemplateDeleted(val templateId: String) :
        AnalyticsEvent("template_deleted", mapOf("template_id" to templateId))

    // Custom event
    data class CustomEvent(
        val eventName: String,
        val eventParams: Map<String, Any> = emptyMap(),
    ) : AnalyticsEvent(eventName, eventParams)
}
