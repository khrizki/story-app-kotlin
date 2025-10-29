package com.dicoding.picodiploma.loginwithanimation

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import java.util.concurrent.*

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.awaitValue(
    timeout: Long = 2,
    unit: TimeUnit = TimeUnit.SECONDS,
    onObserveAction: () -> Unit = {}
): T {
    var result: T? = null
    val latch = CountDownLatch(1)
    val liveDataObserver = object : Observer<T> {
        override fun onChanged(value: T) {
            result = value
            latch.countDown()
            this@awaitValue.removeObserver(this)
        }
    }

    this.observeForever(liveDataObserver)

    try {
        onObserveAction.invoke()

        if (!latch.await(timeout, unit)) {
            throw TimeoutException("LiveData value was not updated within the given time.")
        }
    } finally {
        this.removeObserver(liveDataObserver)
    }

    @Suppress("UNCHECKED_CAST")
    return result as T
}
