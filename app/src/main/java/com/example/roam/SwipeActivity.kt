package com.example.roam

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.Duration
import com.yuyakaido.android.cardstackview.StackFrom
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting
import com.yuyakaido.android.cardstackview.SwipeableMethod
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SwipeActivity : AppCompatActivity(), CardStackListener {

    private val adapter = PlaceAdapter()
    private lateinit var layoutManager: CardStackLayoutManager
    private val placesAPI = PlacesAPI.create()
    private val photosAPI = PhotosAPI.create()
    private lateinit var stackView : CardStackView
    private var duration: String? = null
    private val swipedRightPlaces: MutableList<Place> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swipe)

        layoutManager = CardStackLayoutManager(this, this).apply {
            setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
            setOverlayInterpolator(LinearInterpolator())
            setStackFrom(StackFrom.Top) // Set the desired stack position
            setVisibleCount(3)
        }

        stackView = findViewById<CardStackView>(R.id.stack_view)
        stackView.layoutManager = layoutManager
        stackView.adapter = adapter
        stackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }

        val place = intent.getStringExtra("place")
        duration = intent.getStringExtra("duration")
        val call = placesAPI.getPlaces("$place points of interest", "en", "AIzaSyBglq1rNe7NAt6-Mrv4FxEhO1IaNP8iMrc")

        call.enqueue(object : Callback<PlacesResponse> {
            override fun onFailure(call: Call<PlacesResponse>, t: Throwable) {
                Toast.makeText(this@SwipeActivity, t.toString(), Toast.LENGTH_SHORT).show()
                Log.d("Error", t.toString())
            }

            override fun onResponse(
                call: Call<PlacesResponse>,
                response: Response<PlacesResponse>
            ) {
                response.body()?.let { placesResponse ->
                    val places = placesResponse.results
                    fetchPhotosForPlaces(places)
                    adapter.setProfiles(places)
                }
            }
        })

        val accept = findViewById<Button>(R.id.accept)
        accept.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            layoutManager.setSwipeAnimationSetting(setting)
            stackView.swipe()
        }

        val reject = findViewById<Button>(R.id.reject)
        reject.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            layoutManager.setSwipeAnimationSetting(setting)
            stackView.swipe()
        }
    }


    private fun fetchPhotosForPlaces(places: List<Place>) {
        places.forEach { place ->
            val firstPhotoReference = place.photos?.firstOrNull()?.photo_reference
            if (firstPhotoReference != null) {
                fetchPhoto(firstPhotoReference) { photoUrl ->
                    adapter.updateProfileWithPhoto(place, photoUrl)
                }
            }
        }
    }


    private fun fetchPhoto(photoReference: String, onPhotoFetched: (ByteArray?) -> Unit) {
        val photoCall = photosAPI.getPhoto("1280", "800", photoReference, "AIzaSyBglq1rNe7NAt6-Mrv4FxEhO1IaNP8iMrc")

        photoCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handle photo API call failure
                Log.e("Photo API Error", t.toString())
                onPhotoFetched(null)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val photoUrl = response.body()?.bytes()
                    onPhotoFetched(photoUrl)
                } else {
                    // Log the error response
                    Log.e(
                        "Photo URL Response",
                        "Error: ${response.code()} - ${response.message()}"
                    )
                    onPhotoFetched(null)
                }
            }
        })
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
        /*val borderColor = if (direction == Direction.Right) {
            "#00FF00" // Green for right swipe
        } else {
            "#FF0000" // Red for left swipe
        }
        // Get the background drawable and update the stroke color
        val borderGlowDrawable = stackView.background as GradientDrawable
        borderGlowDrawable.setStroke(5.dpToPx(this), Color.parseColor(borderColor))*/
    }

    override fun onCardSwiped(direction: Direction?) {
        if(direction == Direction.Right){
            val currentPlace = adapter.getProfileAt(layoutManager.topPosition-1)
            currentPlace?.let{
                swipedRightPlaces.add(it)}
        }

        if(layoutManager.topPosition == adapter.itemCount){
            val placesNames = swipedRightPlaces.joinToString(", ") { it.name }

            val intent = Intent(this, ItineraryActivity::class.java)

            intent.putExtra("swipedRightPlaces", placesNames)
            intent.putExtra("duration", duration)
            startActivity(intent)
            //Toast.makeText(this, "Swiped Right Places: $placesNames", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCardRewound() {

    }

    override fun onCardCanceled() {

    }

    override fun onCardAppeared(view: View?, position: Int) {

    }

    override fun onCardDisappeared(view: View?, position: Int) {

    }

    /* fun interpolateColor(startColor: Int, endColor: Int, ratio: Float): Int {
        val r = (Color.red(startColor) * (1 - ratio) + Color.red(endColor) * ratio).toInt()
        val g = (Color.green(startColor) * (1 - ratio) + Color.green(endColor) * ratio).toInt()
        val b = (Color.blue(startColor) * (1 - ratio) + Color.blue(endColor) * ratio).toInt()
        return Color.rgb(r, g, b)
    }*/
}

private fun Int.dpToPx(context: Context): Int {
    val scale = context.resources.displayMetrics.density
    return (this * scale + 0.5f).toInt()
}
