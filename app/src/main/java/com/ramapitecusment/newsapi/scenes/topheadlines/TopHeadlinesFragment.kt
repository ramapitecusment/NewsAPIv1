package com.ramapitecusment.newsapi.scenes.topheadlines

import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ramapitecusment.newsapi.R
import com.ramapitecusment.newsapi.common.*
import com.ramapitecusment.newsapi.common.mvvm.BaseFragment
import com.ramapitecusment.newsapi.databinding.FragmentTopHeadlinesBinding
import com.ramapitecusment.newsapi.scenes.news.NewsFragmentDirections
import com.ramapitecusment.newsapi.services.database.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TopHeadlinesFragment : BaseFragment<TopHeadlinesViewModel>(R.layout.fragment_top_headlines) {
    override val viewModel: TopHeadlinesViewModel by viewModel()
    private val binding: FragmentTopHeadlinesBinding by viewBinding()
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
        adapter = NewsRecyclerViewAdapter(articleClickListener, readLaterClickListener)
        binding.newsLayout.newsRecyclerView.adapter = adapter
    }

    override fun bindViewModel() {
        super.bindViewModel()
        with(viewModel) {
            with(binding) {
                bindVisible(loadingVisible, newsLayout.progressbar)
                bindVisible(errorVisible, newsLayout.noArticleTextView)
                bindVisible(internetErrorVisible, newsLayout.internetProblemsTextView)
                bindVisible(pageLoadingVisible, newsLayout.scrollProgressbar)
                bindVisible(recyclerViewVisible, newsLayout.newsRecyclerView)

                bindRecyclerViewAdapter(articles, adapter)

                bindPager(newsLayout.newsRecyclerView, isLoadingPage) { increasePageValue() }
            }
        }
    }

    private val articleClickListener: (article: Article) -> Unit = { article ->
        findNavController().navigate(NewsFragmentDirections.toDetails(article))
    }

    private val readLaterClickListener: (article: Article) -> Unit = { article ->
        viewModel.readLaterButtonClicked(article)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_everythig, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> {
                viewModel.deleteAllClicked()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}