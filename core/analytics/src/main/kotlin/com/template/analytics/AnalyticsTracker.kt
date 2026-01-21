package com.template.analytics

/**
 * Interface for tracking analytics events.
 * Implementation can be swapped between Firebase, custom analytics, or debug logger.
 */
interface AnalyticsTracker {
    /**
     * Track an analytics event.
     */
    fun trackEvent(event: AnalyticsEvent)

    /**
     * Set a user property.
     */
    fun setUserProperty(name: String, value: String)

    /**
     * Log a screen view.
     */
    fun logScreenView(screenName: String)

    /**
     * Log an error.
     */
    fun logError(throwable: Throwable, message: String? = null)
}
