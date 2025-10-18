package cl.duoc.tengohambre.ui

/*
import android.graphics.Bitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.ImageBitmap
import kotlin.math.absoluteValue

// Función muy simple que genera un QR manual estilo tablero (no real QR, solo simula)

fun generarQrSimple(codigo: String, tamano: Int = 200): ImageBitmap {
    val bitmap = Bitmap.createBitmap(tamano, tamano, Bitmap.Config.ARGB_8888)
    val hash = codigo.hashCode().absoluteValue

    for (x in 0 until tamano) {
        for (y in 0 until tamano) {
            // Patrón tipo tablero, visible, cambia según el código
            val color = if (((x / 10 + y / 10 + hash % 10) % 2) == 0)
                android.graphics.Color.BLACK
            else
                android.graphics.Color.WHITE
            bitmap.setPixel(x, y, color)
        }
    }

    return bitmap.asImageBitmap()
}
*/

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlin.random.Random
import kotlin.math.absoluteValue

/**
 * Genera un "pseudo-QR" sin librerías:
 * - 3 patrones de búsqueda en las esquinas (parecido a un QR real)
 * - resto del área con puntos aleatorios controlados por el código
 * - NO es escaneable: es solo representación visual simple
 */
fun generarQrSimple(codigo: String, sizePx: Int = 200): ImageBitmap {
    val grid = 33                    // tamaño de la matriz (módulos) ~ QR chico
    val moduleSize = sizePx / grid   // píxeles por módulo
    val bmp = Bitmap.createBitmap(grid * moduleSize, grid * moduleSize, Bitmap.Config.ARGB_8888)

    // 1) Matriz booleana
    val m = Array(grid) { BooleanArray(grid) { false } }

    // 2) Dibujar "finder patterns" (los 3 ojos) 7x7 con anillos
    fun drawFinder(cx: Int, cy: Int) {
        for (y in -3..3) {
            for (x in -3..3) {
                val ax = cx + x
                val ay = cy + y
                if (ax !in 0 until grid || ay !in 0 until grid) continue
                val d = maxOf(kotlin.math.abs(x), kotlin.math.abs(y))
                // anillos negros-blancos-negros (3,2,1)
                m[ay][ax] = when (d) {
                    0, 1, 3 -> true   // negro
                    else -> false     // blanco
                }
            }
        }
    }
    drawFinder(3, 3)                   // arriba-izquierda
    drawFinder(grid - 4, 3)            // arriba-derecha
    drawFinder(3, grid - 4)            // abajo-izquierda

    // 3) Zona "aleatoria" determinista (semilla del cupón) con densidad ajustable
    val seed = codigo.hashCode().absoluteValue
    val rnd = Random(seed)
    val density = 0.33f                // 33% de módulos negros aprox. (ajusta 0.25..0.40)

    for (y in 0 until grid) {
        for (x in 0 until grid) {
            // Saltar donde hay finder patterns
            val inTopLeft     = x in 0..6 && y in 0..6
            val inTopRight    = x in (grid-7) until grid && y in 0..6
            val inBottomLeft  = x in 0..6 && y in (grid-7) until grid
            if (inTopLeft || inTopRight || inBottomLeft) continue

            // Bordes de "timing" (líneas alternadas) para que se vea más QR
            if (y == 6 || x == 6) {
                m[y][x] = ((x + y) % 2 == 0)
                continue
            }

            // Relleno pseudo-aleatorio estable (no ajedrez)
            m[y][x] = rnd.nextFloat() < density
        }
    }

    // 4) Pintar al bitmap escalando cada módulo a moduleSize px
    for (y in 0 until grid) {
        for (x in 0 until grid) {
            val color = if (m[y][x]) Color.BLACK else Color.WHITE
            val startX = x * moduleSize
            val startY = y * moduleSize
            for (py in 0 until moduleSize) {
                for (px in 0 until moduleSize) {
                    bmp.setPixel(startX + px, startY + py, color)
                }
            }
        }
    }

    return bmp.asImageBitmap()
}