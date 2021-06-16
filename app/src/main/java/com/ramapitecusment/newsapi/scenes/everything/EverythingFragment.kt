package com.ramapitecusment.newsapi.scenes.everything

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.widget.textChanges
import com.ramapitecusment.newsapi.R
import com.ramapitecusment.newsapi.common.LOG
import com.ramapitecusment.newsapi.common.NewsRecyclerViewAdapter
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


class EverythingFragment : Fragment() {
    private val compositeDisposable = CompositeDisposable()
    private val everythingViewModel by viewModel<EverythingViewModel>()
    private lateinit var binding: FragmentEverythingBinding
    private lateinit var adapter: NewsRecyclerViewAdapter
    private var isNetworkError = false

    private var pageNumber = 1
    private var isPageLoading = false
    private var lastVisibleItem = -1
    private var totalItemCount = -1
    private val VISIBLE_THRESHOLD = 1

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, sIS: Bundle?): View {
        binding = FragmentEverythingBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        setAdapter()
        setSearchButtonListener()
        setSearchViewListener()
        setLoadModeListener()
        isLoadingListener()
        isErrorListener()
        isInternetErrorListener()
        isPageLoadingListener()
        setArticles()
    }

    private fun setArticles() {
        val result = everythingViewModel.articles
            .doOnNext { data ->
                Log.d(LOG, "setGetArticles setListeners: ")
                data?.let {
                    if (it.isNotEmpty()) {
                        Log.d(
                            LOG,
                            "setGetArticles setDataToRV: ${it.size} isLoading: $isPageLoading"
                        )
                        adapter.submitList(it.toArticle())
                        isPageLoading = false
                        binding.newsRecyclerView.visibility = View.VISIBLE
                        binding.tvNoArticle.visibility = View.GONE
                        binding.progressbar.visibility = View.GONE
                        binding.scrollProgressbar.visibility = View.GONE
                    }
                }
            }
            .doOnError {
                Log.d(LOG, "setGetArticles Error setDataToRV: $it")
                recyclerViewNoData()
            }
            .doOnComplete {
                Log.d(LOG, "setGetArticles Complete")
            }
            .subscribe()
        compositeDisposable.add(result)
    }

    private fun setLoadModeListener() {
        binding.newsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                totalItemCount = layoutManager.itemCount
                lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                Log.d(LOG, "onScrolled: $totalItemCount - $lastVisibleItem - $isPageLoading")
                if (!isPageLoading && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD)) {
                    Log.d(
                        LOG,
                        "onScrolled in if: $totalItemCount - $lastVisibleItem - $isPageLoading"
                    )
                    pageNumber++
                    everythingViewModel.pageObservable.onNext(pageNumber)
                    binding.scrollProgressbar.visibility = View.VISIBLE
                    isPageLoading = true
                }
            }
        })

        val result = everythingViewModel.getFromRemoteBySearchTagAndPage
            .onBackpressureBuffer()
            .doOnNext {
                Log.d(LOG, "setLoadModeListener page: $pageNumber")
                isPageLoading = false
                binding.scrollProgressbar.visibility = View.VISIBLE
            }.subscribe()

        compositeDisposable.add(result)
    }

    private fun setSearchViewListener() {
        val result = binding.newsSearch.textChanges()
            .debounce(900, TimeUnit.MILLISECONDS)
            .filter { charSequence ->
                Log.d(LOG, "before filter rxSearch -$charSequence- $isNetworkError")
                !(TextUtils.isEmpty(
                    binding.newsSearch.text.toString().trim { it <= ' ' })) && !isNetworkError
            }
            .map { it.toString() }
            .distinctUntilChanged()
            .doOnNext {
                if (pageNumber != 1) {
                    pageNumber = 1
                }
                Log.d(LOG, "setSearchViewListener: onNext")
                everythingViewModel.pageObservable.onNext(pageNumber)
                everythingViewModel.searchTag.onNext(it)
            }
            .subscribe()
        compositeDisposable.add(result)
    }

    private fun setSearchButtonListener() {
        binding.buttonSearch.setOnClickListener {
            pageNumber = 1
            everythingViewModel.searchTag.onNext(binding.newsSearch.text.toString())
            everythingViewModel.pageObservable.onNext(pageNumber)
        }
    }

    private fun isInternetErrorListener() {
        everythingViewModel.isInternetError.observe(viewLifecycleOwner) { isInternetError ->
            Log.e(LOG, "isNetworkErrorListener: $isInternetError")
            isNetworkError = isInternetError
            if (isInternetError) {
                binding.newsRecyclerView.visibility = View.GONE
                binding.tvNoArticle.visibility = View.GONE
                binding.tvInternetProblems.visibility = View.VISIBLE
                binding.progressbar.visibility = View.GONE
                binding.scrollProgressbar.visibility = View.GONE
            } else {
                binding.tvInternetProblems.visibility = View.GONE
            }
        }
    }

    private fun isErrorListener() {
        everythingViewModel.isError.observe(viewLifecycleOwner) { isError ->
            Log.e(LOG, "isErrorListener: $isError")
            if (isError) {
                binding.newsRecyclerView.visibility = View.GONE
                binding.tvNoArticle.visibility = View.VISIBLE
                binding.progressbar.visibility = View.GONE
                binding.tvInternetProblems.visibility = View.GONE
                binding.scrollProgressbar.visibility = View.GONE
            } else {
                binding.tvNoArticle.visibility = View.GONE
            }
        }
    }

    private fun isLoadingListener() {
        everythingViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Log.d(LOG, "isLoadingListener: $isLoading")
            if (isLoading) {
                binding.newsRecyclerView.visibility = View.GONE
                binding.tvNoArticle.visibility = View.GONE
                binding.tvInternetProblems.visibility = View.GONE
                binding.scrollProgressbar.visibility = View.GONE
                binding.progressbar.visibility = View.VISIBLE
            } else {
                binding.progressbar.visibility = View.GONE
            }
        }
    }

    private fun isPageLoadingListener() {
        everythingViewModel.isPageLoading.observe(viewLifecycleOwner) { isPageLoading ->
            Log.d(LOG, "isPageLoading: $isPageLoading")
            this.isPageLoading = isPageLoading
            if (isPageLoading) {
                binding.newsRecyclerView.visibility = View.VISIBLE
                binding.tvNoArticle.visibility = View.GONE
                binding.tvInternetProblems.visibility = View.GONE
                binding.scrollProgressbar.visibility = View.VISIBLE
                binding.progressbar.visibility = View.GONE
            } else {
                binding.scrollProgressbar.visibility = View.GONE
            }
        }
    }

    private fun setAdapter() {
        adapter = NewsRecyclerViewAdapter(clickListener)
        binding.newsRecyclerView.adapter = adapter
    }

    private fun recyclerViewNoData() {
        binding.tvNoArticle.visibility = View.VISIBLE
        binding.newsRecyclerView.visibility = View.GONE
        binding.newsRecyclerView.visibility = View.GONE
    }

    private val clickListener: (article: Article) -> Unit = { article ->
//        navigateToArticleDetails(article)
    }

    private fun deleteAll() {
        everythingViewModel.deleteAll()
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
        everythingViewModel.destroy()
    }
}