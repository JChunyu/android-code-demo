package com.example.test.lifecycle

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleEventObserver
import com.example.test.ui.theme.TestTheme
import com.example.test.ui.widget.CommonNavigationBar

/*
* 验证 Compose 方法在 容器的 onCreate 中创建，为什么还能观察到 Lifecycle.Event.ON_CREATE 事件
*
* 因为在 addObserver 时，这个方法会发送一次 add 时最后的事件
*
     override fun addObserver(observer: LifecycleObserver) {
        enforceMainThreadIfNeeded("addObserver")
        val initialState = if (state == State.DESTROYED) State.DESTROYED else State.INITIALIZED
        val statefulObserver = ObserverWithState(observer, initialState)
        val previous = observerMap.putIfAbsent(observer, statefulObserver)
        if (previous != null) {
            return
        }
        val lifecycleOwner = lifecycleOwner.get()
            ?: // it is null we should be destroyed. Fallback quickly
            return
        val isReentrance = addingObserverCounter != 0 || handlingEvent
        var targetState = calculateTargetState(observer)
        addingObserverCounter++
        while (statefulObserver.state < targetState && observerMap.contains(observer)
        ) {
            pushParentState(statefulObserver.state)
            val event = Event.upFrom(statefulObserver.state)
                ?: throw IllegalStateException("no event up from ${statefulObserver.state}")
            statefulObserver.dispatchEvent(lifecycleOwner, event)
            popParentState()
            // mState / subling may have been changed recalculate
            targetState = calculateTargetState(observer)
        }
        if (!isReentrance) {
            // we do sync only on the top level.
            sync()
        }
        addingObserverCounter--
    }
* 在这个方法中，
* ```
    val statefulObserver = ObserverWithState(observer, initialState)
    val targetState = calculateTargetState(observer)
    while (statefulObserver.state < targetState && observerMap.contains(observer)) {
        val event = Event.upFrom(statefulObserver.state)
        statefulObserver.dispatchEvent(lifecycleOwner, event)
    }
* ```
* 当你添加观察者时，如果当前 LifecycleOwner 的状态是 STARTED，则 targetState 被计算为 STARTED。
* 但新观察者的初始状态通常是 INITIALIZED，因此在 while 循环中，它会逐步推进状态，从 INITIALIZED -> CREATED -> STARTED。
* 这就会触发 ON_CREATE 和 ON_START 事件，确保新观察者的状态与当前 LifecycleOwner 的状态保持一致。
*
* 生命周期事件具有 顺序性，从 ON_CREATE 到 ON_DESTROY 是按顺序发生的。当你在 STARTED 状态中添加一个新观察者时，Lifecycle 会确保：
* 1.	如果观察者没有经历过 ON_CREATE，则补发 ON_CREATE。
* 2.	紧接着发送 ON_START，使其状态与 LifecycleOwner 的状态同步。
* 这种机制可以防止观察者错过任何它需要感知的状态变化。
*
* 如果你希望避免观察者接收某些事件，可以考虑以下方案：
* 方案 1：自定义状态过滤
    class CustomObserver : LifecycleObserver {
        private var isCreated = false

        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun onCreate() {
            if (isCreated) return
            isCreated = true
            // 处理 ON_CREATE 逻辑
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onStart() {
            // 处理 ON_START 逻辑
        }
    }
* */
class ComposeLifecycleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

    }

    override fun onStart() {
        super.onStart()
        setContent {
            TestTheme {
                LifecycleTestUI()
            }
        }
    }
}

@Composable
fun LifecycleTestUI() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            Log.d("LifecycleTestUI", "Lifecycle event - ${event.name}")
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CommonNavigationBar(
            leftArea = {
                Text(
                    text = "返回",
                    modifier = Modifier.clickable {
                        (context as? Activity)?.finish()
                    }
                )
            }
        )
    }
}