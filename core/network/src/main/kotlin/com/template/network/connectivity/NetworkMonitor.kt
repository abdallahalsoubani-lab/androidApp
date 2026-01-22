package com.template.network.connectivity

import kotlinx.coroutines.flow.Flow

/**
 * Interface for monitoring network connectivity status.
 */
interface NetworkMonitor {
    /**
     * Flow that emits the current network connectivity status.
     * true when connected, false when disconnected.
     */
    val isOnline: Flow<Boolean>
}
