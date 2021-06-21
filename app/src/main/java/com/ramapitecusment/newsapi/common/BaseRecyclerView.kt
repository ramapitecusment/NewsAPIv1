package com.ramapitecusment.newsapi.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerView<T>(
    private val onClick: (t: T) -> Unit,
    @LayoutRes private val layoutResId : Int,
    compareItms: (old: T, new: T) -> Boolean,
    compareCnts: (old: T, new: T) -> Boolean
) : ListAdapter<T, BaseRecyclerView.ViewHolder>(DiffCallback(compareItms, compareCnts)) {

    abstract fun ViewHolder.onBind(item : T)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.onBind(item)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}

class DiffCallback<K>(
    private val compareItems: (old: K, new: K) -> Boolean,
    private val compareContents: (old: K, new: K) -> Boolean
) : DiffUtil.ItemCallback<K>() {
    override fun areItemsTheSame(old: K, new: K) = compareItems(old, new)
    override fun areContentsTheSame(old: K, new: K) = compareContents(old, new)
}
