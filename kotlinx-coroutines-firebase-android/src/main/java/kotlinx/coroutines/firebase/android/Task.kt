package kotlinx.coroutines.firebase.android

import com.google.android.gms.tasks.Task
import kotlin.coroutines.experimental.suspendCoroutine

/**
 *
 * @link https://developers.google.com/android/guides/tasks
 */
suspend fun <T> awaitTask(task: Task<T>): T = suspendCoroutine { continuation ->
    task.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            continuation.resume(task.result)
        } else {
            continuation.resumeWithException(task.exception!!)
        }
    }
}

suspend fun <T> Task<T>.await(): T = awaitTask(this)
