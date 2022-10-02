package com.wanderer.client.activitiy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wanderer.client.databinding.ActivityFriendBinding

class FriendActivity : AppCompatActivity(){

    private lateinit var mBinding: ActivityFriendBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityFriendBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}