package data

import java.time.LocalDate
import java.time.format.DateTimeParseException

data class Activity(
        val id: Long? = null,
        val role: Roles,
        val res: String,
        val ds: String,
        val de: String,
        val vol: String
) {
    /**
     *  Проверка валидности даты
     */
    fun hasValidDate(): Boolean {
        try {
            LocalDate.parse(ds)
            LocalDate.parse(de)
        } catch (error: DateTimeParseException) {
            return false
        }
        if (vol.toIntOrNull() == null) return false

        return true
    }
}
