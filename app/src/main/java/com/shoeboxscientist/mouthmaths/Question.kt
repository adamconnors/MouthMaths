package com.shoeboxscientist.mouthmaths

data class Question(val lhs: Int, val rhs: Int) {
    fun getAnswer(): Int {
        return lhs * rhs
    }
}