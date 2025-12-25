package com.pineapple.app.consts

/**
 * Represent the available options for sorting posts in a list
 * These values represent the same strings that are sent in an API request
 */
object PostFilterSort {
    const val SORT_HOT = "hot"
    const val SORT_NEW = "new"
    const val SORT_TOP = "top"
    const val SORT_RISING = "rising"
    const val SORT_CONTROVERSIAL = "controversial"
}