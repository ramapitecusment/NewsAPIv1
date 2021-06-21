package com.ramapitecusment.newsapi.common

import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideOption
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.ramapitecusment.newsapi.R
import com.ramapitecusment.newsapi.common.mvvm.BaseViewModel
import com.ramapitecusment.newsapi.common.mvvm.DataList

import com.ramapitecusment.newsapi.common.mvvm.Text
import com.ramapitecusment.newsapi.common.mvvm.Visible

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

fun LifecycleOwner.bindMenuItemVisibility(liveData: Visible, menuItem: MenuItem) =
    liveData.observe(this, { menuItem.isVisible = it })

fun <T, TViewHolder : RecyclerView.ViewHolder?> LifecycleOwner.bindRecyclerViewAdapter(
    lifeData: DataList<T>,
    adapter: ListAdapter<T, TViewHolder>
) =
    lifeData.observe(this) {
        adapter.submitList(it)
    }

fun LifecycleOwner.bindTextTwoWay(liveData: Text, editText: EditText) {
    editText.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            liveData.mutableValue = s?.toString() ?: ""
        }
    })

    liveData.observe(this, Observer {
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
}

private var <T> BaseViewModel.MutableBindingProperty<T>.mutableValue: T
    get() = liveData.value!!
    set(value) {
        (liveData as MutableLiveData<T>).value = value
    }

@GlideOption
fun Glide.bindImage(urlToImage: String?, image: ImageView, progressbar: ProgressBar) {
    urlToImage?.let {
        Glide.with(image.context)
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
            .into(image)
    }
}

fun ImageView.glideImage(urlToImage: String?, image: ImageView, progressbar: ProgressBar) {
    urlToImage?.let {
        Glide.with(image.context)
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
            .into(image)
    }
}
