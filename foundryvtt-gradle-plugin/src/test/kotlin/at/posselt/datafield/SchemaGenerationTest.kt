package at.posselt.datafield

import java.io.BufferedReader
import java.io.InputStream
import java.nio.charset.Charset
import kotlin.test.Test
import kotlin.test.assertEquals

class SchemaGenerationTest {

    @Test
    fun `parses schema`() {
        val result = parseSchema(getResource("/datafield/data.json"))

        assertEquals("at.posselt.example.Example", result.id)
        assertEquals("https://json-schema.org/draft/2020-12/schema", result.schema)
    }

    @Test
    fun `generates code`() {
        val schema = parseSchema(getResource("/datafield/data.json"))
        val result = generateCode(schema)
        val builder = StringBuilder()
        result.writeTo(builder)
        assertEquals(getResourceString("/datafield/generated/Example.kt"), builder.toString())
    }

    private fun getResourceString(path: String): String =
        getResource(path).bufferedReader(Charsets.UTF_8)
            .use(BufferedReader::readAllAsString)

    private fun getResource(path: String): InputStream =
        this.javaClass.getResourceAsStream(path)!!
}