package com.example.roam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class LocationAdapter(private val locations: List<ExploreLocation>, private val saveClickListener: OnSaveClickListener) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    interface OnSaveClickListener {
        fun onSaveClicked(location: ExploreLocation)
    }

    inner class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.locationImage)
        private val nameTextView: TextView = itemView.findViewById(R.id.locationName)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.locationDescription)
        private val saveButton: ImageButton = itemView.findViewById(R.id.save)

        fun bind(location: ExploreLocation) {
            Glide.with(itemView)
                .load(location.imageUrl)
                .into(imageView)
            nameTextView.text = location.name
            descriptionTextView.text = location.description


            saveButton.setOnClickListener {
                val newImageResource = if (saveButton.tag == R.drawable.save) {
                    R.drawable.saved
                } else {
                    R.drawable.save
                }

                // Set the new image resource
                saveButton.setImageResource(newImageResource)

                // Save the new image resource to the tag
                saveButton.tag = newImageResource

                // Call the listener method if needed
                saveClickListener.onSaveClicked(location)
            }
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
