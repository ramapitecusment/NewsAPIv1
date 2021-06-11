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
import io.reactivex.rxjava3.annotations.NonNull
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

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, sIS: Bundle?): View? {
        binding = FragmentEverythingBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewmodel = everythingViewModel
        binding.lifecycleOwner = this
        setListeners()
        setAdapter()
        everythingViewModel.searchTag.value = QUERY_DEFAULT
    }

    private fun setAdapter() {
        adapter = NewsRecyclerViewAdapter(clickListener)
        binding.newsRecyclerView.adapter = adapter
    }

    private fun setListeners() {
        binding.buttonSearch.setOnClickListener {
            everythingViewModel.searchTag.value = binding.newsSearch.text.toString()
            getFromRemote(binding.newsSearch.text.toString())
        }

        val result = binding.newsSearch.textChanges()
            .debounce(700, TimeUnit.MILLISECONDS)
            .filter { charSequence ->
                Log.d(LOG, "before filter rxSearch -$charSequence-")
                !TextUtils.isEmpty(binding.newsSearch.text.toString().trim { it <= ' ' })
            }
            .map { it.toString() }
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.d(LOG, "rxSearch -$it-")
                everythingViewModel.searchTag.value = it
                getFromRemote(it)
            }
        compositeDisposable.add(result)
    }

    private val clickListener: (article: ArticleEntity) -> Unit = { article ->
//        navigateToArticleDetails(article)
    }

    private fun getFromRemote(searchTag: String) {
        everythingViewModel.getFromRemote(searchTag)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_everythig, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> {
                everythingViewModel.deleteAll()
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