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
import com.ramapitecusment.newsapi.services.database.ArticleEntity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class EverythingFragment : Fragment() {
    private val compositeDisposable = CompositeDisposable()
    private val everythingViewModel by viewModel<EverythingViewModel>()
    private lateinit var binding: FragmentEverythingBinding
    private lateinit var adapter: NewsRecyclerViewAdapter
    private var searchObservable: Disposable? = null
    private var isNetworkError = false

    private val paginator = PublishProcessor.create<Int>()
    private var pageNumber = 1
    private var isLoading = false
    private var lastVisibleItem = -1
    private var totalItemCount = -1
    private val VISIBLE_THRESHOLD = 4

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, sIS: Bundle?): View {
        binding = FragmentEverythingBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewmodel = everythingViewModel
        binding.lifecycleOwner = this
        setAdapter()
        setSearchButtonListener()
        setSearchViewListener()
        setLoadModeListener()
        isLoadingListener()
        isErrorListener()
        isNetworkErrorListener()
    }

    private fun setLoadModeListener() {
        binding.newsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                totalItemCount = layoutManager.itemCount
                lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                Log.d(LOG, "onScrolled: $totalItemCount - $lastVisibleItem")
                if (!isLoading && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD)) {
                    pageNumber++
                    paginator.onNext(pageNumber)
                    isLoading = true
                }
            }
        })
        paginator.onNext(pageNumber)

        val result = paginator
            .doOnNext {
                isLoading = true
                binding.scrollProgressbar.visibility = View.VISIBLE
            }.concatMap {

            }
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
            .switchMap {
                everythingViewModel.searchTag.onNext(it)
                Log.d(LOG, "switchMap: ")
                everythingViewModel.articlesSearch
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).toObservable()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ data ->
                // TODO Почему вызывается дважды?
                Log.d(LOG, "setListeners: ")
                data?.let {
                    if (it.isNotEmpty()) {
                        Log.d(LOG, "setDataToRV: ${it.size}")
                        adapter.submitList(it)
                        binding.newsRecyclerView.visibility = View.VISIBLE
                        binding.tvNoArticle.visibility = View.GONE
                        binding.progressbar.visibility = View.GONE
                    }
                }
            }, {
                Log.d(LOG, "Error setDataToRV: $it")
                recyclerViewNoData()
            }, {

            })
        compositeDisposable.add(result)
    }

    private fun setSearchButtonListener() {
        binding.buttonSearch.setOnClickListener {
            everythingViewModel.searchTag.onNext(binding.newsSearch.text.toString())
            val buttonResult = everythingViewModel.articles
                .distinct()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    data?.let {
                        if (it.isNotEmpty()) {
                            // TODO Почему вызывается дважды?
                            Log.d(LOG, "setDataToRV: ${it.size}")
                            adapter.submitList(it)
                            binding.newsRecyclerView.visibility = View.VISIBLE
                            binding.tvNoArticle.visibility = View.GONE
                            binding.progressbar.visibility = View.GONE
                        }
                    }
                }, {
                    Log.d(LOG, "Error setDataToRV: $it")
                    recyclerViewNoData()
                }, {
                    Log.d(LOG, "Completed setDataToRV: $it")
                })
            compositeDisposable.add(buttonResult)
        }
    }

    private fun isNetworkErrorListener() {
        everythingViewModel.isInternetError.observe(viewLifecycleOwner) { isInternetError ->
            Log.e(LOG, "isNetworkErrorListener: $isInternetError")
            isNetworkError = isInternetError
            if (isInternetError) {
                binding.newsRecyclerView.visibility = View.GONE
                binding.tvNoArticle.visibility = View.GONE
                binding.tvInternetProblems.visibility = View.VISIBLE
                binding.progressbar.visibility = View.GONE
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
                binding.progressbar.visibility = View.VISIBLE
            } else {
                binding.progressbar.visibility = View.GONE
            }
        }
    }

    private fun setAdapter() {
        adapter = NewsRecyclerViewAdapter(clickListener)
        binding.newsRecyclerView.adapter = adapter
    }

//    private fun setListeners() {
//        binding.buttonSearch.setOnClickListener {
//            deleteAll()
//            everythingViewModel.searchTag.onNext(binding.newsSearch.text.toString())
//            getFromRemote(binding.newsSearch.text.toString())
//            val buttonResult = getArticlesByTag()
//                .distinct()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ data ->
//                    data?.let {
//                        if (it.isNotEmpty()) {
//                            Log.d(LOG, "setDataToRV: ${it.size}")
//                            adapter.submitList(it)
//                            binding.newsRecyclerView.visibility = View.VISIBLE
//                            binding.tvNoArticle.visibility = View.GONE
//                            binding.progressbar.visibility = View.GONE
//                        }
//                    }
//                }, {
//                    Log.d(LOG, "Error setDataToRV: $it")
//                    recyclerViewNoData()
//                }, {
//                    Log.d(LOG, "Completed setDataToRV: $it")
//                })
//            compositeDisposable.add(buttonResult)
//        }
//
//        val result = binding.newsSearch.textChanges()
//            .debounce(700, TimeUnit.MILLISECONDS)
//            .filter { charSequence ->
//                Log.d(LOG, "before filter rxSearch -$charSequence-")
//                !TextUtils.isEmpty(
//                    binding.newsSearch.text.toString().trim { it <= ' ' }) && !isNetworkError
//            }
//            .map { it.toString() }
//            .distinctUntilChanged()
//            .switchMap {
//                Log.d(LOG, "switchMap rxSearch -$it-")
//                everythingViewModel.searchTag.onNext(it)
//                deleteAll()
//                getFromRemote(it)
//                getArticlesByTag().toObservable()
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//            }
//            .distinct()
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ data ->
//                data?.let {
//                    if (it.isNotEmpty()) {
//                        Log.d(LOG, "setDataToRV: ${it.size}")
//                        adapter.submitList(it)
//                        binding.newsRecyclerView.visibility = View.VISIBLE
//                        binding.tvNoArticle.visibility = View.GONE
//                        binding.progressbar.visibility = View.GONE
//                    }
//                }
//            }, {
//                Log.d(LOG, "Error setDataToRV: $it")
//                recyclerViewNoData()
//            }, {
//
//            })
//        compositeDisposable.add(result)
//    }

    private fun recyclerViewNoData() {
        binding.tvNoArticle.visibility = View.VISIBLE
        binding.newsRecyclerView.visibility = View.GONE
        binding.newsRecyclerView.visibility = View.GONE
    }

//    private fun getArticlesByTag(): Flowable<List<ArticleEntity>> =
//        everythingViewModel.articlesByTag
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())

    private val clickListener: (article: ArticleEntity) -> Unit = { article ->
//        navigateToArticleDetails(article)
    }

    private fun getFromRemote(searchTag: String) {
        everythingViewModel.getFromRemote(searchTag)
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
        searchObservable?.dispose()
        compositeDisposable.dispose()
        everythingViewModel.destroy()
    }
}