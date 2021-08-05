# LaunchModel 简介

这是一个研究 Android activity 的启动模式的项目，让你更清楚的了解到每个 launchMode 各有什么特点

## launchMode(s)

### standard

* 普通模式：最常用的模式，所有没有声明 launchMode 的 activity 都是使用该模式

### singleTask

* 单例回滚栈顶模式
  1. 如存在栈中，且在栈顶，则直接调用 onNewIntent
  2. 如存在栈中，且不处于栈顶，则将栈进行回滚操作（将位于该 SingleTaskAct 之上的 Activity 先进先出的顺序按正常声明周期全部推出），并重新调用该实例的 onNewIntent

* log

  ```log
  11:41:15.440 D/MainAct[819c2fc]: onCreate
  11:41:32.797 W/MainAct[819c2fc]: Open Standard
  11:41:32.820 D/StandardAct[8ac432f]: onCreate
  11:41:39.311 W/StandardAct[8ac432f]: Open SingleTask
  11:41:39.339 D/SingleTaskAct[5ab0b3e]: onCreate
  11:41:43.074 W/SingleTaskAct[5ab0b3e]: Open Standard
  11:41:43.093 D/StandardAct[9728922]: onCreate
  11:41:45.760 W/StandardAct[9728922]: Open Standard
  11:41:45.782 D/StandardAct[942e9c6]: onCreate
  11:41:47.504 W/StandardAct[942e9c6]: Open SingleTask
  11:41:47.508 D/StandardAct[9728922]: onDestroy
  11:41:47.521 D/SingleTaskAct[5ab0b3e]: onNewIntent
  11:41:48.004 D/StandardAct[942e9c6]: onDestroy
  11:41:52.021 W/SingleTaskAct[5ab0b3e]: Close
  11:41:52.577 D/SingleTaskAct[5ab0b3e]: onDestroy
  11:41:56.248 W/StandardAct[8ac432f]: Close
  11:41:56.815 D/StandardAct[8ac432f]: onDestroy
  11:42:00.964 W/MainAct[819c2fc]: Close
  11:42:01.601 D/MainAct[819c2fc]: onDestroy
  ```

### singleTop

* 栈顶单例模式
  1. 如果存在栈中，且在栈顶，直接调用 onNewIntent
  2. 如果存在栈中，且不再在栈顶，则创建一个新实例放到栈顶

* log

  ```log
  11:44:37.865 D/MainAct[819c2fc]: onCreate
  11:44:40.467 W/MainAct[819c2fc]: Open Standard
  11:44:40.489 D/StandardAct[8ac432f]: onCreate
  11:44:42.721 W/StandardAct[8ac432f]: Open SingleTop
  11:44:42.740 D/SingleTopAct[5ab0b3e]: onCreate
  11:44:43.874 W/SingleTopAct[5ab0b3e]: Open Standard
  11:44:43.890 D/StandardAct[9728922]: onCreate
  11:44:45.723 W/StandardAct[9728922]: Open Standard
  11:44:45.743 D/StandardAct[942e9c6]: onCreate
  11:44:46.935 W/StandardAct[942e9c6]: Open SingleTop
  11:44:46.953 D/SingleTopAct[294312a]: onCreate
  11:44:48.450 W/SingleTopAct[294312a]: Open SingleTop
  11:44:48.454 D/SingleTopAct[294312a]: onNewIntent
  11:44:50.952 W/SingleTopAct[294312a]: Close
  11:44:51.475 D/SingleTopAct[294312a]: onDestroy
  11:44:51.996 W/StandardAct[942e9c6]: Close
  11:44:52.574 D/StandardAct[942e9c6]: onDestroy
  11:44:53.097 W/StandardAct[9728922]: Close
  11:44:53.640 D/StandardAct[9728922]: onDestroy
  11:44:54.015 W/SingleTopAct[5ab0b3e]: Close
  11:44:54.559 D/SingleTopAct[5ab0b3e]: onDestroy
  11:44:54.908 W/StandardAct[8ac432f]: Close
  11:44:55.417 D/StandardAct[8ac432f]: onDestroy
  11:44:56.049 W/MainAct[819c2fc]: Close
  11:44:56.656 D/MainAct[819c2fc]: onDestroy
  ```

