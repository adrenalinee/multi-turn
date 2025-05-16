package malibu.multiturn

fun main() {
    val mm = mutableMapOf<String, String>()
    mm.put("A", "1")
    println(mm)

    val data = Data(mm)
    mm.put("B", "2")

    println(mm)
}

data class Data(
    val mm: Map<String, String>
)