package layout.com.example.dispositivosmoviles

import retrofit2.Call
import com.example.dispositivosmoviles.Product
import retrofit2.http.GET

interface ApiInterface {
    @GET(":code")
    fun getProduct(): Call<Product>
}