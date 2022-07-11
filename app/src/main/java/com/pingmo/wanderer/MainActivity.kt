package com.pingmo.wanderer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.pingmo.wanderer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding : ActivityMainBinding
    lateinit var mainViewModel: MyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mainViewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        mBinding.view = mainViewModel

        mBinding.lifecycleOwner = this
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}