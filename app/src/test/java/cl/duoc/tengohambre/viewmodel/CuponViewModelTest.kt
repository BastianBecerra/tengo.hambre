package cl.duoc.tengohambre.viewmodel

import android.content.Context
import cl.duoc.tengohambre.data.CuponStorage
import cl.duoc.tengohambre.model.Cupon
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import androidx.compose.runtime.mutableStateListOf

class TestCuponStorage(context: Context) : CuponStorage(context) {
    private val cuponesEnMemoria = mutableStateListOf<Cupon>()

    override fun guardarCupones(email: String, listaCupones: List<Cupon>) {
        cuponesEnMemoria.clear()
        cuponesEnMemoria.addAll(listaCupones)
    }

    override fun cargarCupones(email: String): MutableList<Cupon> {
        // En este test, nunca vamos a cargar nada. Devolvemos la memoria simulada.
        return cuponesEnMemoria.toMutableList()
    }
}

class CuponViewModelTest {

    private lateinit var viewModel: CuponViewModel
    private lateinit var mockContext: Context

    @Before
    fun setup() {
        mockContext = mock(Context::class.java)
        viewModel = CuponViewModel()

        val testStorage = TestCuponStorage(mockContext)

        viewModel.setStorage(testStorage)
        viewModel.listaCupones.clear()
    }

    @Test
    fun generarCupon_test() {
        val tamanoInicial = viewModel.listaCupones.size
        val marcasValidas = listOf("McDonalds", "Wendy", "BurgerKing", "KFC", "PapaJohns", "DominoPizza")

        viewModel.generarCupon()
        viewModel.generarCupon()

        assertEquals("La lista de cupones debería haber aumentado en 2", tamanoInicial + 2, viewModel.listaCupones.size)

        val ultimoCupon = viewModel.listaCupones.last()
        assertTrue("El origen del cupón debería ser uno de los válidos", marcasValidas.contains(ultimoCupon.origen))
    }
}