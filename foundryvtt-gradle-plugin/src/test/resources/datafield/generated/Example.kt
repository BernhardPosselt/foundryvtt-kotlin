package at.posselt.example

import kotlin.Double
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map

public interface Example {
    public val id: String

    public val obj: Map<String, Int>?

    public val blubb: Int?

    public val blubb2: Double?

    public val sap: List<Int>?
}
