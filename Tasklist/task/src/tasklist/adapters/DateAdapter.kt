package tasklist.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.text.ParseException


var customLocalDateTimeAdapter: Any = object : Any() {
    @ToJson
    @Synchronized
    fun dateToJson(d: LocalDateTime?): String? {
        return d.toString()
    }

    @FromJson
    @Synchronized
    @Throws(ParseException::class)
    fun dateToJson(s: String?): LocalDateTime? {
        return s!!.toLocalDateTime()
    }
}