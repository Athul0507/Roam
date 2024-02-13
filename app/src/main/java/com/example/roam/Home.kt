package com.example.roam

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import com.ncorti.slidetoact.SlideToActView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var firebaseAuth:FirebaseAuth
    private var location: String? = null
    private var duration: String?=null
    private lateinit var durationText : EditText

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

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)
        durationText = view.findViewById(R.id.durations)

        when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_NO -> {
                durationText.setBackgroundResource(R.drawable.text_input_background)
            }
            AppCompatDelegate.MODE_NIGHT_YES -> {
                durationText.setBackgroundResource(R.drawable.text_input_background_dark)
            }
            else -> {
                durationText.setBackgroundResource(R.drawable.text_input_background)
            }
        }

        val date = view.findViewById<TextView>(R.id.date)
        date.text = formattedDate

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        val greetingText = view.findViewById<TextView>(R.id.greeting)
        greetingText.text = if (currentUser != null) "Welcome, ${currentUser.displayName}" else "Welcome"


        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        val autocompleteEditText = autocompleteFragment?.view?.findViewById<EditText>(resources.getIdentifier("places_autocomplete_search_input", "id",
            context?.packageName ?: null
        ))

// Set custom hint and icon
        autocompleteEditText?.hint = "Enter destination!"
        autocompleteEditText?.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        autocompleteEditText?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        autocompleteEditText?.background = ContextCompat.getDrawable(requireContext(), R.drawable.text_input_background)
        autocompleteEditText?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)


        val cardView = view.findViewById<CardView>(R.id.card_view)
        cardView.setBackgroundResource(R.drawable.text_input_background)
        for (i in 0 until cardView.childCount) {
            val childView = cardView.getChildAt(i)
            if (childView is ImageView) {
                childView.visibility = View.GONE
                break
            }
        }
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))


        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                location = place.name

            }

            override fun onError(status: Status) {
                Log.i(TAG, "An error occurred: $status")
            }
        })


        val slideToActView = view.findViewById<SlideToActView>(R.id.slide)

        slideToActView.onSlideCompleteListener = object: SlideToActView.OnSlideCompleteListener{
            override fun onSlideComplete(view: SlideToActView) {


                duration = durationText.text.toString()

                if(location.isNullOrEmpty() && duration.isNullOrEmpty()) {
                    slideToActView.setCompleted(false, true)
                    ToastUtils.showToast(requireContext(), "Place and duration cant be empty", 1)
                }

                else{
                    val intent = Intent(activity, SwipeActivity::class.java)
                    intent.putExtra("place", location)
                    intent.putExtra("duration", duration)
                    startActivity(intent)
                    slideToActView.setCompleted(false, true)
                }
            }
        }


        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

