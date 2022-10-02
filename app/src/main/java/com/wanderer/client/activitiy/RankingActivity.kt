package com.wanderer.client.activitiy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wanderer.client.databinding.ActivitiyRankingBinding

class RankingActivity : AppCompatActivity(){

    private lateinit var mBinding: ActivitiyRankingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitiyRankingBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}