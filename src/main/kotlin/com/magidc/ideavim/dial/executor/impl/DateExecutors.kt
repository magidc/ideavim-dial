package com.magidc.ideavim.dial.executor.impl

import com.magidc.ideavim.dial.executor.Executor
import com.magidc.ideavim.dial.executor.normalizedCaseWordSet
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.Temporal
import java.time.temporal.TemporalUnit
import java.util.*
import java.util.stream.Stream

enum class TimePattern(val dateTimePattern: String, val regexPattern: String) {
    DAY("dd", "(?:0[1-9]|[12]\\d|3[01])"),
    MONTH("MM", "(?:0[1-9]|1[0-2])"),
    YEAR_LONG("yyyy", "\\d{4}"),
    YEAR_SHORT("yy", "\\d{2}"),
    HOUR_24("HH", "(?:[01]\\d|2[0-3])"),
    HOUR_12("hh", "(?:0?[1-9]|1[0-2])"),
    MINUTES("mm", "[0-5]\\d"),
    SECONDS("ss", "[0-5]\\d"),
    TIME_ZONE("Z", "(?:Z|[+-]\\d{2}:?\\d{2})"),
    AM_PM("a", "\\s*(?:am|pm|AM|PM)");
}


object DateExecutors : ExecutorProvider {
    override val category = "dates"

