package cl.duoc.tengohambre.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.tengohambre.network.ApiCupon
import cl.duoc.tengohambre.data.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun XanoPantallaCupones(onVolver: () -> Unit) {

    var cupones by remember { mutableStateOf<List<ApiCupon>>(emptyList()) }
    var error by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        RetrofitClient.instance.obtenerCupones()
            .enqueue(object : Callback<List<ApiCupon>> {
                override fun onResponse(
                    call: Call<List<ApiCupon>>,
                    response: Response<List<ApiCupon>>
                ) {
                    if (response.isSuccessful) {
                        cupones = response.body() ?: emptyList()
                    } else {
                        error = "Error en la respuesta del servidor"
                    }
                }

                override fun onFailure(call: Call<List<ApiCupon>>, t: Throwable) {
                    error = "Error: ${t.message}"
                }
            })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Button(onClick = onVolver) {
            Text("Volver")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (error.isNotEmpty()) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(cupones) { c ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("ID: ${c.id}")
                        Text("Código: ${c.code}")
                        Text("Usado: ${c.used}")
                    }
                }
            }
        }
    }
}
