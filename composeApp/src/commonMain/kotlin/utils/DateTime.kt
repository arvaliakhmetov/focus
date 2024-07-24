package utils

expect object DateTime {
    fun getFormattedDate(
        timestamp: String,
        inputFormat: String,
        outputFormat: String
    ): String

    fun formatTimeStamp(
        timeStamp: Long,
        outputFormat: String = "yyyy-MM-dd"
    ): String

    fun getDateInMilliSeconds(timeStamp: String, inputFormat: String): Long

    fun getCurrentTimeInMilliSeconds(): Long

    fun getForwardedDate(
        forwardedDaya: Int = 0,
        forwardedMonth: Int = 0,
        outputFormat: String = "DATE_IN_FORMAT_yyyy_MM_dd_T_HH_mm_ss"
    ): String
}