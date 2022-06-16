package br.com.corelib.data

data class Event(
    val people: List<String>? = null,
    val date: Long? = null,
    val description: String? = null,
    val image: String? = null,
    val location: Pair<Double, Double>? = null,
    val price: Double? = null,
    val title: String? = null,
    val id: String? = null
)