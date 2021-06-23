package com.ramapitecusment.newsapi.scenes.everything

import android.os.Bundle
import android.view.*
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ramapitecusment.newsapi.R
import com.ramapitecusment.newsapi.common.*
import com.ramapitecusment.newsapi.common.mvvm.BaseFragment
import com.ramapitecusment.newsapi.databinding.FragmentEverythingBinding
import com.ramapitecusment.newsapi.services.database.Article
import org.koin.androidx.viewmodel.ext.android.viewModel


class EverythingFragment : BaseFragment<EverythingViewModel>(R.layout.fragment_everything) {
    override val viewModel: EverythingViewModel by viewModel()
    private val binding: FragmentEverythingBinding by viewBinding()
    private lateinit var adapter: NewsRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        bindViewModel()
    }

    private fun initViews() {
        adapter = NewsRecyclerViewAdapter(
            clickListener,
            R.layout.news_item,
            { old, new -> old.id == new.id },
            { old, new -> old == new }
        )
        binding.newsLayout.newsRecyclerView.adapter = adapter

        binding.buttonSearch.setOnClickListener {
            viewModel.searchButtonClicked()
        }
    }

    private fun bindViewModel() {
        with(viewModel) {
            with(binding) {
                bindVisible(loadingVisible, newsLayout.progressbar)
                bindVisible(errorVisible, newsLayout.tvNoArticle)
                bindVisible(internetErrorVisible, newsLayout.tvInternetProblems)
                bindVisible(pageLoadingVisible, newsLayout.scrollProgressbar)
                bindVisible(recyclerViewVisible, newsLayout.newsRecyclerView)

                bindTextChange(searchTag, newsSearch, searchTagRX, pageRx).addToSubscription()
                bindText(searchTag, newsSearch, searchTagRX)
                bindRecyclerViewAdapter(articles, adapter)

                bindPager(newsLayout.newsRecyclerView, isLoadingPage) { increasePageValue() }
            }
        }
    }

    private val clickListener: (article: Article) -> Unit = { article ->
//        navigateToArticleDetails(article)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_everythig, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> {
                viewModel.deleteAll()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}