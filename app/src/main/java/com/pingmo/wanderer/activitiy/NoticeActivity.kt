package com.pingmo.wanderer.activitiy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pingmo.wanderer.databinding.ActivitiyNoticeBinding

class NoticeActivity : AppCompatActivity(){

    private lateinit var mBinding: ActivitiyNoticeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitiyNoticeBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}