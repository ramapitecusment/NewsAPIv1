package com.ramapitecusment.newsapi.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ramapitecusment.newsapi.R

class EverythingFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, sIS: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_everything, c, false)
    }
}