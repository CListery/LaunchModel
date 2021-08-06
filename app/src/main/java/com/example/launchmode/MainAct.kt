package com.example.launchmode

import android.app.ActivityManager
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModelProvider
import com.example.launchmode.databinding.ActMainBinding

open class MainAct : BaseAct<ActMainBinding>() {

    private lateinit var logViewModel: LogViewModel

    override fun binderCreator(savedInstanceState: Bundle?): ActMainBinding? {
        return ActMainBinding.inflate(layoutInflater)
    }

    override fun ActMainBinding.onInit(savedInstanceState: Bundle?) {
        logViewModel = ViewModelProvider(mAct).get(LogViewModel::class.java)

        supportActionBar?.title = TAG

        btnOpenStandard.setOnClickListener {
            log("Open Standard")
            startActivity(Intent(mCtx, StandardAct::class.java))
        }
        btnOpenSingleTask.setOnClickListener {
            log("Open SingleTask")
            startActivity(Intent(mCtx, SingleTaskAct::class.java))
        }
        btnOpenSingleTop.setOnClickListener {
            log("Open SingleTop")
            startActivity(Intent(mCtx, SingleTopAct::class.java))
        }
        btnOpenSingleInstance.setOnClickListener {
            log("Open SingleInstance")
            startActivity(Intent(mCtx, SingleInstanceAct::class.java))
        }
        btnOpenSingleTask2.setOnClickListener {
            log("Open SingleTask 2")
            startActivity(Intent(mCtx, SingleTaskAct2::class.java))
        }
        btnOpenSingleTop2.setOnClickListener {
            log("Open SingleTop 2")
            startActivity(Intent(mCtx, SingleTopAct2::class.java))
        }
        btnOpenSingleInstance2.setOnClickListener {
            log("Open SingleInstance 2")
            startActivity(Intent(mCtx, SingleInstanceAct2::class.java))
        }
        btnMoveTaskToBackYES.setOnClickListener {
            // 将包含此活动的任务移到活动堆栈的后面。 任务内的活动顺序不变
            val result = moveTaskToBack(true)
            Log.w("${TAG}[${mActID}]", "moveTaskToBack[${isTaskRoot}]: YES -> $result")
        }
        btnMoveTaskToBackNO.setOnClickListener {
            // 将包含此根节点的任务栈移到活动堆栈的后面，任务内的活动顺序不变
            val result = moveTaskToBack(false)
            Log.w("${TAG}[${mActID}]", "moveTaskToBack[${isTaskRoot}]: NO -> $result")
        }
        btnLoadTopActInfo.setOnClickListener {
            val activityManager = mCtx.getSystemService<ActivityManager>()
            val appTask = activityManager?.appTasks?.filterNotNull()?.find {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    it.taskInfo.taskId == taskId
                } else {
                    @Suppress("DEPRECATION")
                    it.taskInfo.id == taskId
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                log("${appTask?.taskInfo?.topActivity}")
            } else {
                @Suppress("DEPRECATION")
                log(
                    "${TAG}[${mActID}]: ${
                        activityManager?.getRunningTasks(Integer.MAX_VALUE)
                            ?.find { it.id == appTask?.taskInfo?.id }?.topActivity
                    }"
                )
            }
        }
        btnClose.setOnClickListener {
            log("Close")
            finish()
        }
        log("onCreate")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        log("onNewIntent")
    }

    override fun onStart() {
        super.onStart()
        log("onStart: $isChangingConfigurations")
    }

    override fun onRestart() {
        super.onRestart()
        log("onRestart")
    }

    override fun onResume() {
        super.onResume()
        logViewModel.logInfo.observe(mAct, {
            changeBinder {
                txtShower.text = it.logs.joinToString("\n")
                txtShower.post { scrollShower.scrollY = txtShower.height }
            }
        })
        log("onResume")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        log("onConfigurationChanged")
    }

    override fun onPause() {
        log("onPause")
        logViewModel.logInfo.removeObservers(mAct)
        super.onPause()
    }

    override fun onStop() {
        log("onStop: $isChangingConfigurations")
        super.onStop()
    }

    override fun onDestroy() {
        log("onDestroy")
        super.onDestroy()
    }

    private fun log(txt: String) {
        logViewModel.logInfo.append("$TAG: $txt")
    }
}

