package com.ramapitecusment.newsapi.scenes.everything

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.jakewharton.rxbinding4.widget.textChanges
import com.ramapitecusment.newsapi.R
import com.ramapitecusment.newsapi.common.LOG
import com.ramapitecusment.newsapi.common.NewsRecyclerViewAdapter
import com.ramapitecusment.newsapi.common.mvvm.BaseFragment
import com.ramapitecusment.newsapi.databinding.FragmentEverythingBinding
import com.ramapitecusment.newsapi.services.database.Article
import com.ramapitecusment.newsapi.services.database.ArticleEntity
import com.ramapitecusment.newsapi.services.database.toArticle
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import java.util.concurrent.TimeUnit


class EverythingFragment : BaseFragment<EverythingViewModel>(R.layout.fragment_everything) {
    private val compositeDisposable = CompositeDisposable()

    override val viewModel: EverythingViewModel by viewModel()
    private val binding: FragmentEverythingBinding by viewBinding()
    private lateinit var adapter: NewsRecyclerViewAdapter
    private var isNetworkError = false

    private var pageNumber = 1
    private var isPageLoading = false
    private var lastVisibleItem = -1
    private var totalItemCount = -1
    private val VISIBLE_THRESHOLD = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
//        setSearchButtonListener()
//        setSearchViewListener()
//        setLoadModeListener()
//        isLoadingListener()
//        isErrorListener()
//        isInternetErrorListener()
//        isPageLoadingListener()
//        setArticles()
    }

