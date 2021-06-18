package com.ramapitecusment.newsapi.common

import android.view.View
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.ramapitecusment.newsapi.common.mvvm.DataList
import com.ramapitecusment.newsapi.common.mvvm.Text
import com.ramapitecusment.newsapi.common.mvvm.Visible

fun LifecycleOwner.bindVisible(liveData: Visible, view: View, asInvisible: Boolean = false) =
    liveData.observe(this, Observer {
        when {
            it -> view.visibility = View.VISIBLE
            asInvisible -> view.visibility = View.INVISIBLE
            else -> view.visibility = View.GONE
        }
    })


fun LifecycleOwner.bindText(liveData: Text, textView: TextView) =
    liveData.observe(this, Observer { textView.text = it })
