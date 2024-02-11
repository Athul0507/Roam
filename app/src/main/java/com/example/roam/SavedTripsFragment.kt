package com.example.roam

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.gson.Gson

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SavedTripsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_saved_trips, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.savedTripsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val emptyTrips = view.findViewById<TextView>(R.id.emptySavedTrips)

        loadTripNames { trips ->
            if(!(trips.isEmpty())){
                emptyTrips.visibility = View.VISIBLE
            }
            val adapter = TripNamesAdapter(trips) { itineraryResponse ->
                val gson = Gson()
                val itineraryResponseString = gson.toJson(itineraryResponse)
                val intent = Intent(requireContext(), ItineraryActivity::class.java).apply {
                    putExtra("itineraryResponse", itineraryResponseString)
                }
                startActivity(intent)
            }
            recyclerView.adapter = adapter
        }
        return view;
    }

    private fun loadTripNames(callback: (List<SavedTrips>)->Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        if (userId != null) {
            val savedTripsRef = db.collection("userItineraries").document(userId)


            savedTripsRef.collection("savedItineraries").get()
                .addOnSuccessListener { documents ->
                    val tripNames = mutableListOf<SavedTrips>()
                    for (document in documents) {
                        val tripName = document.getString("name")
                        val tripItinerary = document.get("itinerary")as?Map<*, *>
                        val itineraryResponse = parseItineraryResponse(tripItinerary)
                        val trip = SavedTrips(tripName?: "", itineraryResponse);
                        tripNames.add(trip)
                    }
                        callback(tripNames)
                    }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Error fetching trip names: ${exception.message}", Toast.LENGTH_SHORT).show()
                    callback(emptyList())
                }
        }
    }

    private fun parseItineraryResponse(itineraryMap: Map<*, *>?): ItineraryResponse {
        if (itineraryMap == null) {
            return ItineraryResponse(false, emptyList())
        }

        val itinerary = itineraryMap["itinerary"] as? List<*> ?: emptyList<ItineraryDay>()
        val itineraryDays = itinerary.mapNotNull {itineraryDayMap -> parseItineraryDay(itineraryDayMap as? Map<*, *>) }
        return ItineraryResponse(true, itineraryDays)
    }

    // Function to parse a Map into an ItineraryDay object
    private fun parseItineraryDay(itineraryDayMap: Map<*, *>?): ItineraryDay? {
        if (itineraryDayMap == null) {
            return null
        }

        val activities = itineraryDayMap["activities"] as? List<*> ?: emptyList<Activity>()
        val activityList = activities.mapNotNull { activityMap -> parseActivity(activityMap as? Map<*, *> ) }
        return ItineraryDay(activityList)
    }

    // Function to parse a Map into an Activity object
    private fun parseActivity(activityMap: Map<*, *>?): Activity? {
        if (activityMap == null) {
            return null
        }

        val place = activityMap["place"] as? String ?: return null
        val start = activityMap["start"] as? String ?: return null
        val end = activityMap["end"] as? String ?: return null
        return Activity(place, start, end)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SavedTripsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
