@file:Suppress("unused")

package backend.util

import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Description
import org.hamcrest.TypeSafeDiagnosingMatcher
import java.io.IOException
import java.lang.Byte.parseByte
import java.time.ZonedDateTime
import java.time.ZonedDateTime.parse
import java.time.format.DateTimeParseException
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

//fun User.unlockUser() {
//    apply {
//        if (id != null) {
//            id = null
//            version = null
//        }
//    }
//}

private val mapper = createObjectMapper()

private fun createObjectMapper() =
    ObjectMapper().apply {
        configure(WRITE_DURATIONS_AS_TIMESTAMPS, false)
        setSerializationInclusion(NON_EMPTY)
        registerModule(JavaTimeModule())
    }

/**
 * Convert an object to JSON byte array.
 *
 * @param object the object to convert.
 * @return the JSON byte array.
 * @throws IOException
 */
@Throws(IOException::class)
fun convertObjectToJsonBytes(`object`: Any): ByteArray = mapper.writeValueAsBytes(`object`)

/**
 * Create a byte array with a specific size filled with specified data.
 *
 * @param size the size of the byte array.
 * @param data the data to put in the byte array.
 * @return the JSON byte array.
 */
fun createByteArray(size: Int, data: String) = ByteArray(size) { parseByte(data, 2) }

/**
 * A matcher that tests that the examined string represents the same instant as the reference datetime.
 */
class ZonedDateTimeMatcher(private val date: ZonedDateTime) : TypeSafeDiagnosingMatcher<String>() {

    override fun matchesSafely(item: String, mismatchDescription: Description): Boolean {
        try {
            if (!date.isEqual(parse(item))) {
                mismatchDescription.appendText("was ").appendValue(item)
                return false
            }
            return true
        } catch (e: DateTimeParseException) {
            mismatchDescription.appendText("was ")
                .appendValue(item)
                .appendText(", which could not be parsed as a ZonedDateTime")
            return false
        }
    }

    override fun describeTo(description: Description) {
        description.appendText("a String representing the same Instant as ")
            .appendValue(date)
    }
}

/**
 * Creates a matcher that matches when the examined string represents the same instant as the reference datetime.
 * @param date the reference datetime against which the examined string is checked.
 */
fun sameInstant(date: ZonedDateTime) = ZonedDateTimeMatcher(date)

/**
 * Verifies the equals/hashcode contract on the domain object.
 */
fun <T : Any> equalsVerifier(clazz: KClass<T>) {
    clazz.createInstance().apply i@{
        assertThat(toString()).isNotNull
        assertThat(this).isEqualTo(this)
        assertThat(hashCode()).isEqualTo(hashCode())
        // Test with an instance of another class
        assertThat(this).isNotEqualTo(Any())
        assertThat(this).isNotEqualTo(null)
        // Test with an instance of the same class
        clazz.createInstance().apply j@{
            assertThat(this@i).isNotEqualTo(this@j)
            // HashCodes are equals because the objects are not persisted yet
            assertThat(this@i.hashCode()).isEqualTo(this@j.hashCode())
        }
    }
}

