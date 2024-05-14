Dependencies:
JDK 21


Usage:
There is a .jar file in the root directory (the same directory where this README is located) called
"spendings-cli-standalone.jar". To run this jar, type "java -jar spendings-cli-standalone.jar" in the command line.


Commands:
1) Add MCC to category. If category does not exist, it will be created.
add category <category> <mcc> [mcc1] [mcc2]...
Example:
add category Фастфуд 5814

2) Add category to category. All non-existent categories will be created.
add group <category> <category to add> [category to add 2]...
Example:
add group Еда Фастфуд Рестораны

3) Remove category, if it exists.
remove category Рестораны

4) Show existing categories.
show categories

5) Add transaction, defining mcc/category, value (in roubles), and month.
add transaction by (mcc|category) (<mcc>|<category>) <value> <month>

Examples:
add transaction by category Еда 1000 Январь
add transaction by mcc 5814 500 Июнь

6) Remove transaction. Removes the first which fits.
remove transaction <category> <value> <month>
Example:
remove transaction Фастфуд 500 Июнь

7) Show spendings for particular month for evey category.
show month <month>
Example:
show month Январь

7) Show spendings for particular category for evey month.
show category <category>
Example:
show category Рестораны


Due to lack of tests, here are some more examples:
add category Game 3311
add category food 1 2
add group food rest
add transaction by category Game 100 yan
add transaction by mcc 3311 50 yan
add transaction by mcc 2 40 yan
add transaction by mcc 1 40 june
add transaction by mcc 1 40 june
add transaction by mcc 1 40 june
add transaction by category rest 1000 yan
remove transaction food 40 june
show categories
show month yan
show category food