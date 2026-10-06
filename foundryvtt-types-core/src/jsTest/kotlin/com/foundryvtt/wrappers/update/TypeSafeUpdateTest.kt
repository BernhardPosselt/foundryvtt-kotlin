package com.foundryvtt.wrappers.update

import com.foundryvtt.wrappers.RecordProperty
import js.objects.Record
import js.objects.recordOf
import kotlin.test.Test
import kotlin.test.assertEquals

class TypeSafeUpdateTest {

    @TypeSafeUpdateDsl
    class Nested(val path: String) {
        val inner = ScalarProperty<Int>("inner")
    }

    @TypeSafeUpdateDsl
    class Props {
        val test = ScalarProperty<String>("test")
        val abc = ScalarProperty<String>("abc")
        val nested = RecordProperty("nested", Nested("nested"))
        val obj = RecordProperty("obj", recordOf<String, Any?>())
        val obj2 = RecordProperty("obj2", recordOf<String, Any?>())
        val obj3 = RecordProperty("obj3", Nested("nested2"))
    }

    @Test
    fun createsUpdate() {
        val result = buildUpdate(Props(), deleteObject = 42) {
            test.set("value")
            nested {
                inner.set(3)
            }
            -abc
            obj["test2"] = "something"
            obj2.set(recordOf("record" to "val"))
            obj3.deleteEntry("nested2")
        }
        assertEquals(
            JSON.stringify(
                recordOf<String, Any?>(
                    "test" to "value",
                    "nested.inner" to 3,
                    "abc" to 42,
                    "obj.test2" to "something",
                    "obj2" to recordOf("record" to "val"),
                    "obj3.nested2" to 42,
                ),
            ), JSON.stringify(result)
        )
    }

}