package com.ramapitecusment.newsapi.scenes.topheadlines

import android.os.Bundle
import android.text.format.Time
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramapitecusment.newsapi.R
import com.ramapitecusment.newsapi.common.COUNTRY_DEFAULT_VALUE
import com.ramapitecusment.newsapi.common.LOG
import com.ramapitecusment.newsapi.common.NewsRecyclerViewAdapter
import com.ramapitecusment.newsapi.databinding.FragmentEverythingBinding
import com.ramapitecusment.newsapi.databinding.FragmentTopHeadlinesBinding
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

class TopHeadlinesFragment : Fragment() {
    private val compositeDisposable = CompositeDisposable()
    private val viewModel by viewModel<TopHeadlinesViewModel>()
    private lateinit var binding: FragmentTopHeadlinesBinding
    private lateinit var adapter: NewsRecyclerViewAdapter
    private var isNetworkError = false

    private var pageNumber = 1
    private var isPageLoading = false
    private var lastVisibleItem = -1
    private var totalItemCount = -1
    private val VISIBLE_THRESHOLD = 1


    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, sIS: Bundle?): View? {
        binding = FragmentTopHeadlinesBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()
        setLoadModeListener()
        isLoadingListener()
        isErrorListener()
        isInternetErrorListener()
        isPageLoadingListener()
        setInterval()
        setArticles()


        viewModel.pageObservable.onNext(pageNumber)
        viewModel.countryObservable.onNext(COUNTRY_DEFAULT_VALUE)
    }

    private fun setInterval() {
        val disposable: Disposable = Flowable
            .interval(5, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewModel.countryObservable.onNext(COUNTRY_DEFAULT_VALUE)
            }, {
                Log.d(LOG, "setArticles Error: $it")
            }, {

            })
        compositeDisposable.add(disposable)
    }

    private fun setArticles() {
        val disposable: Disposable = viewModel.articles
            .concatMapIterable { v -> v }
            .distinct()
            .toList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ data ->
                Log.d(LOG, "setArticles: ")
                data?.let {
                    if (it.isNotEmpty()) {
                        Log.d(
                            LOG,
                            "setArticles setDataToRV: ${it.size} isLoading: $isPageLoading"
                        )
                        adapter.submitList(it.toArticle())
                        isPageLoading = false
                        binding.newsRecyclerView.visibility = View.VISIBLE
                        binding.tvNoArticle.visibility = View.GONE
                        binding.progressbar.visibility = View.GONE
                        binding.scrollProgressbar.visibility = View.GONE
                    }
                }
            }, {
                Log.d(LOG, "setArticles Error: $it")
            }, {

            })
        compositeDisposable.add(disposable)
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
                    viewModel.pageObservable.onNext(pageNumber)
                    isPageLoading = true
                }
            }
        })

        val result = viewModel.getFromRemoteByCountryAndPage
            .onBackpressureBuffer()
            .doOnNext {
                Log.d(LOG, "setLoadModeListener page: $pageNumber")
                isPageLoading = false
                binding.scrollProgressbar.visibility = View.VISIBLE
            }.subscribe()

        compositeDisposable.add(result)
    }

    private fun isInternetErrorListener() {
        viewModel.isInternetError.observe(viewLifecycleOwner) { isInternetError ->
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
        viewModel.isError.observe(viewLifecycleOwner) { isError ->
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
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
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
        viewModel.isPageLoading.observe(viewLifecycleOwner) { isPageLoading ->
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
        viewModel.onDestroy()
        compositeDisposable.clear()
    }
}