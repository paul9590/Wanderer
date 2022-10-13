package com.wanderer.client.activitiy

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wanderer.client.R
import com.wanderer.client.UserRank
import com.wanderer.client.Wanderer
import com.wanderer.client.databinding.ActivitiyRankingBinding
import com.wanderer.client.recycler.RankingRecyclerAdapter
import org.json.JSONException
import org.json.JSONObject

class RankingActivity : AppCompatActivity(){

    private lateinit var mBinding: ActivitiyRankingBinding

    private val wanderer: Wanderer = Wanderer.instance

    private val mList = ArrayList<UserRank>()
    private val mAdapter = RankingRecyclerAdapter(mList)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitiyRankingBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setAdapter()

        mBinding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setAdapter() {
        mBinding.viewRank.adapter = mAdapter
        mBinding.viewRank.layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
    }

    inner class RankingHandler: Handler(Looper.getMainLooper()) {
        // 아이디 확인 되면 메인 액티비티로
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            try {
                val receive = JSONObject(msg.obj.toString())
                when (msg.what) {
                    210 -> {
                        val info = receive.getJSONArray("info").getJSONObject(0)
                        val name = info.getString("name")
                        val rank = info.getString("rank")
                        val rate = info.getString("rating")
                        mBinding.txtName.text = name
                        mBinding.txtRank.text = "${rank} 위"
                        mBinding.txtRate.text = "${rate} 점"

                        val start = mList.size
                        val arr = receive.getJSONArray("ranking")
                        val imgs = arrayOf(R.drawable.img_result1, R.drawable.img_result2, R.drawable.img_result3)

                        for(i in 0 .. 2) {
                            val data = arr.getJSONObject(i)
                            mList.add(UserRank(name = data.getString("name"), rate = data.getString("rating").toInt(), rank = data.getString("rank").toInt(), img = imgs[i]))
                        }

                        for(i in 3 until arr.length()) {
                            val data = arr.getJSONObject(i)
                            mList.add(UserRank(name = data.getString("name"), rate = data.getString("rating").toInt(), rank = data.getString("rank").toInt(), img = 0))
                        }
                        mAdapter.notifyItemRangeInserted(start, mList.size)
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        wanderer.setHandler(RankingHandler())

        val map = HashMap<String, String>()
        map["what"] = "210"
        wanderer.send(map)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}