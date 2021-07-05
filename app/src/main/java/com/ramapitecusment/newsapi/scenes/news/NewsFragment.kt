package com.ramapitecusment.newsapi.scenes.news

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.ramapitecusment.newsapi.R
import com.ramapitecusment.newsapi.databinding.FragmentNewsBinding
import com.ramapitecusment.newsapi.common.TabLayoutAdapter

class NewsFragment : Fragment(R.layout.fragment_news) {
    private val binding: FragmentNewsBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabBar()
    }

    private fun setupTabBar() {
        val adapter = TabLayoutAdapter(this)
        binding.pager.adapter = adapter

        TabLayoutMediator(binding.tab, binding.pager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.scr_evr_tab_title)
                1 -> tab.text = getString(R.string.scr_tp_hdlns_tab_title)
                2 -> tab.text = getString(R.string.scr_rd_ltr_tab_title)
                else -> tab.text = getString(R.string.common_error_tab_title)
            }
        }.attach()
    }
}