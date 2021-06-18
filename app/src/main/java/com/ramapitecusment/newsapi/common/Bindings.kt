package com.ramapitecusment.newsapi.common

import android.view.MenuItem
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

fun LifecycleOwner.bindTitle(liveData: Text, toolbar: androidx.appcompat.widget.Toolbar) =
    liveData.observe(this, Observer { toolbar.title = it })

fun LifecycleOwner.bindText(liveData: Text, textView: TextView) =
    liveData.observe(this, Observer { textView.text = it })

fun LifecycleOwner.bindMenuItemVisibility(liveData: Visible, menuItem: MenuItem) =
    liveData.observe(this, Observer { menuItem.isVisible = it })

