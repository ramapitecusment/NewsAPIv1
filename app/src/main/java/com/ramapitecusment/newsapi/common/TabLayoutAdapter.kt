package com.ramapitecusment.newsapi.common

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ramapitecusment.newsapi.scenes.everything.EverythingFragment
import com.ramapitecusment.newsapi.scenes.readLater.ReadLaterFragment
import com.ramapitecusment.newsapi.scenes.topheadlines.TopHeadlinesFragment

class TabLayoutAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> EverythingFragment()
            1 -> TopHeadlinesFragment()
            2 -> ReadLaterFragment()
            else -> EverythingFragment()
        }
    }
}