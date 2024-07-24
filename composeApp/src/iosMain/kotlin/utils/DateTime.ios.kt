package utils

import platform.Foundation.*

actual object DateTime {
    actual fun getFormattedDate(
        timestamp: String,
        inputFormat: String,
        outputFormat: String
    ): String {
        val df = NSDateFormatter().apply {
            dateFormat = inputFormat
            timeZone = NSTimeZone.timeZoneWithAbbreviation("GMT")!!
        }

        val date = df.dateFromString(timestamp)
        df.timeZone = NSTimeZone.localTimeZone
        df.dateFormat = outputFormat

        return df.stringFromDate(date!!)
    }

    actual fun formatTimeStamp(timeStamp: Long, outputFormat: String): String {
        val formatter = NSDateFormatter().apply {
            dateFormat = outputFormat
            timeZone = NSTimeZone.localTimeZone
        }
        val date = NSDate(timeStamp.toDouble() / 1000)
        return formatter.stringFromDate(date)
    }

    actual fun getDateInMilliSeconds(timeStamp: String, inputFormat: String): Long {
        if (timeStamp.trim().isEmpty()) return getCurrentTimeInMilliSeconds()

        val df = NSDateFormatter().apply {
            dateFormat = inputFormat
        }
        val date = df.dateFromString(timeStamp)
        return (date!!.timeIntervalSince1970 * 1000).toLong()
    }

    actual fun getCurrentTimeInMilliSeconds(): Long {
        return (NSDate().timeIntervalSince1970 * 1000).toLong()
    }

    actual fun getForwardedDate(
        forwardedDaya: Int,
        forwardedMonth: Int,
        outputFormat: String
    ): String {
        val calendar = NSCalendar.currentCalendar
        val currentDate = NSDate()
        val components = NSDateComponents().apply {
            day = forwardedDaya.toLong()
            month = forwardedMonth.toLong()
        }

        val forwardDate = calendar.dateByAddingComponents(components, currentDate, NSCalendarUnitDay)
        val dateFormatter = NSDateFormatter().apply {
            dateFormat = outputFormat
        }

        return dateFormatter.stringFromDate(forwardDate ?: currentDate)
    }
}