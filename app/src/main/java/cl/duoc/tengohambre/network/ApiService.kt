package cl.duoc.tengohambre.network

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("coupon")
    fun obtenerCupones(): Call<List<ApiCupon>>
}
