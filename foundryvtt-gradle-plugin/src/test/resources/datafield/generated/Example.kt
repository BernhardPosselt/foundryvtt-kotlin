package at.posselt.example

import js.objects.ReadonlyRecord
import kotlin.Array
import kotlin.Double
import kotlin.Int
import kotlin.String

public interface Example {
    public val id: String

    public val obj: ReadonlyRecord<String, Int>?

    public val blubb: Int?

    public val anEnum: String?

    public val blubb2: Double?

    public val sap: Array<Int>?

    public val aref: Refe?
}
