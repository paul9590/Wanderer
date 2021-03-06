package com.pingmo.wanderer.activitiy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pingmo.wanderer.databinding.ActivitiySettingBinding

class SettingActivitiy : AppCompatActivity(){

    private lateinit var mBinding: ActivitiySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitiySettingBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}