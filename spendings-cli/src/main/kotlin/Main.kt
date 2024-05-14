package intern.liptsoft.spendings_cli

import intern.liptsoft.spendings_api.note.Notebook
import intern.liptsoft.spendings_api.notebook.NotebookException
import java.math.BigDecimal

fun listToBeautifulString(list: List<Any?>) = list.toString().drop(1).dropLast(1)

val commands = mapOf<String, (notebook: Notebook, args: List<String>) -> (Unit)>(
    "add category" to { notebook, args ->
        try {
            val result = notebook.addMccsToCategory(
                categoryName = args[0], mcc = args[1].toShort(),
                restMccs = args.drop(2).map { it.toShort() }.toShortArray()
            )
            if (result.first.isNotEmpty()) {
                println("Added MCC's: ${listToBeautifulString(result.first)}.")
            }
            if (result.second.isNotEmpty()) {
                println("Already reserved, therefore not added MCC's: ${listToBeautifulString(result.second)}.")
            }
        } catch (e: NumberFormatException) {
            println("MCC code is not valid. Is should be a number which is >= 0 and <= 9999. Try again.")
        } catch (e: NotebookException) {
            println(e.message)
        }
    },
    "add group" to { notebook, args ->
        notebook.addCategoriesToCategory(
            superCategoryName = args[0], subCategory = args[1],
            subCategories = args.drop(2).toTypedArray()
        )
        println("Categories ${listToBeautifulString(args.drop(1))} added to category ${args[0]}.")
    },
    "remove category" to { notebook, args ->
        val isRemoved = notebook.removeCategory(categoryName = args[0])
        if (isRemoved) {
            println("Category ${args[0]} is removed.")
        } else {
            println("Category ${args[0]} does not exist, not removed.")
        }
    },
    "show categories" to { notebook, _ ->
        val categories = notebook.showCategories()
        if (categories.isEmpty()) {
            println("There are no categories yet.")
        } else {
            println("Categories: ${listToBeautifulString(categories)}.")
        }
    },
    "add transaction" to { notebook, args ->
        try {
            if (args[0] == "by" && args[1] == "mcc") { // add transaction by mcc 1111 100 yan
                val categoryName = notebook.addTransaction(args[2].toShort(), BigDecimal(args[3]), args[4])
                println("Transaction added to category $categoryName.")
            } else if (args[0] == "by" && args[1] == "category") {
                val categoryName = notebook.addTransaction(args[2], BigDecimal(args[3]), args[4])
                println("Transaction added to category $categoryName.")
            } else {
                println("No such command. Try again.")
            }
        } catch (e: NumberFormatException) {
            println("MCC or value is not valid. MCC must be >= 0 and <= 9999 and value must be decimal number.")
        } catch (e: NotebookException) {
            println(e.message)
        }
    },
    "remove transaction" to { notebook, args ->
        try {
            val isRemoved = notebook.removeTransaction(args[0], BigDecimal(args[1]), args[2])
            if (isRemoved) {
                println("Transaction is deleted.")
            } else {
                println("Transaction is not deleted.")
            }
        } catch (e: NumberFormatException) {
            println("Value is not valid. It must be a decimal number.")
        } catch (e: NotebookException) {
            println(e.message)
        }
    },
    "show month" to { notebook, args ->
        for ((category, value) in notebook.showCategoriesSpendingByMonth(args[0])) {
            println("$category ${value}р")
        }
    },
    "show category" to { notebook, args ->
        try {
            for ((month, value) in notebook.showMonthsSpendingByCategory(args[0])) {
                println("$month ${value}р")
            }
        } catch (e: NotebookException) {
            println(e.message)
        }
    }
)

fun main() {
    println("Type 'exit' (without quotes) to quit the program.")
    val notebook = Notebook()
    while (true) {
        val commandString = readln()
        if (commandString == "exit") {
            break
        }
        val commandArgs = commandString.split(" ")
        val command = commands[commandArgs.take(2).joinToString(" ")]
        if (command == null) {
            println("No such command. Try again.")
        } else {
            command(notebook, commandArgs.drop(2))
        }
    }
}