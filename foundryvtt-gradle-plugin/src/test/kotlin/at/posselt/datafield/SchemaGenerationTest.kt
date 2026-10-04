package at.posselt.datafield

import java.io.BufferedReader
import java.io.InputStream
import kotlin.test.Test
import kotlin.test.assertEquals

class SchemaGenerationTest {

    @Test
    fun `parses schema`() {
        val result = parseSchema(getResource("/datafield/interface.json"))

        assertEquals("at.posselt.example.Example", result.id)
        assertEquals("https://json-schema.org/draft/2020-12/schema", result.schema)
    }

    @Test
    fun `generates interface code`() {
        val schema = parseSchema(getResource("/datafield/interface.json"))
        val result = generateType(schema)
        val builder = StringBuilder()
        result.writeTo(builder)
        assertEquals(getResourceString("/datafield/generated/Example.kt"), builder.toString())
    }

    @Test
    fun `generates enum code`() {
        val schema = parseSchema(getResource("/datafield/enum.json"))
        val result = generateType(schema)
        val builder = StringBuilder()
        result.writeTo(builder)
        assertEquals(getResourceString("/datafield/generated/ExampleEnum.kt"), builder.toString())
    }

    private fun getResourceString(path: String): String =
        getResource(path).bufferedReader(Charsets.UTF_8)
            .use(BufferedReader::readAllAsString)

    private fun getResource(path: String): InputStream =
        this.javaClass.getResourceAsStream(path)!!
}