package cl.duoc.tengohambre.data

import android.content.Context
import org.json.JSONArray

class CuponStorage(private val context: Context) {

    private fun fileName(email: String): String {
        return "cupones_${email}.json"
    }

    fun guardarCupones(email: String, listaCupones: List<String>) {
        val json = JSONArray()
        listaCupones.forEach { json.put(it) }

        context.openFileOutput(fileName(email), Context.MODE_PRIVATE)
            .use { it.write(json.toString().toByteArray()) }
    }

    fun cargarCupones(email: String): MutableList<String> {
        val file = context.getFileStreamPath(fileName(email))
        if (!file.exists()) return mutableListOf()

        val contenido = context.openFileInput(fileName(email))
            .use { String(it.readBytes()) }

        val json = JSONArray(contenido)
        val lista = mutableListOf<String>()

        for (i in 0 until json.length()) {
            lista.add(json.getString(i))
        }

        return lista
    }
}
