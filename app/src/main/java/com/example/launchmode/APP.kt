package com.example.launchmode

import android.app.Application
import android.os.StrictMode
import android.util.Log
import com.yh.actmanager.ActManager
import com.yh.appinject.IBaseAppInject
import com.yh.appinject.logger.LogsManager
import com.yh.appinject.logger.logD

class APP : Application(), IBaseAppInject {

    override fun onCreate() {
        super.onCreate()

        LogsManager.get().setDefLoggerConfig(appConfig = (true to Log.VERBOSE))
        logD("onCreate")

        ActManager.get().apply {
            loggerConfig(true to Log.VERBOSE)
            register(this@APP)
            enableForcedStackTopMode(true)
        }
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .permitDiskReads()
                .penaltyLog()
                .penaltyDeath()
                .build()
        )
    }

    override fun getApplication() = this

    override fun getNotificationIcon() = R.mipmap.ic_launcher

    override fun showTipMsg(msg: String) {

    }

}