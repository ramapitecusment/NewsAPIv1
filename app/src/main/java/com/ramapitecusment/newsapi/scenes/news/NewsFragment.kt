package com.ramapitecusment.newsapi.scenes.news

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.ramapitecusment.newsapi.databinding.FragmentNewsBinding
import com.ramapitecusment.newsapi.common.TabLayoutAdapter

class NewsFragment : Fragment() {
    private lateinit var binding: FragmentNewsBinding

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, sIS: Bundle?): View {
        binding = FragmentNewsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabBar()
    }

    private fun setupTabBar() {
        val adapter = TabLayoutAdapter(this)
        binding.pager.adapter = adapter

        TabLayoutMediator(binding.tab, binding.pager) { tab, position ->
            when (position) {
                0 -> tab.text = "Recent"
                1 -> tab.text = "Top Headlines"
                2 -> tab.text = "Read Later"
                else -> tab.text = "Recent"
            }
        }.attach()
    }
}