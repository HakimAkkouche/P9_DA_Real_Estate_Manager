package com.haksoftware.p9_da_real_estate_manager.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


// Utilitaire pour obtenir la valeur d'un LiveData
fun <T> LiveData<T>.getOrAwaitValue(): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(value: T) {
            data = value
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)
    try {
        latch.await(2, TimeUnit.SECONDS)
    } finally {
        this.removeObserver(observer)
    }
    @Suppress("UNCHECKED_CAST")
    return data as T
}
object StateFlowTestUtils {
    suspend fun <T> getValueForTesting(stateFlow: StateFlow<T>): T {
        // Initialize a variable to hold the value emitted by the StateFlow
        var value: T? = null

        // Create a job to collect the first value from the StateFlow
        val job = CoroutineScope(Dispatchers.Default).launch {
            stateFlow.collect { t ->
                value = t
                cancel() // Cancel the collection after receiving the first value
            }
        }

        // Wait for the job to complete, which happens after the first value is collected
        job.join()

        // Return the collected value, assuming it is non-null
        // This !! operator is safe as long as the StateFlow is initialized with a value, as StateFlow cannot hold null values
        return value!!
    }
}
