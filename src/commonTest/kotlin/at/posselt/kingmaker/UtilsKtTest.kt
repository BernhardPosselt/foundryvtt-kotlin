package at.posselt.kingmaker

import kotlin.test.Test
import kotlin.test.assertEquals

class UtilsKtTest {
    @Test
    fun deCamelCase() {
        assertEquals("Test", "test".deCamelCase())
        assertEquals("Test It", "testIt".deCamelCase())
        assertEquals("", "".deCamelCase())
    }
}