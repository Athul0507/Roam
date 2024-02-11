package com.example.roam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BucketListAdapter(private val locations: List<ExploreLocation>) : RecyclerView.Adapter<BucketListAdapter.BucketListViewHolder>() {

    inner class BucketListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Initialize views
        private val imageView: ImageView = itemView.findViewById(R.id.locationImage)
        private val nameTextView: TextView = itemView.findViewById(R.id.locationName)


        fun bind(location: ExploreLocation) {
            // Bind data to views
            Glide.with(itemView)
                .load(location.imageUrl)
                .into(imageView)
            nameTextView.text = location.name

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BucketListViewHolder{
        // Inflate layout for each item
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.bucket_list_item, parent, false)
        return BucketListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BucketListViewHolder, position: Int) {
        // Bind data to ViewHolder
        val location = locations[position]
        holder.bind(location)
    }

    override fun getItemCount() = locations.size
}
