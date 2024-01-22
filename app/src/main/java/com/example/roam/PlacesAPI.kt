import com.example.roam.Place
import com.example.roam.PlacesResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesAPI {
    @GET("place/textsearch/json")
    fun getPlaces(
        @Query("query") query: String,
        @Query("language") language: String,
        @Query("key") apiKey: String
    ): Call<PlacesResponse>

    companion object {
        private const val BASE_URL = "https://maps.googleapis.com/maps/api/"

        fun create(): PlacesAPI {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PlacesAPI::class.java)
        }
    }
}
