package org.reekwest.kontrakt.lens

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.throws
import org.junit.Test
import org.reekwest.kontrakt.boolean
import org.reekwest.kontrakt.dateTime
import org.reekwest.kontrakt.double
import org.reekwest.kontrakt.float
import org.reekwest.kontrakt.int
import org.reekwest.kontrakt.localDate
import org.reekwest.kontrakt.long
import org.reekwest.kontrakt.uuid
import org.reekwest.kontrakt.zonedDateTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class BiDiLensSpecContract {

    private val spec = BiDiLensSpec("location", Get.Companion { _: String, str: String ->
        if (str.isBlank()) emptyList() else listOf(str)
    },
        Set.Companion { _: String, values: List<String>, str: String -> values.fold(str, { memo, next -> memo + next }) })

    @Test
    fun `int`() = checkContract(spec.int(), "123", 123)

    @Test
    fun `long`() = checkContract(spec.long(), "123", 123)

    @Test
    fun `float`() = checkContract(spec.float(), "123.0", 123f)

    @Test
    fun `double`() = checkContract(spec.double(), "123.0", 123.0)

    @Test
    fun `local date`() = checkContract(spec.localDate(), "2001-01-01", LocalDate.of(2001, 1, 1))

    @Test
    fun `uuid`() = checkContract(spec.uuid(), "f5fc0a3f-ecb5-4ab3-bc75-185165dc4844", UUID.fromString("f5fc0a3f-ecb5-4ab3-bc75-185165dc4844"))

    @Test
    fun `boolean`() {
        checkContract(spec.boolean(), "true", true)
        checkContract(spec.boolean(), "false", false)
    }

    @Test
    fun `datetime`() = checkContract(spec.dateTime(), "2001-01-01T02:03:04", LocalDateTime.of(2001, 1, 1, 2, 3, 4))

    @Test
    fun `zoned datetime`() = checkContract(spec.zonedDateTime(), "1970-01-01T00:00:00Z[UTC]", ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))

    private fun <T> checkContract(spec: BiDiLensSpec<String, String, T>, valueAsString: String, tValue: T) {
        val optionalLens = spec.optional("hello")
        assertThat(optionalLens(valueAsString), equalTo(tValue))
        assertThat(optionalLens(""), absent())
        assertThat({ optionalLens("hello") }, throws(equalTo(LensFailure(optionalLens.invalid()))))
        assertThat(optionalLens(tValue, "original"), equalTo("original" + valueAsString))

        val optionalMultiLens = spec.multi.optional("hello")
        assertThat(optionalMultiLens(valueAsString), equalTo(listOf(tValue)))
        assertThat(optionalMultiLens(""), absent())
        assertThat({ optionalMultiLens("hello") }, throws(equalTo(LensFailure(optionalLens.invalid()))))
        assertThat(optionalMultiLens(listOf(tValue, tValue), "original"), equalTo("original" + valueAsString + valueAsString))

        val requiredLens = spec.required("hello")
        assertThat(requiredLens(valueAsString), equalTo(tValue))
        assertThat({ requiredLens("") }, throws(equalTo(LensFailure(requiredLens.missing()))))
        assertThat({ requiredLens("hello") }, throws(equalTo(LensFailure(requiredLens.invalid()))))
        assertThat(requiredLens(tValue, "original"), equalTo("original" + valueAsString))

        val requiredMultiLens = spec.multi.required("hello")
        assertThat(requiredMultiLens(valueAsString), equalTo(listOf(tValue)))
        assertThat({ requiredMultiLens("") }, throws(equalTo(LensFailure(requiredLens.missing()))))
        assertThat({ requiredMultiLens("hello") }, throws(equalTo(LensFailure(requiredLens.invalid()))))
        assertThat(requiredMultiLens(listOf(tValue, tValue), "original"), equalTo("original" + valueAsString + valueAsString))

    }


}
