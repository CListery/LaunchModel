package com.example.launchmode

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import java.io.Serializable

class LogViewModel : ViewModel() {

    companion object {
        @JvmStatic
        val sLogInfo: LogInfo by lazy { LogInfo() }
    }

    val logInfo = sLogInfo
}

class LogInfo : LiveData<LogInfo>(), Serializable {
    private val _logs: ArrayList<String> = arrayListOf()

    fun append(txt: String) {
        _logs.add(txt)
        postValue(this)
    }

    val logs get() = _logs.toList()
}
