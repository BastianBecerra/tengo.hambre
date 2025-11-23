package cl.duoc.tengohambre.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class UserViewModel: ViewModel(){

    var nombreUsuario = mutableStateOf("Invitado")
        private set

    fun setNombre(nombre: String){
        nombreUsuario.value = nombre
    }

    fun limpiarUsuario(){
        nombreUsuario.value = "invitado"
    }
}