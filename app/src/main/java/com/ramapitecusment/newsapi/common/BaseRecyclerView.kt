package com.ramapitecusment.newsapi.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseRecyclerView<TData, TViewHolder>(
    private val onClick: (item: TData) -> Unit,
    compareItems: (old: TData, new: TData) -> Boolean,
    compareContents: (old: TData, new: TData) -> Boolean
) : ListAdapter<TData, TViewHolder>(
    DiffCallback(compareItems, compareContents)
) where TViewHolder : BaseRecyclerView.ItemViewHolder<TData> {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TViewHolder {
        return ItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemViewHolder<TData>(val binding: ViewBinding) :
        RecyclerView.ViewHolder((binding).root) {
        fun bind(item: TData) {

        }

        companion object {
            fun from(parent: ViewGroup): ItemViewHolder {
                return LayoutInflater.from(parent.context).inflate(resId, parent, false)
            }
        }
    }

    private class DiffCallback<K>(
        private val compareItems: (old: K, new: K) -> Boolean,
        private val compareContents: (old: K, new: K) -> Boolean
    ) : DiffUtil.ItemCallback<K>() {
        override fun areItemsTheSame(old: K, new: K) = compareItems(old, new)
        override fun areContentsTheSame(old: K, new: K) = compareContents(old, new)
    }

}