/**
 * ### 普通模式
 */
class StandardAct : MainAct()

/**
 * ### 单例回滚栈顶模式
 *
 * 1. 如存在栈中，且在栈顶，则直接调用 onNewIntent
 * 2. 如存在栈中，且不处于栈顶，则将栈进行回滚操作（将位于该 SingleTaskAct 之上的 Activity 先进先出的顺序按正常声明周期全部推出），并重新调用该实例的 onNewIntent
 *
 * #### Log:
 * ```log
 * 11:41:15.440 D/MainAct[819c2fc]: onCreate
 * 11:41:32.797 W/MainAct[819c2fc]: Open Standard
 * 11:41:32.820 D/StandardAct[8ac432f]: onCreate
 * 11:41:39.311 W/StandardAct[8ac432f]: Open SingleTask
 * 11:41:39.339 D/SingleTaskAct[5ab0b3e]: onCreate
 * 11:41:43.074 W/SingleTaskAct[5ab0b3e]: Open Standard
 * 11:41:43.093 D/StandardAct[9728922]: onCreate
 * 11:41:45.760 W/StandardAct[9728922]: Open Standard
 * 11:41:45.782 D/StandardAct[942e9c6]: onCreate
 * 11:41:47.504 W/StandardAct[942e9c6]: Open SingleTask
 * 11:41:47.508 D/StandardAct[9728922]: onDestroy
 * 11:41:47.521 D/SingleTaskAct[5ab0b3e]: onNewIntent
 * 11:41:48.004 D/StandardAct[942e9c6]: onDestroy
 * 11:41:52.021 W/SingleTaskAct[5ab0b3e]: Close
 * 11:41:52.577 D/SingleTaskAct[5ab0b3e]: onDestroy
 * 11:41:56.248 W/StandardAct[8ac432f]: Close
 * 11:41:56.815 D/StandardAct[8ac432f]: onDestroy
 * 11:42:00.964 W/MainAct[819c2fc]: Close
 * 11:42:01.601 D/MainAct[819c2fc]: onDestroy
 * ```
 */
class SingleTaskAct : MainAct()
class SingleTaskAct2 : MainAct()

/**
 * ### 栈顶单例模式
 *
 * 1. 如果存在栈中，且在栈顶，直接调用 onNewIntent
 * 2. 如果存在栈中，且不再在栈顶，则创建一个新实例放到栈顶
 *
 * ```log
 * 11:44:37.865 D/MainAct[819c2fc]: onCreate
 * 11:44:40.467 W/MainAct[819c2fc]: Open Standard
 * 11:44:40.489 D/StandardAct[8ac432f]: onCreate
 * 11:44:42.721 W/StandardAct[8ac432f]: Open SingleTop
 * 11:44:42.740 D/SingleTopAct[5ab0b3e]: onCreate
 * 11:44:43.874 W/SingleTopAct[5ab0b3e]: Open Standard
 * 11:44:43.890 D/StandardAct[9728922]: onCreate
 * 11:44:45.723 W/StandardAct[9728922]: Open Standard
 * 11:44:45.743 D/StandardAct[942e9c6]: onCreate
 * 11:44:46.935 W/StandardAct[942e9c6]: Open SingleTop
 * 11:44:46.953 D/SingleTopAct[294312a]: onCreate
 * 11:44:48.450 W/SingleTopAct[294312a]: Open SingleTop
 * 11:44:48.454 D/SingleTopAct[294312a]: onNewIntent
 * 11:44:50.952 W/SingleTopAct[294312a]: Close
 * 11:44:51.475 D/SingleTopAct[294312a]: onDestroy
 * 11:44:51.996 W/StandardAct[942e9c6]: Close
 * 11:44:52.574 D/StandardAct[942e9c6]: onDestroy
 * 11:44:53.097 W/StandardAct[9728922]: Close
 * 11:44:53.640 D/StandardAct[9728922]: onDestroy
 * 11:44:54.015 W/SingleTopAct[5ab0b3e]: Close
 * 11:44:54.559 D/SingleTopAct[5ab0b3e]: onDestroy
 * 11:44:54.908 W/StandardAct[8ac432f]: Close
 * 11:44:55.417 D/StandardAct[8ac432f]: onDestroy
 * 11:44:56.049 W/MainAct[819c2fc]: Close
 * 11:44:56.656 D/MainAct[819c2fc]: onDestroy
 * ```
 */
