package com.pingmo.wanderer.activitiy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pingmo.wanderer.databinding.ActivitiyMenuBinding

class MenuActivity : AppCompatActivity(){

    private lateinit var mBinding: ActivitiyMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitiyMenuBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}