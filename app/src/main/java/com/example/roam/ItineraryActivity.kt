package com.example.roam

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.roam.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Div
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.property.TextAlignment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ItineraryActivity : AppCompatActivity() {

    private val itineraryAPI = ItineraryAPI.create()
    private lateinit var itineraryResponse: ItineraryResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itenerary)

        val itineraryRecyclerView = findViewById<RecyclerView>(R.id.itineraryRecyclerView)
        val loadingAnimationView = findViewById<LottieAnimationView>(R.id.loadingAnimationView)
        val shareButton = findViewById<Button>(R.id.button3)
        val saveButton = findViewById<Button>(R.id.saveItinerary)

        val layoutManager = LinearLayoutManager(this)
        itineraryRecyclerView.layoutManager = layoutManager

        if (intent.hasExtra("itineraryResponse")) {
            // ItineraryResponse is passed as extra, retrieve it
            val itineraryResponseString : String? = intent.getStringExtra("itineraryResponse")

            val gson = Gson()
            val itineraryResponse = gson.fromJson(itineraryResponseString, ItineraryResponse::class.java)

            showItinerary(itineraryResponse)
            shareButton.isEnabled = true
        }

        else {
            val swipedRightPlaces = intent.getSerializableExtra("swipedRightPlaces") as String?
            val duration = intent.getStringExtra("duration")!!.toInt()

            val requestBody = swipedRightPlaces?.let { ItineraryRequestData(it, duration) }
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
                            if (responseData != null) {
                                itineraryResponse = responseData
                                showItinerary(responseData)
                                shareButton.isEnabled = true
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Log.e("API Response Error", errorBody ?: "Unknown error")
                        }
                    }

                    override fun onFailure(call: Call<ItineraryResponse>, t: Throwable) {
                        loadingAnimationView.visibility = View.GONE
                        Log.e("API Request", "Failed", t)
                    }
                })
            }
        }

        shareButton.setOnClickListener {
            generateAndSharePdf()
        }

        saveButton.setOnClickListener {
            showTripNameDialog(itineraryResponse)
        }
    }

    private fun showItinerary(itineraryResponse: ItineraryResponse) {
        val itineraryRecyclerView = findViewById<RecyclerView>(R.id.itineraryRecyclerView)
        val adapter = ItineraryAdapter(itineraryResponse.itinerary)
        itineraryRecyclerView.adapter = adapter
    }

    private fun generateAndSharePdf() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Generate the PDF file
                val pdfFile = generatePdf()

                // Share the PDF file
                if (pdfFile != null) {
                    sharePdfFile(pdfFile)
                } else {
                    Log.e("PDF Generation", "Failed to generate PDF")
                }
            } catch (e: Exception) {
                Log.e("PDF Generation", "Error: ${e.message}")
            }
        }
    }

    private fun generatePdf(): File? {
        try {
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val currentDate = Date()
            val pdfFileName = "Itinerary_${dateFormat.format(currentDate)}.pdf"

            val pdfFile = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), pdfFileName)
            pdfFile.createNewFile()

            val writer = PdfWriter(pdfFile)
            val pdf = PdfDocument(writer)
            val document = Document(pdf)


            document.add(Paragraph("Itinerary"))


            for (itineraryDay in itineraryResponse.itinerary) {
                document.add(Paragraph("Day ${itineraryResponse.itinerary.indexOf(itineraryDay) + 1}"))

                for (activity in itineraryDay.activities) {
                    val cardLayout = Div().apply {
                        setPadding(16f)
                        setBackgroundColor(ColorConstants.LIGHT_GRAY)

                        add(Paragraph("Place: ${activity.place}"))
                        add(Paragraph("Time: ${activity.start} - ${activity.end}"))
                    }

                    document.add(cardLayout)
                }
            }

            document.close()

            return pdfFile
        } catch (e: Exception) {
            Log.e("PDF Generation", "Error: ${e.message}")
            return null
        }
    }



    private fun sharePdfFile(pdfFile: File) {
        try {
            val uri = FileProvider.getUriForFile(
                this,
                "${packageName}.provider",
                pdfFile
            )

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "application/pdf"
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(intent, "Share Itinerary PDF"))
        } catch (e: Exception) {
            Log.e("PDF Sharing", "Error: ${e.message}")
        }
    }

    fun showTripNameDialog(itineraryResponse: ItineraryResponse) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.trip_name_dialog, null)
        val editTextTripName = dialogView.findViewById<EditText>(R.id.editTextDialog)

        builder.setView(dialogView)
            .setTitle("Enter Trip Name")
            .setPositiveButton("Save") { dialog, _ ->
                val tripName = editTextTripName.text.toString()
                saveItineraryToFirebase(tripName, itineraryResponse)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun saveItineraryToFirebase(tripName: String, itineraryResponse: ItineraryResponse) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val userItinerariesRef = db.collection("userItineraries").document(userId)
            val itineraryData = mapOf(
                "name" to tripName,
                "itinerary" to mapItineraryResponse(itineraryResponse),
            )

            userItinerariesRef.collection("savedItineraries")
                .add(itineraryData)
                .addOnSuccessListener { documentReference ->
                    Log.d("Firestore", "Itinerary added with ID: ${documentReference.id}")
                    Toast.makeText(this, "Itinerary saved successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error adding itinerary", e)
                    Toast.makeText(this, "Failed to save itinerary", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mapItineraryResponse(itineraryResponse: ItineraryResponse): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        map["success"] = itineraryResponse.success
        map["itinerary"] = itineraryResponse.itinerary.map { mapItineraryDay(it) }
        return map
    }

    private fun mapItineraryDay(itineraryDay: ItineraryDay): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        map["activities"] = itineraryDay.activities.map { mapActivity(it) }
        return map
    }

    private fun mapActivity(activity: Activity): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        map["place"] = activity.place
        map["start"] = activity.start
        map["end"] = activity.end
        return map
    }


}