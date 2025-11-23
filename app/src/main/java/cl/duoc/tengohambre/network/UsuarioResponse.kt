package cl.duoc.tengohambre.network

data class UsuarioItem(
    val id: Int,
    val name: String,
    val email: String
)

data class UsuarioResponse(
    val user: UsuarioItem?
)
