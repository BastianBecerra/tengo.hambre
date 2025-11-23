package cl.duoc.tengohambre.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import cl.duoc.tengohambre.data.CuponStorage
import cl.duoc.tengohambre.model.Cupon
import java.util.UUID

class CuponViewModel : ViewModel() {

    val listaCupones = mutableStateListOf<Cupon>()

    private lateinit var storage: CuponStorage
    private var emailUsuario: String = ""

    fun iniciar(context: Context, email: String) {
        storage = CuponStorage(context)
        emailUsuario = email

        val codigos = storage.cargarCupones(email)

        listaCupones.clear()
        listaCupones.addAll(
            codigos.map { codigo ->
                Cupon(
                    id = UUID.randomUUID().toString(),
                    codigo = codigo,
                    usado = false,
                    fecha = System.currentTimeMillis()
                )
            }
        )
    }

    fun generarCupon() {
        val codigoAleatorio = "CUPON ${(10000..99999).random()}"
        val nuevoCupon = Cupon(
            id = UUID.randomUUID().toString(),
            codigo = codigoAleatorio,
            usado = false,
            fecha = System.currentTimeMillis()
        )

        listaCupones.add(nuevoCupon)
        storage.guardarCupones(emailUsuario, listaCupones.map { it.codigo })
    }

    fun marcarComoUsado(cupon: Cupon) {
        val indice = listaCupones.indexOf(cupon)
        if (indice != -1) {
            listaCupones[indice] = cupon.copy(usado = true)

            storage.guardarCupones(emailUsuario, listaCupones.map { it.codigo })
        }
    }

    fun eliminarCuponesUsados() {
        listaCupones.removeAll { it.usado }

        storage.guardarCupones(emailUsuario, listaCupones.map { it.codigo })
    }
}
