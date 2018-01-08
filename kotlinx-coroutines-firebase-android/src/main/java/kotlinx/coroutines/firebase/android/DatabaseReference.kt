import com.google.firebase.FirebaseException
import com.google.firebase.database.*
import kotlin.coroutines.experimental.suspendCoroutine

/**
 *
 * @link https://firebase.google.com/docs/reference/android/com/google/firebase/database/Query.html#addListenerForSingleValueEvent(com.google.firebase.database.ValueEventListener)
 * @link https://firebase.google.com/docs/reference/android/com/google/firebase/database/DataSnapshot
 * @link https://firebase.google.com/docs/reference/android/com/google/firebase/database/DatabaseError
 */
suspend fun <T : Any> readReference(reference: DatabaseReference, type: Class<T>): T = suspendCoroutine { continuation ->
    reference.addListenerForSingleValueEvent(object : ValueEventListener {

        override fun onCancelled(error: DatabaseError) {
            val exception = when (error.toException()) {
                is FirebaseException -> error.toException()
                else -> Exception("The Firebase call was canceled")
            }

            continuation.resumeWithException(exception)
        }

        override fun onDataChange(snapshot: DataSnapshot) {
            try {
                val data: T? = snapshot.getValue(type)

                continuation.resume(data!!)
            } catch (exception: Exception) {
                continuation.resumeWithException(exception)
            }
        }
    })
}

suspend fun <T : Any> DatabaseReference.readValue(type: Class<T>): T = readReference(this, type)

suspend inline fun <reified T : Any> DatabaseReference.readValue(): T = readValue(T::class.java)

suspend fun <T : Any> readReferences(reference: DatabaseReference, type: Class<T>): Collection<T> = suspendCoroutine { continuation ->
    reference.addListenerForSingleValueEvent(object : ValueEventListener {

        override fun onCancelled(error: DatabaseError) {
            val exception = when (error.toException()) {
                is FirebaseException -> error.toException()
                else -> Exception("The Firebase call was canceled")
            }

            continuation.resumeWithException(exception)
        }

        override fun onDataChange(snapshot: DataSnapshot) {
            try {
                val data: List<T> = snapshot.children.toHashSet().map { it.getValue(type)!! }

                continuation.resume(data)
            } catch (exception: Exception) {
                continuation.resumeWithException(exception)
            }
        }
    })
}

suspend fun <T : Any> DatabaseReference.readValues(type: Class<T>): Collection<T> = readReferences(this, type)

suspend inline fun <reified T : Any> DatabaseReference.readValues(): Collection<T> = readValues(T::class.java)
