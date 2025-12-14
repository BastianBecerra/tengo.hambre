package cl.duoc.tengohambre.data

import android.content.Context
import cl.duoc.tengohambre.model.Cupon
import org.json.JSONArray
import org.json.JSONObject

open class CuponStorage(private val context: Context) {

    private fun fileName(email: String): String {
        return "cupones_${email}.json"
    }

    open fun guardarCupones(email: String, listaCupones: List<Cupon>) {
        val jsonArray = JSONArray()

        listaCupones.forEach { cupon ->
            val jsonObject = JSONObject()
            jsonObject.put("id", cupon.id)
            jsonObject.put("codigo", cupon.codigo)
            jsonObject.put("usado", cupon.usado)
            jsonObject.put("fecha", cupon.fecha)
            jsonObject.put("origen", cupon.origen)

            jsonArray.put(jsonObject)
        }

        context.openFileOutput(fileName(email), Context.MODE_PRIVATE)
            .use { it.write(jsonArray.toString().toByteArray()) }
    }

    open fun cargarCupones(email: String): MutableList<Cupon> {
        val file = context.getFileStreamPath(fileName(email))
        if (!file.exists()) return mutableListOf()

        val contenido = context.openFileInput(fileName(email))
            .use { String(it.readBytes()) }

        val jsonArray = JSONArray(contenido)
        val lista = mutableListOf<Cupon>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val cupon = Cupon(
                id = jsonObject.getString("id"),
                codigo = jsonObject.getString("codigo"),
                usado = jsonObject.getBoolean("usado"),
                fecha = jsonObject.getLong("fecha"),
                origen = jsonObject.getString("origen")
            )
            lista.add(cupon)
        }

        return lista
    }
}