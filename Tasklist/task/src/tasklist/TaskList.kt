package tasklist

import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import tasklist.adapters.customLocalDateTimeAdapter
import java.io.File
import java.io.FileNotFoundException
import java.util.*

object TaskList {
    @Json(name = "tasks")
    private val tasks: MutableList<Task> = mutableListOf()

    @OptIn(ExperimentalStdlibApi::class)
    private val jsonAdapter: JsonAdapter<List<Task>> =
        Moshi.Builder().add(customLocalDateTimeAdapter).addLast(KotlinJsonAdapterFactory()).build().adapter()

    init {
        loadFromFile()
    }

    @JsonClass(generateAdapter = true)
    data class Task(
        var currentIndex: Int, var description: String, var priority: Priority, var deadline: LocalDateTime
    ) {
        enum class Priority(val color: String) {
            C("\u001B[101m \u001B[0m"),
            H("\u001B[103m \u001B[0m"),
            N("\u001B[102m \u001B[0m"),
            L("\u001B[104m \u001B[0m");
        }

        enum class DueTag(val color: String) {
            I("\u001B[102m \u001B[0m"),
            T("\u001B[103m \u001B[0m"),
            O("\u001B[101m \u001B[0m")
        }

        private fun getDueTag(): DueTag {
            val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date
            val numberOfDays =
                currentDate.daysUntil(LocalDate(deadline.year, deadline.monthNumber, deadline.dayOfMonth))
            return when {
                numberOfDays == 0 -> DueTag.T
                numberOfDays < 0 -> DueTag.O
                numberOfDays > 0 -> DueTag.I
                else -> throw Exception("error while trying to calculate due tag")
            }
        }

        private fun getDateString(): String = "${deadline.year}-${deadline.monthNumber.toString().padStart(2, '0')}-${
            deadline.dayOfMonth.toString().padStart(2, '0')
        }"

        private fun getTimeString(): String =
            "${deadline.hour.toString().padStart(2, '0')}:${deadline.minute.toString().padStart(2, '0')}"

        override fun toString(): String {
            val descriptionLines = description.split("\n").flatMap { it.chunked(44) }
            return buildString {
                append("\n")
                append("| ${currentIndex.toString().padEnd(3, ' ')}")
                append("| ${getDateString()} ")
                append("| ${getTimeString()} ")
                append("| ${priority.color} ")
                append("| ${getDueTag().color} ")
                descriptionLines.forEachIndexed { index, line ->
                    if (index == 0) {
                        append("|${line.trimStart().padEnd(44, ' ')}|\n")
                    } else {
                        append("|    |            |       |   |   |${line.trimStart().padEnd(44, ' ')}|\n")
                    }
                }
                append("+----+------------+-------+---+---+--------------------------------------------+")
            }
        }
    }

    fun isEmpty(): Boolean = tasks.isEmpty()

    private fun queryTaskPriority(): Task.Priority {
        println("Input the task priority (C, H, N, L):")
        return try {
            Task.Priority.valueOf(readln().uppercase(Locale.getDefault()))
        } catch (e: Exception) {
            queryTaskPriority()
        }
    }

    private fun queryDate(): LocalDate {
        println("Input the date (yyyy-mm-dd):")
        return try {
            val (year, month, days) = readln().split("-").map { it.toInt() }
            LocalDate(year, month, days)
        } catch (e: Exception) {
            println("The input date is invalid")
            queryDate()
        }
    }

    private fun queryTime(localDate: LocalDate): LocalDateTime {
        println("Input the time (hh:mm):")
        return try {
            val (hours, minutes) = readln().split(":").map { it.toInt() }
            LocalDateTime(localDate.year, localDate.monthNumber, localDate.dayOfMonth, hours, minutes, 0)
        } catch (e: Exception) {
            println("The input time is invalid")
            queryTime(localDate)
        }
    }


    fun queryDescription(): String {
        println("Input a new task (enter a blank line to end):")
        var description = ""
        do {
            val input = readln().trim()
            if (input == "") continue
            description += "${input}\n"
        } while (input != "")
        return description.trim()
    }

    fun writeToFile() {
        val json: String = jsonAdapter.toJson(this.tasks)
        val file = File("tasklist.json")
        file.createNewFile()
        file.writeText(json)
    }

    fun loadFromFile() = try {
        val file = File("tasklist.json")
        val tasks = jsonAdapter.fromJson(file.readText()) ?: throw Exception("Could not load tasks from file")

        tasks.forEach {
            addTask(it)
        }
    } catch (e: FileNotFoundException) {
    }

    fun addTask(task: TaskList.Task) {
        tasks.add(task)
    }

    fun addTask() {
        val priority = queryTaskPriority()
        val date = queryDate()
        val dateTime = queryTime(date)
        val taskIndex = tasks.size + 1
        val description = queryDescription()

        if (description.trim() == "") println("The task is blank") else tasks.add(
            Task(
                taskIndex, description, priority, dateTime
            )
        )
        writeToFile()
    }

    private fun queryTaskIndex(): Int {
        println("Input the task number (1-${tasks.size}):")
        val inputString = readln()
        return try {
            val taskIndex = inputString.toInt() - 1
            tasks[taskIndex]
            taskIndex
        } catch (e: Exception) {
            println("Invalid task number")
            queryTaskIndex()
        }
    }

    fun deleteTask() {
        val taskIndex = queryTaskIndex()
        tasks.removeAt(taskIndex)
        println("The task is deleted")
        for (i in taskIndex until tasks.size) {
            val task = tasks[i]
            task.currentIndex--
        }
        writeToFile()
    }

    private fun queryEditedField(): String {
        println("Input a field to edit (priority, date, time, task):")
        return try {
            val userInput = readln().lowercase(Locale.getDefault())
            if (!"(priority|date|time|task)".toRegex().matches(userInput)) throw Exception("Invalid field")
            userInput
        } catch (e: Exception) {
            println(e.message)
            queryEditedField()
        }
    }

    fun editTask() {
        val taskIndex = queryTaskIndex()
        val field = queryEditedField()
        val task = tasks[taskIndex]
        when (field) {
            "priority" -> task.priority = queryTaskPriority()
            "date" -> {
                val date = queryDate()
                val oldDate = task.deadline
                val newDateTime =
                    LocalDateTime(date.year, date.monthNumber, date.dayOfMonth, oldDate.hour, oldDate.minute)
                tasks[taskIndex].deadline = newDateTime
            }

            "time" -> {
                val oldDate = task.deadline
                val newDateTime = queryTime(LocalDate(oldDate.year, oldDate.monthNumber, oldDate.dayOfMonth))
                tasks[taskIndex].deadline = newDateTime
            }

            "task" -> task.description = queryDescription()
        }
        println("The task is changed")
        writeToFile()
    }

    override fun toString(): String = buildString {
        if (tasks.isEmpty()) append("No tasks have been input") else {
            append(
                """
            +----+------------+-------+---+---+--------------------------------------------+
            | N  |    Date    | Time  | P | D |                   Task                     |
            +----+------------+-------+---+---+--------------------------------------------+
        """.trimIndent()
            )
            tasks.forEach { append("$it") }
        }
    }
}