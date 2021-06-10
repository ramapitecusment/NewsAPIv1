package com.ramapitecusment.newsapi.scenes.everything

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ramapitecusment.newsapi.R
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EverythingFragment : Fragment() {

    private val everythingViewModel by viewModel<EverythingViewModel>()

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, sIS: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_everything, c, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        everythingViewModel.getFromRemote("bitcoin")
    }

    override fun onDestroy() {
        super.onDestroy()
        everythingViewModel.destroy()
    }
}