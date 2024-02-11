package com.example.roam

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Explore : Fragment(), LocationAdapter.OnSaveClickListener {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: LocationAdapter

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
        val view = inflater.inflate(R.layout.fragment_explore, container, false)
        viewPager = view.findViewById<ViewPager2>(R.id.exploreViewPager)
        getLocationsFromFirebase { locations ->
            adapter = LocationAdapter(locations, this)
            viewPager.adapter = adapter
        }

        return view
    }


    override fun onSaveClicked(location: ExploreLocation) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val userLocationsRef = db.collection("userLocations").document(userId)


            userLocationsRef.collection("savedLocations").add(location)
                .addOnSuccessListener { documentReference ->
                    Log.d("Firestore", "Location added with ID: ${documentReference.id}")
                    Toast.makeText(requireContext(), "Location saved successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error adding location", e)
                    Toast.makeText(requireContext(), "Failed to save location", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
    private fun getLocationsFromFirebase(callback: (List<ExploreLocation>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("places")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("firebase Error", "Error listening for documents: ", error)
                    callback(emptyList())
                    return@addSnapshotListener
                }

                val locations = mutableListOf<ExploreLocation>()
                if (snapshot != null) {
                    for (document in snapshot.documents) {
                        val name = document.getString("name")
                        val imageUrl = document.getString("imageUrl")
                        val description = document.getString("description")

                        val location = ExploreLocation(name ?: "", imageUrl ?: "", description ?: "")
                        locations.add(location)
                    }
                }
                callback(locations)
            }
    }



    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Explore().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}