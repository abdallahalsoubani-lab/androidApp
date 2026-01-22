package com.template.testing.fakes

/**
 * Utility functions for creating mock data for testing.
 */

fun mockString(length: Int = 10): String {
    return (1..length).map { ('a'..'z').random() }.joinToString("")
}

fun mockInt(min: Int = 0, max: Int = 100): Int {
    return (min..max).random()
}

fun mockLong(min: Long = 0, max: Long = 1000): Long {
    return (min..max).random()
}

fun mockBoolean(): Boolean {
    return (0..1).random() == 1
}

fun <T> mockList(size: Int = 5, factory: () -> T): List<T> {
    return (1..size).map { factory() }
}