//    private fun setArticles() {
//        val result = viewModel.articles
//            .doOnNext { data ->
//                Log.d(LOG, "setGetArticles setListeners: ")
//                data?.let {
//                    if (it.isNotEmpty()) {
//                        Log.d(
//                            LOG,
//                            "setGetArticles setDataToRV: ${it.size} isLoading: $isPageLoading"
//                        )
//                        adapter.submitList(it.toArticle())
//                        isPageLoading = false
//                        binding.newsLayout.newsRecyclerView.visibility = View.VISIBLE
//                        binding.newsLayout.tvNoArticle.visibility = View.GONE
//                        binding.newsLayout.progressbar.visibility = View.GONE
//                        binding.newsLayout.scrollProgressbar.visibility = View.GONE
//                    }
//                }
//            }
//            .doOnError {
//                Log.d(LOG, "setGetArticles Error setDataToRV: $it")
//                recyclerViewNoData()
//            }
//            .doOnComplete {
//                Log.d(LOG, "setGetArticles Complete")
//            }
//            .subscribe()
//        compositeDisposable.add(result)
//    }
//
//    private fun setLoadModeListener() {
//        binding.newsLayout.newsRecyclerView.addOnScrollListener(object :
//            RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//                totalItemCount = layoutManager.itemCount
//                lastVisibleItem = layoutManager.findLastVisibleItemPosition()
//                Log.d(LOG, "onScrolled: $totalItemCount - $lastVisibleItem - $isPageLoading")
//                if (!isPageLoading && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD)) {
//                    Log.d(
//                        LOG,
//                        "onScrolled in if: $totalItemCount - $lastVisibleItem - $isPageLoading"
//                    )
//                    pageNumber++
//                    viewModel.pageObservable.onNext(pageNumber)
//                    binding.newsLayout.scrollProgressbar.visibility = View.VISIBLE
//                    isPageLoading = true
//                }
//            }
//        })
//
//        val result = viewModel.getFromRemoteBySearchTagAndPage
//            .onBackpressureBuffer()
//            .doOnNext {
//                Log.d(LOG, "setLoadModeListener page: $pageNumber")
//                isPageLoading = false
//                binding.newsLayout.scrollProgressbar.visibility = View.VISIBLE
//            }.subscribe()
//
//        compositeDisposable.add(result)
//    }
//
//    private fun setSearchViewListener() {
//        val result = binding.newsSearch.textChanges()
//            .debounce(900, TimeUnit.MILLISECONDS)
//            .filter { charSequence ->
//                Log.d(LOG, "before filter rxSearch -$charSequence- $isNetworkError")
//                !(TextUtils.isEmpty(
//                    binding.newsSearch.text.toString().trim { it <= ' ' })) && !isNetworkError
//            }
//            .map { it.toString() }
//            .distinctUntilChanged()
//            .doOnNext {
//                if (pageNumber != 1) {
//                    pageNumber = 1
//                }
//                Log.d(LOG, "setSearchViewListener: onNext")
//                viewModel.pageObservable.onNext(pageNumber)
//                viewModel.searchTag.onNext(it)
//            }
//            .subscribe()
//        compositeDisposable.add(result)
//    }
//
//    private fun setSearchButtonListener() {
//        binding.buttonSearch.setOnClickListener {
//            pageNumber = 1
//            viewModel.searchTag.onNext(binding.newsSearch.text.toString())
//            viewModel.pageObservable.onNext(pageNumber)
//        }
//    }

    private fun isInternetErrorListener() {
        viewModel.isInternetError.observe(viewLifecycleOwner) { isInternetError ->
            Log.e(LOG, "isNetworkErrorListener: $isInternetError")
            isNetworkError = isInternetError
            if (isInternetError) {
                binding.newsLayout.newsRecyclerView.visibility = View.GONE
                binding.newsLayout.tvNoArticle.visibility = View.GONE
                binding.newsLayout.tvInternetProblems.visibility = View.VISIBLE
                binding.newsLayout.progressbar.visibility = View.GONE
                binding.newsLayout.scrollProgressbar.visibility = View.GONE
            } else {
                binding.newsLayout.tvInternetProblems.visibility = View.GONE
            }
        }
    }

    private fun isErrorListener() {
        viewModel.isError.observe(viewLifecycleOwner) { isError ->
            Log.e(LOG, "isErrorListener: $isError")
            if (isError) {
                binding.newsLayout.newsRecyclerView.visibility = View.GONE
                binding.newsLayout.tvNoArticle.visibility = View.VISIBLE
                binding.newsLayout.progressbar.visibility = View.GONE
                binding.newsLayout.tvInternetProblems.visibility = View.GONE
                binding.newsLayout.scrollProgressbar.visibility = View.GONE
            } else {
                binding.newsLayout.tvNoArticle.visibility = View.GONE
            }
        }
    }

    private fun isLoadingListener() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Log.d(LOG, "isLoadingListener: $isLoading")
            if (isLoading) {
                binding.newsLayout.newsRecyclerView.visibility = View.GONE
                binding.newsLayout.tvNoArticle.visibility = View.GONE
                binding.newsLayout.tvInternetProblems.visibility = View.GONE
                binding.newsLayout.scrollProgressbar.visibility = View.GONE
                binding.newsLayout.progressbar.visibility = View.VISIBLE
            } else {
                binding.newsLayout.progressbar.visibility = View.GONE
            }
        }
    }

    private fun isPageLoadingListener() {
        viewModel.isPageLoading.observe(viewLifecycleOwner) { isPageLoading ->
            Log.d(LOG, "isPageLoading: $isPageLoading")
            this.isPageLoading = isPageLoading
            if (isPageLoading) {
                binding.newsLayout.newsRecyclerView.visibility = View.VISIBLE
                binding.newsLayout.tvNoArticle.visibility = View.GONE
                binding.newsLayout.tvInternetProblems.visibility = View.GONE
                binding.newsLayout.scrollProgressbar.visibility = View.VISIBLE
                binding.newsLayout.progressbar.visibility = View.GONE
            } else {
                binding.newsLayout.scrollProgressbar.visibility = View.GONE
            }
        }
    }

    private fun setAdapter() {
        adapter = NewsRecyclerViewAdapter(clickListener)
        binding.newsLayout.newsRecyclerView.adapter = adapter
    }

    private fun recyclerViewNoData() {
        binding.newsLayout.tvNoArticle.visibility = View.VISIBLE
        binding.newsLayout.newsRecyclerView.visibility = View.GONE
        binding.newsLayout.newsRecyclerView.visibility = View.GONE
    }

    private val clickListener: (article: Article) -> Unit = { article ->
//        navigateToArticleDetails(article)
    }

    private fun deleteAll() {
        viewModel.deleteAll()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_everythig, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> {
                deleteAll()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
        viewModel.destroy()
    }
}