class SingleTopAct : MainAct()
class SingleTopAct2 : MainAct()

/**
 * ### 独立栈单例模式
 *
 * 1. 如 SingleInstanceAct 不存在任何 activity 栈中，创建一个独立的栈，可通过 taskAffinity 指定栈名称
 * 2. 如在任意一个栈中存在，则恢复 SingleInstanceAct 显示，并切换栈
 *
 * #### 使用注意
 * * 如启动顺序为 A(standard) -> B(singleInstance) ，且 B 为当前页面时，按下 home 键，再重新打开 APP，回到的页面为主任务栈的栈顶 A，而不是 B
 * * 如启动顺序为 A(standard) -> B(singleInstance) -> C(standard) 时，关闭 C 会直接回到 A，而不是 B，因为 A、C 位于同一个栈
 * * 当打开该模式的 activity 时会在 最近任务栈列表 中创建一个新的 任务栈，即使该 activity 销毁后依然会存在，可以使用 autoRemoveFromRecents 来自动移除
 * * API_30: 如启动顺序为 A(standard) -> B(standard) -> B(singleInstance)，且该 B 未定义 taskAffinity，当最后一个页面为 B 时，点击 HOME 键，A 所在栈的所有 activity 会被销毁
 * * API_30: 如启动顺序为 A(standard) -> B(singleInstance) -> C(standard) -> D(singleInstance) -> E(standard)，且该 B,D 未定义 taskAffinity，当最后一个页面为 E 时，点击 HOME 键，B,D 所在栈的所以 activity 会被销毁
 * * API_30: 当有 singleInstance 被打开时，且 taskAffinity 未定义时，当点击 home 键后，只保留最后一个 activity 所在栈
 * * 建议为 singleInstance 的 activity 配置不同的 taskAffinity，可以避免 API_30 造成的栈销毁
 * * moveTaskToBack(false) 始终会成功，因为使用该模式打开的 activity 是存在一个独立的栈中，且为根节点
 *
 * ```log
 * 11:47:13.720 D/StandardAct[748fd85]: onCreate
 * 11:47:15.273 W/StandardAct[748fd85]: Open Standard
 * 11:47:15.289 D/StandardAct[8ac432f]: onCreate
 * 11:47:17.027 W/StandardAct[8ac432f]: Open SingleInstance
 * 11:47:17.046 D/SingleInstanceAct[de136c0]: onCreate
 * 11:47:22.279 W/SingleInstanceAct[de136c0]: Open Standard
 * 11:47:22.298 D/StandardAct[9728922]: onCreate
 * 11:47:27.132 W/StandardAct[9728922]: Open Standard
 * 11:47:27.155 D/StandardAct[942e9c6]: onCreate
 * 11:47:29.732 W/StandardAct[942e9c6]: Open SingleInstance
 * 11:47:29.749 D/SingleInstanceAct[de136c0]: onNewIntent
 * 11:47:33.679 W/SingleInstanceAct[de136c0]: Open SingleInstance
 * 11:47:33.685 D/SingleInstanceAct[de136c0]: onNewIntent
 * 11:47:38.877 W/SingleInstanceAct[de136c0]: Close
 * 11:47:39.646 D/SingleInstanceAct[de136c0]: onDestroy
 * 11:47:40.887 W/StandardAct[942e9c6]: Close
 * 11:47:41.394 D/StandardAct[942e9c6]: onDestroy
 * 11:47:41.950 W/StandardAct[9728922]: Close
 * 11:47:42.536 D/StandardAct[9728922]: onDestroy
 * 11:47:43.002 W/StandardAct[8ac432f]: Close
 * 11:47:43.534 D/StandardAct[8ac432f]: onDestroy
 * 11:47:43.991 W/StandardAct[748fd85]: Close
 * 11:47:44.017 D/MainAct[35dcb66]: onCreate
 * 11:47:44.556 D/StandardAct[748fd85]: onDestroy
 * 11:47:44.998 W/MainAct[35dcb66]: Close
 * 11:47:45.650 D/MainAct[35dcb66]: onDestroy
 * ```
 */
class SingleInstanceAct : MainAct()
class SingleInstanceAct2 : MainAct()