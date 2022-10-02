package com.wanderer.client.activitiy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wanderer.client.databinding.ActivitiyQuestBinding

class QuestActivity : AppCompatActivity(){

    private lateinit var mBinding: ActivitiyQuestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitiyQuestBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}