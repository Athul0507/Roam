package com.example.roam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class LocationAdapter(private val locations: List<ExploreLocation>) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    inner class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.locationImage)
        private val nameTextView: TextView = itemView.findViewById(R.id.locationName)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.locationDescription)

        fun bind(location: ExploreLocation) {
            Glide.with(itemView)
                .load(location.imageUrl)
                .into(imageView)
            nameTextView.text = location.name
            descriptionTextView.text = location.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.taj_mahal, parent, false)
        return LocationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locations[position]
        holder.bind(location)
    }

    override fun getItemCount() = locations.size
}