### singleInstance

* 独立栈单例模式
  1. 如 SingleInstanceAct 不存在任何 activity 栈中，创建一个独立的栈，可通过 taskAffinity 指定栈名称
  2. 如在任意一个栈中存在，则恢复 SingleInstanceAct 显示，并切换栈
  
* ***使用注意***
  * 如启动顺序为 A(standard) -> B(singleInstance) ，且 B 为当前页面时，按下 home 键，再重新打开 APP，回到的页面为主任务栈的栈顶 A，而不是 B
  * 如启动顺序为 A(standard) -> B(singleInstance) -> C(standard) 时，关闭 C 会直接回到 A，而不是 B，因为 A、C 位于同一个栈
  * 当打开该模式的 activity 时会在 最近任务栈列表 中创建一个新的 任务栈，即使该 activity 销毁后依然会存在，可以使用 autoRemoveFromRecents 来自动移除
  * API_30: 如启动顺序为 A(standard) -> B(standard) -> B(singleInstance)，且该 B 未定义 taskAffinity，当最后一个页面为 B 时，点击 HOME 键，A 所在栈的所有 activity 会被销毁
  * API_30: 如启动顺序为 A(standard) -> B(singleInstance) -> C(standard) -> D(singleInstance) -> E(standard)，且该 B,D 未定义 taskAffinity，当最后一个页面为 E 时，点击 HOME 键，B,D 所在栈的所以 activity 会被销毁
  * API_30: 当有 singleInstance 被打开时，且 taskAffinity 未定义时，当点击 home 键后，只保留最后一个 activity 所在栈
  * 建议为 singleInstance 的 activity 配置不同的 taskAffinity，可以避免 API_30 造成的栈销毁
  * moveTaskToBack(false) 始终会成功，因为使用该模式打开的 activity 是存在一个独立的栈中，且为根节点

* log

  ```log
  11:47:13.720 D/StandardAct[748fd85]: onCreate
  11:47:15.273 W/StandardAct[748fd85]: Open Standard
  11:47:15.289 D/StandardAct[8ac432f]: onCreate
  11:47:17.027 W/StandardAct[8ac432f]: Open SingleInstance
  11:47:17.046 D/SingleInstanceAct[de136c0]: onCreate
  11:47:22.279 W/SingleInstanceAct[de136c0]: Open Standard
  11:47:22.298 D/StandardAct[9728922]: onCreate
  11:47:27.132 W/StandardAct[9728922]: Open Standard
  11:47:27.155 D/StandardAct[942e9c6]: onCreate
  11:47:29.732 W/StandardAct[942e9c6]: Open SingleInstance
  11:47:29.749 D/SingleInstanceAct[de136c0]: onNewIntent
  11:47:33.679 W/SingleInstanceAct[de136c0]: Open SingleInstance
  11:47:33.685 D/SingleInstanceAct[de136c0]: onNewIntent
  11:47:38.877 W/SingleInstanceAct[de136c0]: Close
  11:47:39.646 D/SingleInstanceAct[de136c0]: onDestroy
  11:47:40.887 W/StandardAct[942e9c6]: Close
  11:47:41.394 D/StandardAct[942e9c6]: onDestroy
  11:47:41.950 W/StandardAct[9728922]: Close
  11:47:42.536 D/StandardAct[9728922]: onDestroy
  11:47:43.002 W/StandardAct[8ac432f]: Close
  11:47:43.534 D/StandardAct[8ac432f]: onDestroy
  11:47:43.991 W/StandardAct[748fd85]: Close
  11:47:44.017 D/MainAct[35dcb66]: onCreate
  11:47:44.556 D/StandardAct[748fd85]: onDestroy
  11:47:44.998 W/MainAct[35dcb66]: Close
  11:47:45.650 D/MainAct[35dcb66]: onDestroy
  ```
