package com.ramapitecusment.newsapi.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

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


//abstract class BaseRecyclerView<T, TViewHolder : BaseRecyclerView.ItemViewHolder<T>>(
//    private val onClick: (t: T) -> Unit,
//    private val inflate: (i: LayoutInflater) -> ViewBinding,
//    compareItms: (old: T, new: T) -> Boolean,
//    compareCnts: (old: T, new: T) -> Boolean
//) : ListAdapter<T, TViewHolder>(DiffCallback(compareItms, compareCnts)) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TViewHolder =
//        ItemViewHolder.from<T>(inflate, parent) as TViewHolder
//
//
//    override fun onBindViewHolder(holder: TViewHolder, position: Int) {
//        holder.bind(getItem(position))
//    }
//
//    open class ItemViewHolder<T>(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
//        open fun bind(item: T) {
//
//        }
//
//        companion object {
//            fun <T> from(
//                inflate: (i: LayoutInflater) -> ViewBinding,
//                parent: ViewGroup
//            ): ItemViewHolder<T> = ItemViewHolder(inflate(LayoutInflater.from(parent.context)))
//        }
//    }
//
//}
//
//class DiffCallback<K>(
//    private val compareItems: (old: K, new: K) -> Boolean,
//    private val compareContents: (old: K, new: K) -> Boolean
//) : DiffUtil.ItemCallback<K>() {
//    override fun areItemsTheSame(old: K, new: K) = compareItems(old, new)
//    override fun areContentsTheSame(old: K, new: K) = compareContents(old, new)
//}
