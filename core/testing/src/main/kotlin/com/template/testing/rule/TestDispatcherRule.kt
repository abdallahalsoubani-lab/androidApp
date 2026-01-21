package com.template.testing.rule

import com.template.common.dispatcher.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * JUnit rule that sets up test dispatchers for all coroutine tests.
 * Use this rule to ensure all coroutines run on the test dispatcher.
 */
class TestDispatcherRule : TestWatcher() {
    private val testDispatcher = StandardTestDispatcher()

    /**
     * Returns a [DispatcherProvider] that uses the test dispatcher for all operations.
     */
    val testDispatcherProvider: DispatcherProvider = object : DispatcherProvider {
        override fun main(): CoroutineDispatcher = testDispatcher
        override fun default(): CoroutineDispatcher = testDispatcher
        override fun io(): CoroutineDispatcher = testDispatcher
        override fun unconfined(): CoroutineDispatcher = testDispatcher
    }

    override fun starting(description: Description?) {
        super.starting(description)
        kotlinx.coroutines.Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    fun runTest(block: suspend () -> Unit) {
        kotlinx.coroutines.test.runTest(testDispatcher) {
            block()
        }
    }
}
