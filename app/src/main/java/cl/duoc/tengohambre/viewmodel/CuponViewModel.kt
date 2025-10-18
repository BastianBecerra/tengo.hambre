package cl.duoc.tengohambre.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import cl.duoc.tengohambre.model.Cupon
import java.util.UUID

class CuponViewModel : ViewModel() {

    val listaCupones = mutableStateListOf<Cupon>()

    fun generarCupon() {
        val codigoAleatorio = "CUPON ${(10000..99999).random()}"
        val nuevoCupon = Cupon(
            id = UUID.randomUUID().toString(),
            codigo = codigoAleatorio
        )
        listaCupones.add(nuevoCupon)
    }

    fun marcarComoUsado(cupon: Cupon) {
        val indice = listaCupones.indexOf(cupon)
        if (indice != -1) {
            listaCupones[indice] = cupon.copy(usado = true)
        }
    }

    fun eliminarCuponesUsados() {
        listaCupones.removeAll { it.usado }
    }
}