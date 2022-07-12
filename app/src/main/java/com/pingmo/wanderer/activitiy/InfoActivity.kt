package com.pingmo.wanderer.activitiy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pingmo.wanderer.databinding.ActivitiyInfoBinding

class InfoActivity : AppCompatActivity(){

    private lateinit var mBinding: ActivitiyInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitiyInfoBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}