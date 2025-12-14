package cl.duoc.tengohambre.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import cl.duoc.tengohambre.data.CuponStorage
import cl.duoc.tengohambre.model.Cupon
import java.util.UUID

open class CuponViewModel : ViewModel() {

    val listaCupones = mutableStateListOf<Cupon>()

    private lateinit var storage: CuponStorage
    private var emailUsuario: String = ""
    private val marcasDisponibles = listOf("McDonalds", "Wendy", "BurgerKing", "KFC", "PapaJohns", "DominoPizza")
    private fun obtenerOrigenAleatorio(): String {
        return marcasDisponibles.random()
    }


    internal fun setStorage(storage: CuponStorage) {
        this.storage = storage
    }

    open fun iniciar(context: Context, email: String) {
        storage = CuponStorage(context)
        emailUsuario = email
        val cuponesCargados = storage.cargarCupones(email)
        listaCupones.clear()
        listaCupones.addAll(cuponesCargados)
    }

    open fun generarCupon() {
        val codigoAleatorio = "CUPON ${(10000..99999).random()}"
        val nuevoCupon = Cupon(
            id = UUID.randomUUID().toString(),
            codigo = codigoAleatorio,
            usado = false,
            fecha = System.currentTimeMillis(),
            origen = obtenerOrigenAleatorio()
        )
        listaCupones.add(nuevoCupon)
        storage.guardarCupones(emailUsuario, listaCupones)
    }

    open fun marcarComoUsado(cupon: Cupon) {
        val indice = listaCupones.indexOf(cupon)
        if (indice != -1) {
            listaCupones[indice] = cupon.copy(usado = true)
            storage.guardarCupones(emailUsuario, listaCupones)
        }
    }

    open fun eliminarCuponesUsados() {
        listaCupones.removeAll { it.usado }
        storage.guardarCupones(emailUsuario, listaCupones)
    }
}