    class DateTimeExecutor(
        regexPattern: String,
        dateTimePattern: String,
        temporalUnit: TemporalUnit,
        builder: (String, DateTimeFormatter) -> Temporal,
        locale: Locale = Locale.FRANCE,
        private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern).withLocale(locale)
    ) : Executor(
        category,
        "date",
        regexPattern,
        DateTimeExecutor@{ matchResult: MatchResult ->
            try {
                val parsedDate = matchResult.groups[0]?.value ?: return@DateTimeExecutor null
                formatter.format(builder.invoke(parsedDate, formatter).plus(1, temporalUnit))
            } catch (e: Throwable) {
                return@DateTimeExecutor null
            }
        },
        DateTimeExecutor@{ matchResult: MatchResult ->
            try {
                val parsedDate = matchResult.groups[0]?.value ?: return@DateTimeExecutor null
                formatter.format(builder.invoke(parsedDate, formatter).minus(1, temporalUnit))
            } catch (e: Throwable) {
                return@DateTimeExecutor null
            }
        },
        true,
        true
    ) {
        companion object {
            fun fromSeparator(
                timePatterns: List<TimePattern>,
                separator: String,
                temporalUnit: TemporalUnit,
                builder: (String, DateTimeFormatter) -> Temporal,
                locale: Locale = Locale.FRANCE
            ): DateTimeExecutor {
                val regexSeparator = if (separator == ".") "\\." else separator
                return DateTimeExecutor(
                    "\\b" + timePatterns.joinToString(regexSeparator) { it.regexPattern } + "\\b",
                    timePatterns.joinToString(separator) { it.dateTimePattern },
                    temporalUnit, builder, locale
                )
            }

            fun fromTemplate(timePatterns: List<TimePattern>, template: String, temporalUnit: TemporalUnit, builder: (String, DateTimeFormatter) -> Temporal): DateTimeExecutor {
                var regexPattern = "\\b$template\\b"
                var dateTimePattern = template
                for (timePattern in timePatterns) {
                    regexPattern = regexPattern.replaceFirst("{}", timePattern.regexPattern)
                    dateTimePattern = dateTimePattern.replaceFirst("{}", timePattern.dateTimePattern)
                }
                dateTimePattern = dateTimePattern.replace("T", "'T'")
                return DateTimeExecutor(regexPattern, dateTimePattern, temporalUnit, builder)
            }
        }
    }


    fun isMonthFirst(): Boolean {
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.getDefault())
        return formatter.format(LocalDate.of(2023, 11, 22)).startsWith("11/");
    }


    override fun buildExecutors(): List<Executor> {
        val executors = mutableListOf(
            normalizedCaseWordSet(category, "weekdays", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"),
            normalizedCaseWordSet(category, "months", "january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december"),

            // ISO dates (yyyy/MM/dd)
            DateTimeExecutor.fromSeparator(
                listOf(TimePattern.YEAR_LONG, TimePattern.MONTH, TimePattern.DAY),
                "/",
                ChronoUnit.DAYS,
                LocalDate::parse
            ),

            // ISO dates (yyyy.MM.dd)
            DateTimeExecutor.fromSeparator(
                listOf(TimePattern.YEAR_LONG, TimePattern.MONTH, TimePattern.DAY),
                ".",
                ChronoUnit.DAYS,
                LocalDate::parse
            ),
            // ISO dates (yyyy-MM-dd)
            DateTimeExecutor.fromSeparator(
                listOf(TimePattern.YEAR_LONG, TimePattern.MONTH, TimePattern.DAY),
                "-",
                ChronoUnit.DAYS,
                LocalDate::parse
            ),
            // ISO datetime (yyyy-MM-dd'T'HH:mm:ss)
            DateTimeExecutor.fromTemplate(
                listOf(TimePattern.YEAR_LONG, TimePattern.MONTH, TimePattern.DAY, TimePattern.HOUR_24, TimePattern.MINUTES, TimePattern.SECONDS),
                "{}-{}-{}T{}:{}:{}",
                ChronoUnit.SECONDS,
                LocalDateTime::parse
            ),
            // ISO datetime with timezone (yyyy-MM-dd'T'HH:mm:ssZ)
            DateTimeExecutor.fromTemplate(
                listOf(TimePattern.YEAR_LONG, TimePattern.MONTH, TimePattern.DAY, TimePattern.HOUR_24, TimePattern.MINUTES, TimePattern.SECONDS, TimePattern.TIME_ZONE),
                "{}-{}-{}T{}:{}:{}{}",
                ChronoUnit.SECONDS,
                ZonedDateTime::parse
            ),
            // Time 24h with seconds (HH:mm:ss)
            DateTimeExecutor.fromSeparator(
                listOf(TimePattern.HOUR_24, TimePattern.MINUTES, TimePattern.SECONDS),
                ":",
                ChronoUnit.SECONDS,
                LocalTime::parse
            ),
            // Time 12h (hh:mm am/pm)
            DateTimeExecutor(
                "\\b${TimePattern.HOUR_12.regexPattern}:${TimePattern.MINUTES.regexPattern}(?!:[0-5]\\d) ${TimePattern.AM_PM.regexPattern}\\b",
                "${TimePattern.HOUR_12.dateTimePattern}:${TimePattern.MINUTES.dateTimePattern} ${TimePattern.AM_PM.dateTimePattern}",
                ChronoUnit.MINUTES,
                LocalTime::parse
            ),
            // Time 24h (HH:mm)
            DateTimeExecutor(
                "\\b${TimePattern.HOUR_24.regexPattern}:${TimePattern.MINUTES.regexPattern}(?!:[0-5]\\d)\\b",
                "${TimePattern.HOUR_24.dateTimePattern}:${TimePattern.MINUTES.dateTimePattern}",
                ChronoUnit.MINUTES,
                LocalTime::parse
            )
        )
        val usDateFormatExecutors = Stream.of("-", "/", ".")
            .map { separator ->
                DateTimeExecutor.fromSeparator(
                    listOf(TimePattern.MONTH, TimePattern.DAY, TimePattern.YEAR_LONG),
                    separator,
                    ChronoUnit.DAYS,
                    LocalDate::parse,
                    Locale.US
                )
            }
            .toList()
        val defaultDateFormatExecutors = Stream.of("-", "/", ".")
            .map { separator ->
                DateTimeExecutor.fromSeparator(
                    listOf(TimePattern.DAY, TimePattern.MONTH, TimePattern.YEAR_LONG),
                    separator,
                    ChronoUnit.DAYS,
                    LocalDate::parse,
                    Locale.US
                )
            }
            .toList()

        if (isMonthFirst()) {
            executors.addAll(usDateFormatExecutors)
            executors.addAll(defaultDateFormatExecutors)
        } else {
            executors.addAll(defaultDateFormatExecutors)
            executors.addAll(usDateFormatExecutors)
        }

        return executors
    }
}
