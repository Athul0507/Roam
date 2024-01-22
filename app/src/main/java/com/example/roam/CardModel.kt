package com.example.roam


import com.google.gson.annotations.SerializedName


data class PlacesResponse(
    val results: List<Place>
)

data class Place(
    val business_status: String,
    val formatted_address: String,
    val geometry: Geometry,
    val icon: String,
    val icon_background_color: String,
    val icon_mask_base_uri: String,
    val name: String,
    val opening_hours: OpeningHours?,
    val photos: List<Photo>,
    val place_id: String,
    val rating: Double,
    val reference: String,
    val types: List<String>,
    val user_ratings_total: Int,
    var photoBytes: ByteArray?=null


)

data class Geometry(
    val location: Location,
    val viewport: Viewport
)

data class Location(
    val lat: Double,
    val lng: Double
)

data class Viewport(
    val northeast: Location,
    val southwest: Location
)

data class OpeningHours(
    val open_now: Boolean
)

data class Photo(
    val height: Int,
    val html_attributions: List<String>,
    val photo_reference: String?,
    val width: Int
)




data class Activity(
    @SerializedName("place")val place: String,
    val start: String,
    val end: String,

)

data class ItineraryDay(
    @SerializedName("activities")val activities: List<Activity>
)


data class ItineraryResponse(
    val success: Boolean,
    @SerializedName("itinerary")val itinerary: List<ItineraryDay>
)

data class ItineraryRequestData(
    val places: String,
    val duration: Int
    // Add other fields as needed
)
