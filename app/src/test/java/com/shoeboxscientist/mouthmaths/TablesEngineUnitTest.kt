package com.shoeboxscientist.mouthmaths

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TablesEngineUnitTest {
    @Test
    fun testAnswerChecking() {
        val engine = TablesEngine(30, arrayListOf(1))
        assertTrue(engine.checkAnswer(Question(3, 4),
                arrayListOf("kalsdjfl", "sdkjf12")))

        assertFalse(engine.checkAnswer(Question(3, 4),
                arrayListOf("kalsdjfl", "sdkjf")))

    }

    @Test
    fun testGenerateTables() {
        var counter = Counter(0)
        var engine = TablesEngine(3, arrayListOf(2,5), { counter.increment() % it })
        var tables = engine.generateTablesList()

        // Counter gets incremented twice per call... This is a rubbish test. TODO: Revisit
        assertTrue(tables.toArray() contentDeepEquals arrayOf(
                Question(2, 2), Question(2, 4),
                Question(2, 6)))

        counter = Counter(1)
        engine = TablesEngine(3, arrayListOf(2,5), { counter.increment() % it })
        tables = engine.generateTablesList()

        println(tables.toArray())
        assertTrue(tables.toArray() contentDeepEquals arrayOf(
                Question(5, 3), Question(5, 5),
                Question(5, 7)))
    }


    // Simple counter to replace the random function
    class Counter(val start: Int) {
        var current = start
        fun increment() : Int {
            return current++
        }
    }
}
