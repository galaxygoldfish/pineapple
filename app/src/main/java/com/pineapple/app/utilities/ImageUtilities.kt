package com.pineapple.app.utilities

import com.pineapple.app.R

/**
 * Logic to determine the best user profile image to show.
 * - Priority 1: Snoovatar (if not blank)
 * - Priority 2: Icon Image (if not blank and not a default reddit avatar)
 * - Fallback: Generic placeholder drawable
 */
fun getBestUserImage(snoovatar: String?, icon: String?): Any {
    if (!snoovatar.isNullOrBlank()) {
        return snoovatar
    }
    if (!icon.isNullOrBlank() &&
        !icon.contains("avatar_default") &&
        !icon.contains("default_avatar")) {
        return icon
    }
    return R.drawable.generic_avatar
}
