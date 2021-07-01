package com.ramapitecusment.newsapi.scenes.readLater

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ramapitecusment.newsapi.R
import com.ramapitecusment.newsapi.common.*
import com.ramapitecusment.newsapi.common.mvvm.BaseFragment
import com.ramapitecusment.newsapi.databinding.FragmentEverythingBinding
import com.ramapitecusment.newsapi.databinding.FragmentNewsDetailsBinding
import com.ramapitecusment.newsapi.databinding.FragmentReadLaterBinding
import com.ramapitecusment.newsapi.scenes.everything.EverythingViewModel
import com.ramapitecusment.newsapi.scenes.news.NewsFragmentDirections
import com.ramapitecusment.newsapi.services.database.Article
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReadLaterFragment : BaseFragment<ReadLaterViewModel>(R.layout.fragment_read_later) {
    private val binding: FragmentReadLaterBinding by viewBinding()
    override val viewModel: ReadLaterViewModel by viewModel()
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
            }
        }
    }

    private val articleClickListener: (article: Article) -> Unit = { article ->
        findNavController().navigate(NewsFragmentDirections.toDetails(article))
    }

    private val readLaterClickListener: (article: Article) -> Unit = { article ->
        viewModel.readLaterClicked(article)
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