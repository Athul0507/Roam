package com.example.roam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ItineraryActivity : AppCompatActivity() {

    private val itineraryAPI = ItineraryAPI.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itenerary)


        val itineraryRecyclerView = findViewById<RecyclerView>(R.id.itineraryRecyclerView)
        val loadingAnimationView = findViewById<LottieAnimationView>(R.id.loadingAnimationView)

        val layoutManager = LinearLayoutManager(this)
        itineraryRecyclerView.layoutManager = layoutManager

        val swipedRightPlaces = intent.getSerializableExtra("swipedRightPlaces") as String?
       /* val swipedPlacesTextView = findViewById<TextView>(R.id.textView7)


        if (swipedRightPlaces != null) {
            swipedPlacesTextView.text = "Swiped Right Places:\n$swipedRightPlaces"
        }*/

        val requestBody = swipedRightPlaces?.let { ItineraryRequestData(it, 2) }
        if (requestBody != null) {
            loadingAnimationView.visibility = View.VISIBLE
            itineraryAPI.postData(requestBody).enqueue(object : Callback<ItineraryResponse> {
                override fun onResponse(
                    call: Call<ItineraryResponse>,
                    response: Response<ItineraryResponse>
                ) {
                    loadingAnimationView.visibility = View.GONE
                    if (response.isSuccessful) {
                        val responseData = response.body()
                        val itineraryText = responseData?.let{buildItineraryText(it)}

                        if(responseData!=null){
                            showItinerary(responseData)
                        }


                        //swipedPlacesTextView.append("\n\nAPI Response:\n$itineraryText")
                    } else {
                        // Handle error response
                        val errorBody = response.errorBody()?.string()
                        Log.e("API Response Error", errorBody ?: "Unknown error")
                    }
                }

                override fun onFailure(call: Call<ItineraryResponse>, t: Throwable) {
                    // Handle network failure
                    loadingAnimationView.visibility = View.GONE
                    Log.e("API Request", "Failed", t)
                }
            })
        }
    }

   private fun showItinerary(itineraryResponse: ItineraryResponse){
       val itineraryRecyclerView = findViewById<RecyclerView>(R.id.itineraryRecyclerView)
       val adapter = ItineraryAdapter(itineraryResponse.itinerary)
       itineraryRecyclerView.adapter = adapter
   }

    private fun buildItineraryText(itinerary: ItineraryResponse): String {
        val stringBuilder = StringBuilder()
        Log.d("Itinerary", itinerary.toString())
        for (itineraryDay in itinerary.itinerary) {
            Log.d("ItineraryDayList", itineraryDay.toString())
            stringBuilder.append("\n\n day: \n")
            for (activity in itineraryDay.activities) {
                Log.d("ItineraryDay", activity.toString())
                val place = activity.place
                val start = activity.start
                val end = activity.end
                stringBuilder.append("$place:\n")
                stringBuilder.append("$start - $end\n")
            }
        }

        return stringBuilder.toString()
    }
}