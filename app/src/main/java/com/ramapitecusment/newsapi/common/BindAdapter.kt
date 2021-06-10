package com.ramapitecusment.newsapi.common

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.ramapitecusment.newsapi.R
import com.ramapitecusment.newsapi.services.database.ArticleEntity

@BindingAdapter("bindImage", "progressBar")
fun bindImage(image: ImageView, imgUrl: String?, progressBar: ProgressBar) {
    imgUrl?.let {
        Glide.with(image.context)
            .load(imgUrl)
            .apply(RequestOptions().error(R.drawable.ic_connection_error))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
                    return false;
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
                    return false;
                }

            })
            .into(image)
    }
}

@BindingAdapter("setDataToRV", "textViewId")
fun setDataToRV(recyclerView: RecyclerView, data: List<ArticleEntity>?, noData: TextView) {
    data?.let {
        if (it.isNotEmpty()) {
            Log.d(LOG, "setDataToRV:")
            val adapter = recyclerView.adapter as? NewsRecyclerViewAdapter
            adapter?.submitList(it)
            recyclerView.visibility = View.VISIBLE
            noData.visibility = View.GONE
        } else {
            recyclerView.visibility = View.GONE
            noData.visibility = View.VISIBLE
        }
    }
}