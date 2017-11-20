package com.shoeboxscientist.mouthmaths

/**
 * Created by adamconnors on 11/2/17.
 */
class TablesEngine(val howMany: Int,
                   val whichTables: ArrayList<Int>,
                   val randomFunction: (Int) -> Int = { (Math.random() * it).toInt() }) {

    // Constant for the range of multipliers (the times tables) we're going to use.
    val TWELVE = 12

    fun generateTablesList() : ArrayList<Question> {
        val rtn = ArrayList<Question>()
        for (i in 1..howMany) {
            val lhs = whichTables.get(randomFunction(whichTables.size))
            val rhs = randomFunction(TWELVE - 1) + 1
            rtn.add(Question(lhs, rhs))
        }

        return rtn;
    }

    fun checkAnswer(question: Question, answers: ArrayList<String>?) : Boolean {

        if (answers == null) {
            return false
        }

        val realAnswer = question.lhs * question.rhs
        return (answers.any { it.contains(realAnswer.toString()) })
    }
}