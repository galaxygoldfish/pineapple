package com.pineapple.app

import android.app.Application
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PineappleApp : Application() {

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
    }

}