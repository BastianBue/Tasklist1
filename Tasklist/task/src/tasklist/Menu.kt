package tasklist

import java.util.*

fun menu() {
    do {
        println("Input an action (add, print, edit, delete, end):")
        val line = readln().trim().lowercase(Locale.getDefault())
        when (line) {
            "end" -> println("Tasklist exiting!")
            "add" -> TaskList.addTask()
            "delete" -> {
                println(TaskList)
                if (!TaskList.isEmpty()) TaskList.deleteTask()
            }

            "edit" -> {
                println(TaskList)
                if (!TaskList.isEmpty()) TaskList.editTask()
            }

            "print" -> println(TaskList)
            else -> println("The input action is invalid")
        }
    } while (line != "end")
}