package cl.duoc.tengohambre

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import cl.duoc.tengohambre.model.Cupon
import cl.duoc.tengohambre.ui.PantallaInicio
import cl.duoc.tengohambre.ui.theme.TengoHambreTheme
import cl.duoc.tengohambre.viewmodel.CuponViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    private val viewModel: CuponViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TengoHambreTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var pantalla by remember { mutableStateOf("bienvenida") }

                    when (pantalla) {
                        "bienvenida" -> PantallaInicio(onComenzar = { pantalla = "inicio" })
                        "inicio" -> PantallaCupones(
                            viewModel = viewModel,
                            onVerUsados = { pantalla = "usados" }
                        )
                        "usados" -> PantallaCuponesUsados(
                            viewModel = viewModel,
                            onVolver = { pantalla = "inicio" }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PantallaCupones(
    viewModel: CuponViewModel,
    onVerUsados: () -> Unit
) {
    val todosLosCupones = viewModel.listaCupones
    val cuponesActivos = todosLosCupones.filter { !it.usado }
    val hayUsados = todosLosCupones.any { it.usado }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Tengo Hambre - Cupones", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { viewModel.generarCupon() }) {
                Text("Generar cupón")
            }

            OutlinedButton(
                onClick = onVerUsados,
                enabled = hayUsados
            ) {
                Text("Ver cupones usados")
            }
        }

        Spacer(Modifier.height(16.dp))

        if (cuponesActivos.isEmpty()) {
            Text("No tienes cupones activos.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cuponesActivos) { cupon ->
                    TarjetaCupon(cupon) { seleccionado ->
                        viewModel.marcarComoUsado(seleccionado)
                    }
                }
            }
        }
    }
}

@Composable
fun PantallaCuponesUsados(
    viewModel: CuponViewModel,
    onVolver: () -> Unit
) {
    val usados = viewModel.listaCupones.filter { it.usado }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Mis Cupones Usados", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        Button(onClick = onVolver) {
            Text("Volver")
        }

        Spacer(Modifier.height(16.dp))

        if (usados.isEmpty()) {
            Text("No hay cupones usados todavía.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(usados) { cupon ->
                    TarjetaCupon(cupon, onMarcarUsado = {})
                }
            }
        }
    }
}

@Composable
fun TarjetaCupon(cupon: Cupon, onMarcarUsado: (Cupon) -> Unit) {
    val fondo = if (cupon.usado)
        MaterialTheme.colorScheme.surfaceVariant
    else
        MaterialTheme.colorScheme.primaryContainer

    val enlaceCupon = "https://tengohambre.cl/cupon?id=${cupon.codigo}"
    val enlaceCodificado = URLEncoder.encode(enlaceCupon, StandardCharsets.UTF_8.toString())
    val urlQr = "https://api.qrserver.com/v1/create-qr-code/?size=220x220&data=$enlaceCodificado"

    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { if (!cupon.usado) onMarcarUsado(cupon) },
        colors = CardDefaults.cardColors(containerColor = fondo)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(cupon.codigo, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(8.dp))

            AndroidView(
                factory = { context ->
                    android.webkit.WebView(context).apply {
                        settings.javaScriptEnabled = false
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        loadUrl(urlQr)
                    }
                },
                update = { it.loadUrl(urlQr) },
                modifier = Modifier.size(170.dp)
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = if (cupon.usado) "Cupón usado" else "Cupón activo",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = enlaceCupon,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
                        intent.data = android.net.Uri.parse(enlaceCupon)
                        context.startActivity(intent)
                    }
            )
        }
    }
}