package com.pingmo.wanderer.activitiy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pingmo.wanderer.databinding.ActivitiyQuestBinding

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