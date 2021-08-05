package com.example.launchmode

import android.app.Application
import com.squareup.leakcanary.LeakCanary

class APP : Application() {

    override fun onCreate() {
        super.onCreate()

        initLeakCanary()

    }

    private fun initLeakCanary() {
        if(LeakCanary.isInAnalyzerProcess(this)){
            return
        }
        LeakCanary.install(this)
    }

}