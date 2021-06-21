package com.ramapitecusment.newsapi.scenes.everything

import android.os.Bundle
import android.view.*
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ramapitecusment.newsapi.R
import com.ramapitecusment.newsapi.common.*
import com.ramapitecusment.newsapi.common.mvvm.BaseFragment
import com.ramapitecusment.newsapi.databinding.FragmentEverythingBinding
import com.ramapitecusment.newsapi.databinding.NewsItemBinding
import com.ramapitecusment.newsapi.services.database.Article
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.koin.androidx.viewmodel.ext.android.viewModel


class EverythingFragment : BaseFragment<EverythingViewModel>(R.layout.fragment_everything) {
    override val viewModel: EverythingViewModel by viewModel()
    private val binding: FragmentEverythingBinding by viewBinding()
    private val recyclerViewBinding: NewsItemBinding by viewBinding()
    private lateinit var adapter: NewsRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        bindViewModel()
        viewModel.init()
    }

    private fun initViews() {
        adapter = NewsRecyclerViewAdapter(
            clickListener,
//            recyclerViewBinding,
            R.layout.news_item,
            { old, new -> old.id == new.id },
            { old, new -> old == new }
        )
        binding.newsLayout.newsRecyclerView.adapter = adapter
    }

    private fun bindViewModel() {
        bindVisible(viewModel.loadingVisible, binding.newsLayout.progressbar)
        bindVisible(viewModel.errorVisible, binding.newsLayout.tvNoArticle)
        bindVisible(viewModel.internetErrorVisible, binding.newsLayout.tvInternetProblems)
        bindVisible(viewModel.pageLoadingVisible, binding.newsLayout.scrollProgressbar)
        bindVisible(viewModel.recyclerViewVisible, binding.newsLayout.newsRecyclerView)

        bindTextTwoWay(viewModel.searchTag, binding.newsSearch)
        bindRecyclerViewAdapter(viewModel.articles, adapter)
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

    //    private var isNetworkError = false
//
//    private var pageNumber = 1
//    private var isPageLoading = false
//    private var lastVisibleItem = -1
//    private var totalItemCount = -1
//    private val VISIBLE_THRESHOLD = 1

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
}