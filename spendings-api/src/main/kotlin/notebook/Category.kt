package intern.liptsoft.spendings_api.notebook

import java.util.*

class Category(val name: String) {
    private val superCategories = mutableSetOf<Category>()
    private val subCategories = mutableSetOf<Category>()

    // isSub = true -> sub, isSub = false -> sup
    private fun getAllSubOrSuperCategoriesTransitivelyIncluded(isSub: Boolean): Set<Category> {
        val categories = mutableSetOf<Category>()
        val bfsQueue: Queue<Category> = LinkedList()
        bfsQueue.add(this)
        while (!bfsQueue.isEmpty()) {
            val curCategory = bfsQueue.remove()
            categories.add(curCategory)
            if (isSub) {
                for (sub in curCategory.subCategories) {
                    bfsQueue.add(sub)
                }
            } else {
                for (sup in curCategory.superCategories) {
                    bfsQueue.add(sup)
                }
            }
        }
        return categories
    }

    // all super categories transitively and the category itself
    fun getAllSuperCategoriesTransitivelyIncluded(): Set<Category> {
        return getAllSubOrSuperCategoriesTransitivelyIncluded(isSub = false)
    }

    // all sub categories transitively and the category itself
    fun getAllSubCategoriesTransitivelyIncluded(): Set<Category> {
        return getAllSubOrSuperCategoriesTransitivelyIncluded(isSub = true)
    }

    fun removeConnectionsWithOthers() {
        for (sup in superCategories) {
            sup.removeSubCategory(this)
        }
        for (sub in subCategories) {
            sub.removeSuperCategory(this)
        }
    }

    fun addSuperCategory(category: Category) {
        superCategories.add(category)
    }

    fun addSubCategory(category: Category) {
        subCategories.add(category)
    }

    fun removeSuperCategory(category: Category) {
        superCategories.remove(category)
    }

    fun removeSubCategory(category: Category) {
        subCategories.remove(category)
    }

    override fun toString(): String {
        return name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is Category) {
            return name.equals(other.name)
        } else {
            return false
        }
    }
}