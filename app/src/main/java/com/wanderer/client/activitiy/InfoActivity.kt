package com.wanderer.client.activitiy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wanderer.client.databinding.ActivitiyInfoBinding

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