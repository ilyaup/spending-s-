package notebook

import intern.liptsoft.spendings_api.note.Notebook
import org.junit.jupiter.api.Test

import kotlin.test.assertEquals

class NotebookTest {

    @Test
    fun addMccsToCategory_test1() {
        val note = Notebook()
        assertEquals("([123], [])", note.addMccsToCategory("game", 123).toString())
        assertEquals("([133, 333, 90], [])",
            note.addMccsToCategory("еда", 133, 333, 90).toString())
        assertEquals("([], [333])", note.addMccsToCategory("game", 333).toString())
    }
}