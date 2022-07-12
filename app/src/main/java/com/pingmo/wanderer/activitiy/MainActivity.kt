package com.pingmo.wanderer.activitiy

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.pingmo.wanderer.R
import com.pingmo.wanderer.Wanderer
import com.pingmo.wanderer.databinding.ActivityMainBinding
import com.pingmo.wanderer.viewmodel.MainViewModel
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding : ActivityMainBinding

    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mBinding.view = mainViewModel

        mBinding.lifecycleOwner = this

        if(!Wanderer.isUser(this)) {
            val intent = Intent(this, TitleActivity :: class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }

        val activitys = arrayOf(
            Intent(this, FriendActivity :: class.java),
            Intent(this, GameActivity :: class.java),
            Intent(this, GameActivity :: class.java),
            Intent(this, InfoActivity :: class.java),
            Intent(this, MenuActivity :: class.java),
            Intent(this, NoticeActivity :: class.java),
            Intent(this, QuestActivity :: class.java),
            Intent(this, RankingActivity :: class.java),
            Intent(this, SettingActivitiy :: class.java),
            Intent(this, ShopActivitiy :: class.java)
        )

        val imbs = arrayOf(
            mBinding.imbFriend,
            mBinding.imbFriendlyGame,
            mBinding.imbRankGame,
            mBinding.imbInfo,
            mBinding.imbMenu,
            mBinding.imbNotice,
            mBinding.imbQuest,
            mBinding.imbRanking,
            mBinding.imbSetting,
            mBinding.imbShop
        )

        for(i in activitys.indices) {
            imbs[i].setOnClickListener {
                val intent = Intent(this, TitleActivity :: class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}