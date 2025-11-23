package cl.duoc.tengohambre.data

import android.content.Context

class AppPreferences(context: Context) {

    private val prefs = context.getSharedPreferences("tengo_hambre_prefs", Context.MODE_PRIVATE)

    fun guardarUsuario(nombre: String, email: String) {
        prefs.edit()
            .putString("nombre", nombre)
            .putString("email", email)
            .putBoolean("logeado", true)
            .apply()
    }

    fun cerrarSesion() {
        prefs.edit()
            .putBoolean("logeado", false)
            .apply()
    }

    fun estaLogeado(): Boolean {
        return prefs.getBoolean("logeado", false)
    }

    fun obtenerNombre(): String {
        return prefs.getString("nombre", "Invitado") ?: "Invitado"
    }

    fun obtenerEmail(): String? {
        return prefs.getString("email", null)
    }
}
