package com.ramapitecusment.newsapi.scenes.topheadlines

import android.os.Bundle
import android.text.format.Time
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ramapitecusment.newsapi.R
import com.ramapitecusment.newsapi.common.*
import com.ramapitecusment.newsapi.common.mvvm.BaseFragment
import com.ramapitecusment.newsapi.databinding.FragmentEverythingBinding
import com.ramapitecusment.newsapi.databinding.FragmentTopHeadlinesBinding
import com.ramapitecusment.newsapi.databinding.NewsItemBinding
import com.ramapitecusment.newsapi.scenes.everything.EverythingViewModel
import com.ramapitecusment.newsapi.services.database.Article
import com.ramapitecusment.newsapi.services.database.ArticleEntity
import com.ramapitecusment.newsapi.services.database.toArticle
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

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

    private fun bindViewModel() {
        with(viewModel) {
            with(binding) {
                bindVisible(loadingVisible, newsLayout.progressbar)
                bindVisible(errorVisible, newsLayout.tvNoArticle)
                bindVisible(internetErrorVisible, newsLayout.tvInternetProblems)
                bindVisible(pageLoadingVisible, newsLayout.scrollProgressbar)
                bindVisible(recyclerViewVisible, newsLayout.newsRecyclerView)

                bindRecyclerViewAdapter(articles, adapter)

                bindPager(newsLayout.newsRecyclerView, isLoadingPage) { increasePageValue() }
            }
        }
    }

    private val articleClickListener: (article: Article) -> Unit = { article ->
        Toast.makeText(requireContext(), "articleClickListener", Toast.LENGTH_SHORT).show()
//        navigateToArticleDetails(article)
    }

    private val readLaterClickListener: (article: Article) -> Unit = { article ->
        Toast.makeText(requireContext(), "readLaterClickListener", Toast.LENGTH_SHORT).show()
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