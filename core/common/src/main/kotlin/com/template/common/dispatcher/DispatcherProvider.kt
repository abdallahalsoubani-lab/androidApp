package com.template.common.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Provides coroutine dispatchers for different types of work.
 * This allows for dependency injection and testing with fake dispatchers.
 */
interface DispatcherProvider {
    /**
     * Dispatcher for main thread operations (UI updates).
     */
    fun main(): CoroutineDispatcher

    /**
     * Dispatcher for CPU-bound work.
     */
    fun default(): CoroutineDispatcher

    /**
     * Dispatcher for I/O operations (network, disk, database).
     */
    fun io(): CoroutineDispatcher

    /**
     * Dispatcher for lightweight work that shouldn't block threads.
     */
    fun unconfined(): CoroutineDispatcher
}

/**
 * Default implementation using Android dispatchers.
 */
class DefaultDispatcherProvider : DispatcherProvider {
    override fun main(): CoroutineDispatcher = Dispatchers.Main
    override fun default(): CoroutineDispatcher = Dispatchers.Default
    override fun io(): CoroutineDispatcher = Dispatchers.IO
    override fun unconfined(): CoroutineDispatcher = Dispatchers.Unconfined
}
