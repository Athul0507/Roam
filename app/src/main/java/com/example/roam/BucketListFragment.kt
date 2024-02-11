package com.example.roam

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BucketListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BucketListFragment : Fragment() {
    // TODO: Rename and change types of parameters
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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bucket_list, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewBucketList)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        getSavedLocationsFromFirebase { locations ->
            val adapter = BucketListAdapter(locations)
            recyclerView.adapter = adapter
        }

        return view
    }

    fun getSavedLocationsFromFirebase(callback: (List<ExploreLocation>) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val userLocationsRef = db.collection("userLocations").document(userId)

            userLocationsRef.collection("savedLocations")
                .get()
                .addOnSuccessListener { documents ->
                    val locations = mutableListOf<ExploreLocation>()
                    for (document in documents) {
                        val name = document.getString("name")
                        val imageUrl = document.getString("imageUrl")
                        val description = document.getString("description")
                        val location = ExploreLocation(name ?: "", imageUrl ?: "", description ?: "")
                        locations.add(location)
                    }
                    callback(locations)
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error getting saved locations", exception)
                    callback(emptyList())
                }
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BucketListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BucketListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}