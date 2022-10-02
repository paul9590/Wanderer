package com.wanderer.client.activitiy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import com.wanderer.client.R
import com.wanderer.client.User
import com.wanderer.client.Wanderer
import com.wanderer.client.databinding.ActivityMainBinding
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding : ActivityMainBinding

    private lateinit var mainHandler : Handler

    val wanderer: Wanderer = Wanderer.instance
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val activitys = arrayOf(
            Intent(this, FriendActivity :: class.java),
            Intent(this, RoomSearchActivity :: class.java),
            Intent(this, RoomSearchActivity :: class.java),
            Intent(this, InfoActivity :: class.java),
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
            mBinding.imbNotice,
            mBinding.imbQuest,
            mBinding.imbRanking,
            mBinding.imbSetting,
            mBinding.imbShop
        )

        for(i in activitys.indices) {
            imbs[i].setOnClickListener {
                activitys[i].addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                activitys[i].putExtra("user", user)
                startActivity(activitys[i])
            }
        }

        mainHandler = Handler { msg: Message ->
            // 아이디 확인 되면 메인 액티비티로
            try {
                val receive = JSONObject(msg.obj.toString())
                when(msg.what) {
                    101 -> {
                        val isUser = receive.getString("isUser").toInt()
                        if (isUser == 1) {
                            val name = receive.getString("name")
                            val money = receive.getString("money").toInt()
                            val user = User(name, money)
                            wanderer.updateUser(applicationContext, user)
                            mBinding.txtName.text = user.name
                            mBinding.imgProfile.setImageResource(R.drawable.img_profile_c)
                            mBinding.txtMoney.text = user.money.toString()
                        } else {
                            val intent = Intent(this, TitleActivity :: class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            startActivity(intent)
                        }
                    }

                    103 -> {
                        val isUser = receive.getString("isUser")
                        if (isUser == "1") {
                            val name = receive.getString("name")
                            val money = receive.getInt("money")
                            user = User(name, money)
                            if(wanderer.isUser(applicationContext)) {
                                wanderer.updateUser(applicationContext, user)
                            }else {
                                wanderer.addUser(applicationContext, user)
                            }
                            mBinding.txtName.text = name
                            mBinding.txtMoney.text = money.toString()
                        }
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            true
        }
    }

    private fun setUser(context: Context) {
        if(wanderer.isUser(context)) {
            val user = wanderer.getUser(context)
            val map = HashMap<String, String>()
            map["what"] = "203"
            map["name"] = user.name
            wanderer.send(map)
        }else {
            val intent = Intent(this, TitleActivity :: class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        // 초기 핸들러 설정
        Thread {
            while (true) {
                try {
                    Thread.sleep(1)
                    if (wanderer.isConnected()) {
                        wanderer.setHandler(mainHandler)
                        setUser(applicationContext)
                        break
                    }
                } catch (ignored: Exception) {
                }
            }
        }.start()
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}