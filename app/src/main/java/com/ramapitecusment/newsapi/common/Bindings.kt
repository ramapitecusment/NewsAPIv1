package com.ramapitecusment.newsapi.common

import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.jakewharton.rxbinding4.widget.textChanges
import com.ramapitecusment.newsapi.R
import com.ramapitecusment.newsapi.common.mvvm.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

fun LifecycleOwner.bindVisible(liveData: Visible, view: View, asInvisible: Boolean = false) =
    liveData.observe(this, {
        when {
            it -> view.visibility = View.VISIBLE
            asInvisible -> view.visibility = View.INVISIBLE
            else -> view.visibility = View.GONE
        }
    })

fun LifecycleOwner.bindTitle(liveData: Text, toolbar: androidx.appcompat.widget.Toolbar) =
    liveData.observe(this, { toolbar.title = it })

fun LifecycleOwner.bindText(liveData: Text, textView: TextView) =
    liveData.observe(this, { textView.text = it })

fun LifecycleOwner.bindText(liveData: Text, textView: TextView, subject: PublishSubject<String>) =
    liveData.observe(this, {
        textView.text = it
    })

fun LifecycleOwner.bindMenuItemVisibility(liveData: Visible, menuItem: MenuItem) =
    liveData.observe(this, { menuItem.isVisible = it })

fun <T, TViewHolder : RecyclerView.ViewHolder?> LifecycleOwner.bindRecyclerViewAdapter(
    lifeData: DataList<T>,
    adapter: ListAdapter<T, TViewHolder>
) = lifeData.observe(this) { adapter.submitList(it) }

private var <T> BaseViewModel.MutableBindingProperty<T>.mutableValue: T
    get() = liveData.value!!
    set(value) {
        (liveData as MutableLiveData<T>).value = value
    }

fun ImageView.glideImage(urlToImage: String?, progressbar: ProgressBar) {
    urlToImage?.let {
        Glide.with(this)
            .load(it)
            .apply(RequestOptions().error(R.drawable.ic_connection_error))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressbar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressbar.visibility = View.GONE
                    return false
                }
            })
            .into(this)
    }
}

fun WebView.baseSetup() {
    settings.domStorageEnabled = true
    settings.javaScriptEnabled = true
    settings.loadsImagesAutomatically = true
    scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
    webViewClient = WebViewClient()
}

fun LifecycleOwner.bindTextChange(
    search: Text,
    page: Data<Int>,
    editText: EditText,
    searchObservable: PublishProcessor<String>,
    pageObservable: PublishProcessor<Int>,
//    getData: () -> Unit
): Disposable {

    val disposable = editText.textChanges()
        .debounce(900, TimeUnit.MILLISECONDS)
        .filter {
            !(TextUtils.isEmpty(editText.text.toString().trim { it <= ' ' }))
        }
        .map { it.toString() }
        .distinctUntilChanged()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            Log.d(LOG, "textChanges $it")
            search.mutableValue = it
            page.mutableValue = 1
            // It must be first
            searchObservable.onNext(it)
            // It must be second
            pageObservable.onNext(1)
//            getData()
        }, {
            Log.e(LOG, "Error $it")
        })

    search.observe(this, Observer {
        if (editText.text.toString() == it) {
            return@Observer
        }

        val oldSelection = editText.selectionStart
        val newLength = it?.length ?: 0
        val oldLength = editText.text?.length ?: 0
        val diff = newLength - oldLength
        editText.setText(it)

        var newSelection = when (diff) {
            1, -1 -> oldSelection + diff
            else -> newLength
        }

        if (newSelection < 0) {
            newSelection = 0
        }

        try {
            editText.setSelection(newSelection)
        } catch (e: Exception) {
            print(e)
        }
    })

    return disposable
}

fun LifecycleOwner.bindPager(
    recyclerView: RecyclerView,
    pageLoading: Visible,
    changePage: () -> Unit
) {
    var isLoading = false

    pageLoading.observe(this) {
        isLoading = it
    }

    recyclerView.addOnScrollListener(object :
        RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val totalItemCount = layoutManager.itemCount
            val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
//            Log.d(LOG, "onScrolled: $totalItemCount - $lastVisibleItem")
            if (!isLoading && (totalItemCount <= (lastVisibleItem + 1))) {
                Log.d(LOG, "onScrolled in if: $totalItemCount - $lastVisibleItem")
                changePage()
            }
        }
    })
}

