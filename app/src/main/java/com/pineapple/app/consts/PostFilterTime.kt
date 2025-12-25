package com.pineapple.app.consts

/**
 * Represents the options for time range supplied when filtering posts by top or controversial
 * These values are the same strings used in API requests
 */
object PostFilterTime {
    const val TIME_DAY = "day"
    const val TIME_MONTH = "month"
    const val TIME_WEEK = "week"
    const val TIME_YEAR = "year"
    const val TIME_ALL = "all"
}