package com.example.roam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ItineraryActivity : AppCompatActivity() {

    private val itineraryAPI = ItineraryAPI.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itenerary)
        val swipedPlacesTextView = findViewById<TextView>(R.id.textView7)

        // Retrieve the swipedRightPlaces list from the intent
        val swipedRightPlaces = intent.getSerializableExtra("swipedRightPlaces") as String?

        // Check if the list is not null and display the places in the TextView
        if (swipedRightPlaces != null) {
            swipedPlacesTextView.text = "Swiped Right Places:\n$swipedRightPlaces"
        }

        val requestBody = swipedRightPlaces?.let { ItineraryRequestData(it, 2) }
        if (requestBody != null) {
            itineraryAPI.postData(requestBody).enqueue(object : Callback<ItineraryResponse> {
                override fun onResponse(
                    call: Call<ItineraryResponse>,
                    response: Response<ItineraryResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseData = response.body()
                        val itineraryText = responseData?.let{buildItineraryText(it)}


                        swipedPlacesTextView.append("\n\nAPI Response:\n$itineraryText")
                    } else {
                        // Handle error response
                        val errorBody = response.errorBody()?.string()
                        Log.e("API Response Error", errorBody ?: "Unknown error")
                    }
                }

                override fun onFailure(call: Call<ItineraryResponse>, t: Throwable) {
                    // Handle network failure
                    Log.e("API Request", "Failed", t)
                }
            })
        }
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