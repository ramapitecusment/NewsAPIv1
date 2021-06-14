package com.ramapitecusment.newsapi.scenes.everything

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.jakewharton.rxbinding4.widget.textChanges
import com.ramapitecusment.newsapi.R
import com.ramapitecusment.newsapi.common.LOG
import com.ramapitecusment.newsapi.common.NewsRecyclerViewAdapter
import com.ramapitecusment.newsapi.common.QUERY_DEFAULT
import com.ramapitecusment.newsapi.databinding.FragmentEverythingBinding
import com.ramapitecusment.newsapi.services.database.ArticleEntity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
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
        setListeners()
        isLoadingListener()
        isErrorListener()
        isNetworkErrorListener()
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

    private fun setListeners() {
        binding.buttonSearch.setOnClickListener {
            everythingViewModel.searchTag.onNext(binding.newsSearch.text.toString())
            getFromRemote(binding.newsSearch.text.toString())
        }

        val result = binding.newsSearch.textChanges()
            .debounce(700, TimeUnit.MILLISECONDS)
            .filter { charSequence ->
                Log.d(LOG, "before filter rxSearch -$charSequence-")
                !TextUtils.isEmpty(
                    binding.newsSearch.text.toString().trim { it <= ' ' }) && !isNetworkError
            }
            .map { it.toString() }
            .distinctUntilChanged()
            .switchMap {
                Log.d(LOG, "switchMap rxSearch -$it-")
                everythingViewModel.searchTag.onNext(it)
                deleteAll()
                getFromRemote(it)
                getArticlesByTag().toObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            }
            .distinct()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ data ->
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

    private fun recyclerViewNoData() {
        binding.tvNoArticle.visibility = View.VISIBLE
        binding.newsRecyclerView.visibility = View.GONE
        binding.newsRecyclerView.visibility = View.GONE
    }

    private fun getArticlesByTag(): Flowable<List<ArticleEntity>> =
        everythingViewModel.articlesByTag
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

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