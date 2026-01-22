package com.template.analytics

import timber.log.Timber

/**
 * Debug implementation of [AnalyticsTracker] that logs to Timber.
 * Use this in development instead of Firebase Analytics.
 */
class DebugAnalyticsTracker : AnalyticsTracker {
    override fun trackEvent(event: AnalyticsEvent) {
        Timber.d("Analytics Event: ${event.name} - Params: ${event.params}")
    }

    override fun setUserProperty(name: String, value: String) {
        Timber.d("Analytics Property: $name = $value")
    }

    override fun logScreenView(screenName: String) {
        Timber.d("Analytics Screen: $screenName")
    }

    override fun logError(throwable: Throwable, message: String?) {
        Timber.e(throwable, "Analytics Error: ${message ?: ""}")
    }
}
