package com.ramapitecusment.newsapi.scenes.newsDetails

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ramapitecusment.newsapi.R
import com.ramapitecusment.newsapi.common.glideImage
import com.ramapitecusment.newsapi.databinding.FragmentNewsDetailsBinding

class NewsDetailsFragment : Fragment(R.layout.fragment_news_details) {
    private val binding: FragmentNewsDetailsBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: NewsDetailsFragmentArgs by navArgs()
        val article = args.article

        binding.titleTextView.text = article.title
        binding.descriptionTextView.text = article.description
        binding.newsImageView.glideImage(article.urlToImage, binding.imageProgressBar)

        if (article.isReadLater == 1)
            binding.readLaterImageButton.setImageResource(R.drawable.ic_bookmark_red)
        else
            binding.readLaterImageButton.setImageResource(R.drawable.ic_bookmark_white)

        binding.sourceTextView.text = article.source
        binding.timeTextView.text = article.publishedAt

        binding.detailsWebView.settings.domStorageEnabled = true
        binding.detailsWebView.settings.javaScriptEnabled = true
        binding.detailsWebView.settings.loadsImagesAutomatically = true
        binding.detailsWebView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        binding.detailsWebView.webViewClient = WebViewClient()
        article.url?.let { binding.detailsWebView.loadUrl(it) }

        if (binding.detailsWebView.isShown) {
            binding.detailsWebViewProgressBar.visibility = View.INVISIBLE
        }
    }
}