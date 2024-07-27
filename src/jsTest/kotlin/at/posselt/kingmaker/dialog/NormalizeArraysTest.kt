package at.posselt.kingmaker.dialog

import js.objects.recordOf
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class NormalizeArraysTest {

    @Test
    fun testFirstLevelArray() {
        val test = recordOf(0 to "hi", 1 to "lo")
        val result = normalizeArrays(test)

        assertContentEquals(arrayOf("hi", "lo"), result)
    }

    @Test
    fun nestedArray() {
        val test = recordOf(
            "test" to "hi", "nested" to recordOf(
                0 to "lo",
                1 to "hi"
            )
        )
        val result = normalizeArrays(test)

        val expected = recordOf("test" to "hi", "nested" to arrayOf("lo", "hi"))
        assertEquals(JSON.stringify(expected), JSON.stringify(result))
    }
}