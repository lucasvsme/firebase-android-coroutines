package firebase.coroutines.kotlinx.app

import android.os.Parcel
import android.os.Parcelable
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.database.*
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.firebase.android.await
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import readValue
import readValues

@RunWith(AndroidJUnit4::class)
class DatabaseReferenceIntegrationTest : BaseIntegrationTest() {

    @Test
    fun testStringCanBeManipulated() = runBlocking {
        val messagesNode = FirebaseDatabase.getInstance().getReference("tasks")

        val firstMessageNode = messagesNode.child("1")
        val secondMessageNode = messagesNode.child("2")

        try {
            firstMessageNode.setValue("Call Mary tomorrow morning").await()
            secondMessageNode.setValue("See the new TV show season").await()

            val first = firstMessageNode.readValue<String>()
            val second = secondMessageNode.readValue(String::class.java)

            assertThat(first, `is`(equalTo("Call Mary tomorrow morning")))
            assertThat(second, `is`(equalTo("See the new TV show season")))
        } catch (exception: FirebaseException) {
            fail("An exception should not be thrown here: ${exception.message}")
        }

        /* return */ Unit
    }

    @Test
    fun testDataClassWithSimpleObjectsCanBeManipulated() = runBlocking {
        val carsNode = FirebaseDatabase.getInstance().getReference("cars")

        val leyland = Car("Leyland Mini 1000", 1959, 8350.0)
        val mustang = Car("Ford Mustang", 1970, 65200.0)
        val fpace = Car("Jaguar F-Pace", 2017, 549999.0)

        try {
            setOf(leyland, mustang, fpace)
                    .map { carsNode.push().setValue(it) }
                    .map { it.await() }

            val carsRetrieved = carsNode.readValues<Car>()

            assertThat(carsRetrieved.size, `is`(equalTo(3)))
            assertThat(carsRetrieved, hasItem(leyland))
            assertThat(carsRetrieved, hasItem(mustang))
            assertThat(carsRetrieved, hasItem(fpace))
        } catch (exception: FirebaseException) {
            fail("An exception should not be thrown here: ${exception.message}")
        }

        /* return */ Unit
    }

    private data class Car(
            val model: String = "",
            val releaseYear: Int = 0,
            val price: Double = 0.0)

}
