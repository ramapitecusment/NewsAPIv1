package com.ramapitecusment.newsapi.scenes.everything

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ramapitecusment.newsapi.R
import com.ramapitecusment.newsapi.common.*
import com.ramapitecusment.newsapi.common.mvvm.BaseFragment
import com.ramapitecusment.newsapi.databinding.FragmentEverythingBinding
import com.ramapitecusment.newsapi.scenes.news.NewsFragmentDirections
import com.ramapitecusment.newsapi.services.database.Article
import com.ramapitecusment.newsapi.services.database.toArticleEntity
import com.ramapitecusment.newsapi.services.database.toReadLaterArticle
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
        adapter = NewsRecyclerViewAdapter(articleClickListener, readLaterClickListener)
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

    private val articleClickListener: (article: Article) -> Unit = { article ->
        Toast.makeText(requireContext(), "articleClickListener", Toast.LENGTH_SHORT).show()
        findNavController().navigate(NewsFragmentDirections.toDetails(article))
    }

    private val readLaterClickListener: (article: Article) -> Unit = { article ->
        Toast.makeText(
            requireContext(),
            getString(R.string.toast_added_read_later),
            Toast.LENGTH_SHORT
        ).show()
        Log.d(LOG, "readLaterClickListener: ${article.id}")
        if (article.isReadLater == 1) viewModel.unreadLaterArticle(article.toArticleEntity())
        else viewModel.readLaterArticle(article.toArticleEntity())
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