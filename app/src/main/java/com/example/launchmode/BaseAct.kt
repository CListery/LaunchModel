package com.example.launchmode

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseAct<VB : ViewBinding> : AppCompatActivity() {

    protected val TAG by lazy { "${this::class.java.simpleName}[$mActID] TID:${taskId}" }

    abstract fun binderCreator(savedInstanceState: Bundle?): VB?

    private var _binder: VB? = null

    private val uiLooper by lazy { Looper.getMainLooper() }
    private val uiHandler by lazy { Handler(uiLooper, null) }

    @Suppress("UNCHECKED_CAST")
    protected val mAct: BaseAct<VB> by lazy { this }
    protected val mCtx: Context by lazy { this.baseContext }

    protected val mActID by lazy { mAct.memoryId }

    final override fun onCreate(savedInstanceState: Bundle?) {
        beforeCreate(savedInstanceState)
        super.onCreate(savedInstanceState)

        val binder = binderCreator(savedInstanceState)
        if (null == binder) {
            finish()
            return
        }

        _binder = binder
        setContentView(binder.root)

        binder.onInit(savedInstanceState)
    }

    open fun beforeCreate(savedInstanceState: Bundle?) {

    }

    abstract fun VB.onInit(savedInstanceState: Bundle?)

    fun changeBinder(changer: VB.() -> Unit): Boolean {
        if (null == _binder) {
            return false
        }
        if (uiLooper.isCurrentLooper) {
            try {
                _binder?.changer()
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        } else {
            uiHandler.post {
                changeBinder(changer)
            }
        }
        return true
    }
}

private val Any.memoryId
    get():String {
        return toString().replace(this::class.java.canonicalName ?: "", "").replace("@", "")
            .uppercase()
    }

private val Looper.isCurrentLooper
    get(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isCurrentThread
        } else {
            Thread.currentThread() == thread
        }
    }