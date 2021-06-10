package com.ramapitecusment.newsapi.scenes.newsDetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ramapitecusment.newsapi.R

class NewsDetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, sIS: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news_details, c, false)
    }
}