package cl.duoc.tengohambre.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.tengohambre.R
import cl.duoc.tengohambre.data.AppPreferences

@Composable
fun PantallaInicio(
    nombreUsuario: String,
    onComenzar: () -> Unit,
    onGoToLogin: () -> Unit,
    onVerCuponesExternos: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { AppPreferences(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo Tengo Hambre",
            modifier = Modifier
                .size(180.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Bienvenido, $nombreUsuario",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Tengo Hambre",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Genera y guarda tus cupones de comida de forma rápida y sencilla.",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        Button(onClick = onComenzar) {
            Text("Comenzar")
        }

        Spacer(Modifier.height(18.dp))

        Button(
            onClick = { onVerCuponesExternos() },
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Ver cupones de Xano")
        }

        Spacer(Modifier.height(18.dp))

        if (!prefs.estaLogeado()) {
            Text(
                text = "¿Ya tienes cuenta? Iniciar sesión",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable { onGoToLogin() }
            )
        } else {
            Button(
                onClick = {
                    prefs.cerrarSesion()
                    onGoToLogin()
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}
