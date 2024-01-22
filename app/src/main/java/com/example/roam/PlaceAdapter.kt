package com.example.roam

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.roam.databinding.CardViewPlaceBinding

class PlaceAdapter : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    private var places: List<Place>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PlaceViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.card_view_place,
            parent,
            false
        )
    )

    override fun getItemCount() = places?.size ?: 0

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        places?.let {
            val place = it[position]
            holder.binding.place = place
            holder.binding.photoImageView.setImageBitmap(place.photoBytes?.toBitmap())



            holder.binding.executePendingBindings()
        }
    }
    fun getProfileAt(position: Int): Place?{
        return if(position>=0 && position< places?.size!!){
            places!![position]
        }else{null}
    }

    fun setProfiles(places: List<Place>) {
        this.places = places
        notifyDataSetChanged()
    }

    fun updateProfileWithPhoto(place: Place, photoBytes: ByteArray?) {
        val position = places?.indexOf(place)
        if (position != -1 && photoBytes != null) {
            place.photoBytes = photoBytes
            if (position != null) {
                notifyItemChanged(position)
            }
        }
    }

    inner class PlaceViewHolder(val binding: CardViewPlaceBinding) :
        RecyclerView.ViewHolder(binding.root)

}

private fun ByteArray?.toBitmap(): Bitmap? {
    if (this == null) return null
    return BitmapFactory.decodeByteArray(this, 0, size)
}
