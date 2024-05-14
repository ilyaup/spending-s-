package intern.liptsoft.spendings_api.note

import intern.liptsoft.spendings_api.notebook.Category
import intern.liptsoft.spendings_api.notebook.NotebookException
import intern.liptsoft.spendings_api.notebook.Transaction
import java.math.BigDecimal

class Notebook {
    private val categoryDict = mutableMapOf<String, Category>()
    private val categoryByMcc = mutableMapOf<Short, String>()
    private val transactions = mutableListOf<Transaction>()

    fun addMccsToCategory(categoryName: String, mcc: Short, vararg restMccs: Short) : Pair<List<Short>, List<Short>> {
        categoryDict.putIfAbsent(categoryName, Category(categoryName))
        val mccs = listOf(mcc) + restMccs.asList()
        val reservedMccs = mutableListOf<Short>()
        val addedMccs = mutableListOf<Short>()
        for (code in mccs) {
            if (code < 0 || code >= 9999) {
                throw NotebookException("MCC must be bigger or equal than 0 or less or equal than 9999, but $code is " +
                        "given.")
            }
            if (categoryByMcc.containsKey(code)) {
                reservedMccs.addLast(code)
            } else {
                categoryByMcc[code] = categoryName
                addedMccs.addLast(code)
            }
        }
        return Pair(addedMccs, reservedMccs)
    }
    fun addCategoriesToCategory(superCategoryName: String, subCategory: String, vararg subCategories: String) {
        val superCategory = categoryDict[superCategoryName] ?: run {
            categoryDict[superCategoryName] = Category(superCategoryName)
            categoryDict[superCategoryName]!!
        }
        val categoriesToAddNames = subCategories.asList() + subCategory
        for (categoryToAddName in categoriesToAddNames) {
            val categoryToAdd = categoryDict[categoryToAddName] ?: run {
                categoryDict[categoryToAddName] = Category(categoryToAddName)
                categoryDict[categoryToAddName]!!
            }
            superCategory.addSubCategory(categoryToAdd)
            categoryToAdd.addSuperCategory(superCategory)
        }
    }

    fun removeCategory(categoryName: String): Boolean {
        val category = categoryDict[categoryName] ?: run {
            return false
        }
        category.removeConnectionsWithOthers()
        categoryDict.remove(categoryName)
        return true
    }

    fun showCategories() : List<String> {
        return categoryDict.keys.toList()
    }

    fun addTransaction(categoryName: String, value: BigDecimal, month: String): String {
        val category = categoryDict[categoryName] ?: throw NotebookException("No such category: $categoryName.")
        transactions.addLast(Transaction(category, value, month))
        return category.name
    }

    fun addTransaction(mcc: Short, value: BigDecimal, month: String): String {
        val categoryName = categoryByMcc[mcc] ?: throw NotebookException("No category for this MCC: $mcc.")
        return addTransaction(categoryName, value, month)
    }

    fun removeTransaction(categoryName: String, value: BigDecimal, month: String) : Boolean {
        val category = categoryDict[categoryName] ?: throw NotebookException("No such category: $categoryName.")
        return transactions.remove(Transaction(category, value, month))
    }

    fun showCategoriesSpendingByMonth(month: String): Map<String, BigDecimal> {
        val spendingByCategory = mutableMapOf<String, BigDecimal>()
        for (transaction in transactions.filter { it.month == month }) {
            for (category in transaction.category.getAllSuperCategoriesTransitivelyIncluded()) {
                spendingByCategory.putIfAbsent(category.name, BigDecimal(0))
                spendingByCategory[category.name] = spendingByCategory[category.name]!!.add(transaction.value)
            }
        }
        return spendingByCategory
    }

    fun showMonthsSpendingByCategory(categoryName: String): Map<String, BigDecimal> {
        val spendingByMonth = mutableMapOf<String, BigDecimal>()
        val category = categoryDict[categoryName] ?: throw NotebookException("No such category: $categoryName.")
        val categories = category.getAllSubCategoriesTransitivelyIncluded()
        for (transaction in transactions.filter { categories.contains(it.category) }) {
            spendingByMonth.putIfAbsent(transaction.month, BigDecimal(0))
            spendingByMonth[transaction.month] = spendingByMonth[transaction.month]!!.add(transaction.value)
        }
        return spendingByMonth
    }
}
