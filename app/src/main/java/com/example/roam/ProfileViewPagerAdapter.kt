package com.example.roam

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ProfileViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 2 // Number of fragments

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SavedTripsFragment()
            1 -> BucketListFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
}