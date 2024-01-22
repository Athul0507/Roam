package com.example.roam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ActivitiesAdapter(private val activities: List<Activity>?) :
    RecyclerView.Adapter<ActivitiesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_card_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activity = activities?.get(position)
        if (activity != null) {
            holder.placeTextView.text = activity.place
            holder.timeTextView.text = "${activity.start} - ${activity.end}"
        }
    }

    override fun getItemCount(): Int {
        return activities?.size ?: 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeTextView: TextView = itemView.findViewById(R.id.placeTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
    }
}
