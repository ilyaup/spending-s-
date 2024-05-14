package intern.liptsoft.spendings_api.notebook

import java.math.BigDecimal

data class Transaction(val category: Category, val value: BigDecimal, val month: String)
