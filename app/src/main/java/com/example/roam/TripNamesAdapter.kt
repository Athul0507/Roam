package com.example.roam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TripNamesAdapter(
    private val tripNames: List<SavedTrips>,
    private val onItemClick: (ItineraryResponse) -> Unit
) : RecyclerView.Adapter<TripNamesAdapter.TripNameViewHolder>() {

    inner class TripNameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tripNameTextView: TextView = itemView.findViewById(R.id.tripNameTextView)

        fun bind(tripName: SavedTrips) {
            tripNameTextView.text = tripName.name
            itemView.setOnClickListener {
                onItemClick(tripName.itinerary)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripNameViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trip_view, parent, false)
        return TripNameViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TripNameViewHolder, position: Int) {
        val tripName = tripNames[position]
        holder.bind(tripName)
    }

    override fun getItemCount(): Int {
        return tripNames.size
    }
}
