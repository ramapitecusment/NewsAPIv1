package com.ramapitecusment.newsapi.scenes.readLater

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ramapitecusment.newsapi.R

class ReadLaterFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, sIS: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_read_later, c, false)
    }
}