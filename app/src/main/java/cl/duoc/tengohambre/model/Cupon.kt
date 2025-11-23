package cl.duoc.tengohambre.model

data class Cupon(
    val id: String,
    val codigo: String,
    val usado: Boolean = false,
    val fecha: Long = System.currentTimeMillis()
)