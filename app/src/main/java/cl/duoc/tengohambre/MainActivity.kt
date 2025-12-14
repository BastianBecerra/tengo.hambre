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
import cl.duoc.tengohambre.data.AppPreferences
import cl.duoc.tengohambre.model.Cupon
import cl.duoc.tengohambre.ui.PantallaInicio
import cl.duoc.tengohambre.ui.LoginScreen
import cl.duoc.tengohambre.ui.RegisterScreen
import cl.duoc.tengohambre.ui.XanoPantallaCupones
import cl.duoc.tengohambre.ui.theme.TengoHambreTheme
import cl.duoc.tengohambre.viewmodel.CuponViewModel
import cl.duoc.tengohambre.viewmodel.UserViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource

class MainActivity : ComponentActivity() {

    private val viewModel: CuponViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            val context = LocalContext.current
            val prefs = remember { AppPreferences(context) }

            var pantalla by remember {
                mutableStateOf(
                    if (prefs.estaLogeado()) "inicio" else "bienvenida"
                )
            }


            if (prefs.estaLogeado()) {
                userViewModel.setNombre(prefs.obtenerNombre())
                viewModel.iniciar(context, prefs.obtenerEmail() ?: "")
            }

            TengoHambreTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    when (pantalla) {

                        "bienvenida" -> PantallaInicio(
                            nombreUsuario = prefs.obtenerNombre(),
                            onComenzar = { pantalla = "inicio" },
                            onGoToLogin = { pantalla = "login" },
                            onVerCuponesExternos = { pantalla = "cupones" } // ← agregado
                        )

                        "login" -> LoginScreen(
                            onLoginSuccess = { nombre, email ->
                                userViewModel.setNombre(nombre)
                                viewModel.iniciar(context, email)
                                pantalla = "inicio"
                            },
                            onGoToRegister = { pantalla = "register" },
                            onBack = { pantalla = "bienvenida" }
                        )

                        "register" -> RegisterScreen(
                            onRegisterSuccess = { nombre, email ->
                                userViewModel.setNombre(nombre)
                                viewModel.iniciar(context, email)
                                pantalla = "inicio"
                            },
                            onGoToLogin = { pantalla = "login" }
                        )

                        "inicio" -> PantallaCupones(
                            viewModel = viewModel,
                            onVerUsados = { pantalla = "usados" },
                            onVolver = { pantalla = "bienvenida" }
                        )

                        "usados" -> PantallaCuponesUsados(
                            viewModel = viewModel,
                            onVolver = { pantalla = "inicio" }
                        )

                        "cupones" -> XanoPantallaCupones(
                            onVolver = { pantalla = "bienvenida" }
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
    onVerUsados: () -> Unit,
    onVolver: () -> Unit
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

            Button(
                onClick = onVolver,
                modifier = Modifier.wrapContentWidth()
            ) {
                Text("Inicio")
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


@DrawableRes
fun obtenerLogoPorOrigen(origen: String): Int {
    return when (origen) {
        "McDonalds" -> R.drawable.logo_mcdonald
        "Wendy" -> R.drawable.logo_wendys
        "BurgerKing" -> R.drawable.logo_burger_king
        "DominoPizza" -> R.drawable.logo_domino_pizza
        "KFC" -> R.drawable.logo_kfc
        "PapaJohns" -> R.drawable.logo_papa_jhons
        else -> R.drawable.logo
    }
}

@Composable
fun TarjetaCupon(cupon: Cupon, onMarcarUsado: (Cupon) -> Unit) {
    val fondo = if (cupon.usado)
        MaterialTheme.colorScheme.surfaceVariant
    else
        MaterialTheme.colorScheme.primaryContainer

    val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    val fechaLegible = formatoFecha.format(Date(cupon.fecha))

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                val logoId = obtenerLogoPorOrigen(cupon.origen)
                Image(
                    painter = painterResource(id = logoId),
                    contentDescription = "Logo de ${cupon.origen}",
                    modifier = Modifier.size(50.dp) // Tamaño del logo
                )

                Text(
                    cupon.codigo,
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Text(
                text = "Creado: $fechaLegible",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

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