package at.posselt.kingmaker.app

import js.objects.Record
import js.objects.recordOf
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class NormalizeArraysTest {

    @Test
    fun testFirstLevelArray() {
        @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
        val test = recordOf(0 to "hi", 1 to "lo") as Record<String, String>
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