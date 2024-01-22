package com.example.roam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ItineraryAdapter(private val itineraryDays: List<ItineraryDay>) :
    RecyclerView.Adapter<ItineraryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.itinerary_card_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itineraryDay = itineraryDays[position]
        holder.dayTextView.text = "Day ${position + 1}"
        val layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.activitiesRecyclerView.layoutManager = layoutManager
        val activitiesAdapter = ActivitiesAdapter(itineraryDay.activities)
        holder.activitiesRecyclerView.adapter = activitiesAdapter
    }

    override fun getItemCount(): Int {
        return itineraryDays.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayTextView: TextView = itemView.findViewById(R.id.dayTextView)
        val activitiesRecyclerView: RecyclerView = itemView.findViewById(R.id.activitiesRecyclerView)
    }
}
