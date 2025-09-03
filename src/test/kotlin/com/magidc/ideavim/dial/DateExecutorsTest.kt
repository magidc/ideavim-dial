package com.magidc.ideavim.dial

import com.magidc.ideavim.dial.executor.impl.DateExecutors
import org.assertj.core.api.Assertions.assertThat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import java.util.Locale

class DateExecutorsTest : BaseTest() {
    override fun getDefinitions(): String = DateExecutors.category

    companion object {
        val testDateSet: List<ZonedDateTime> = listOf(
            ZonedDateTime.parse("2023-01-10T00:00:00Z"),
            ZonedDateTime.parse("2023-02-28T00:00:00Z"),
            ZonedDateTime.parse("2024-02-28T00:00:00Z"),
            ZonedDateTime.parse("2024-06-30T00:00:00Z"),
            ZonedDateTime.parse("2024-07-30T00:00:00Z")
        )
    }

    private fun testDateTimes(datePattern: String, temporalUnit: TemporalUnit, locale: Locale) {
        assertThat(execute(CARET + "value = monday")).isEqualTo("value = ${CARET}tuesday")
        assertThat(execute(CARET + "value = Sunday")).isEqualTo("value = ${CARET}Monday")
        assertThat(execute(CARET + "value = JUNE")).isEqualTo("value = ${CARET}JULY")

        val formatter = DateTimeFormatter.ofPattern(datePattern).withLocale(locale)
        for (date in testDateSet) {
            val formattedDate = formatter.format(date)
            val formattedDatePlusOne = formatter.format(date.plus(1, temporalUnit))
            // Test incrementing the date
            assertThat(execute(CARET + "value = $formattedDate")).isEqualTo("value = $CARET$formattedDatePlusOne")
            assertThat(execute("value =$CARET $formattedDate")).isEqualTo("value = $CARET$formattedDatePlusOne")
            assertThat(execute("value = $formattedDate$CARET")).isEqualTo("value = $formattedDate$CARET")
            assertThat(execute("value$CARET = $formattedDate")).isEqualTo("value = $CARET$formattedDatePlusOne")

            assertThat(execute("value = ${formattedDate.take(3)}" + CARET + formattedDate.substring(3))).isEqualTo("value = $CARET$formattedDatePlusOne")
            // Test decrementing the date
            val formattedDateMinusOne = formatter.format(date.minus(1, temporalUnit))
            assertThat(execute(CARET + "value = $formattedDate", true)).isEqualTo("value = $CARET$formattedDateMinusOne")
            assertThat(execute("value =$CARET $formattedDate", true)).isEqualTo("value = $CARET$formattedDateMinusOne")
            assertThat(execute("value = $formattedDate$CARET", true)).isEqualTo("value = $formattedDate$CARET")
            assertThat(execute("value$CARET = $formattedDate", true)).isEqualTo("value = $CARET$formattedDateMinusOne")

            assertThat(execute("value = ${formattedDate.take(3)}" + CARET + formattedDate.substring(3), true)).isEqualTo("value = $CARET$formattedDateMinusOne")
        }
    }

    fun testDefaultDates() {
        Locale.setDefault(Locale.FRANCE)
        testDateTimes("yyyy-MM-dd", ChronoUnit.DAYS, Locale.FRANCE)
        testDateTimes("dd.MM.yyyy", ChronoUnit.DAYS, Locale.FRANCE)
        testDateTimes("dd/MM/yyyy", ChronoUnit.DAYS, Locale.FRANCE)
        testDateTimes("dd-MM-yyyy", ChronoUnit.DAYS, Locale.FRANCE)
        testDateTimes("HH:mm", ChronoUnit.MINUTES, Locale.FRANCE)
        testDateTimes("HH:mm:ss", ChronoUnit.SECONDS, Locale.FRANCE)
        testDateTimes("hh:mm a", ChronoUnit.MINUTES, Locale.FRANCE)
        testDateTimes("yyyy-MM-dd'T'HH:mm:ss", ChronoUnit.SECONDS, Locale.FRANCE)
        testDateTimes("yyyy-MM-dd'T'HH:mm:ssZ", ChronoUnit.SECONDS, Locale.FRANCE)
    }

    fun testUSDates() {
        Locale.setDefault(Locale.US)
        testDateTimes("yyyy-MM-dd", ChronoUnit.DAYS, Locale.US)
        testDateTimes("MM.dd.yyyy", ChronoUnit.DAYS, Locale.US)
        testDateTimes("MM/dd/yyyy", ChronoUnit.DAYS, Locale.US)
        testDateTimes("MM-dd-yyyy", ChronoUnit.DAYS, Locale.US)
        testDateTimes("HH:mm", ChronoUnit.MINUTES, Locale.US)
        testDateTimes("HH:mm:ss", ChronoUnit.SECONDS, Locale.US)
        testDateTimes("hh:mm a", ChronoUnit.MINUTES, Locale.US)
        testDateTimes("yyyy-MM-dd'T'HH:mm:ss", ChronoUnit.SECONDS, Locale.US)
        testDateTimes("yyyy-MM-dd'T'HH:mm:ssZ", ChronoUnit.SECONDS, Locale.US)
    }
}
