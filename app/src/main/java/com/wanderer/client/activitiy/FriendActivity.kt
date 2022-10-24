package com.wanderer.client.activitiy

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wanderer.client.NoticeInfo
import com.wanderer.client.PlayerInfo
import com.wanderer.client.R
import com.wanderer.client.Wanderer
import com.wanderer.client.databinding.ActivityFriendBinding
import com.wanderer.client.databinding.DialFriendBinding
import com.wanderer.client.databinding.DialInfoBinding
import com.wanderer.client.recycler.PlayerRecyclerAdapter
import org.json.JSONException
import org.json.JSONObject

class FriendActivity : AppCompatActivity(){

    private lateinit var mBinding: ActivityFriendBinding
    private val wanderer = Wanderer.instance
    private val mList = ArrayList<PlayerInfo>()
    private val mAdapter = PlayerRecyclerAdapter(mList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityFriendBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setAdapter()
        mBinding.viewFriend.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val last = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                if(recyclerView.adapter!!.itemCount - 1 == last) {
                    val map = HashMap<String, String>()
                    map["what"] = "809"
                    wanderer.send(map)
                }
            }
        })

        mBinding.btnFindFriend.setOnClickListener {
            showFriendDial()
        }

        mBinding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setAdapter() {
        mBinding.viewFriend.adapter = mAdapter
        mBinding.viewFriend.layoutManager = GridLayoutManager(applicationContext, 2)
    }

    private fun setList() {
        val map = HashMap<String, String>()
        map["what"] = "801"
        wanderer.send(map)
    }



    override fun onStart() {
        super.onStart()
        wanderer.setHandler(FriendHandler())
        setList()
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    private fun showFriendDial() {
        val dial = Dialog(this)
        dial.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dial.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val mBinding = DialFriendBinding.inflate(this.layoutInflater)
        dial.setContentView(mBinding.root)
        dial.show()

        mBinding.btnX.setOnClickListener {
            dial.dismiss()
        }

        mBinding.btnSearch.setOnClickListener {

            var validate = true
            val name = mBinding.editSearch.text.toString()

            if (name.length < 2) {
                validate = false
                Toast.makeText(this, "이름이 너무 짧습니다.", Toast.LENGTH_SHORT).show()
            }
            if (name.length > 15) {
                validate = false
                Toast.makeText(this, "이름이 너무 깁니다.", Toast.LENGTH_SHORT).show()
            }

            if(validate) {
                val map = HashMap<String, String>()
                map["what"] = "812"
                map["name"] = name
                wanderer.send(map)
                dial.dismiss()
            }
        }
    }

    private fun showInfoDial(name: String, body: String, isFriend: Boolean, rating: String) {
        val dial = Dialog(this)
        dial.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dial.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val mBinding = DialInfoBinding.inflate(this.layoutInflater)
        dial.setContentView(mBinding.root)
        dial.show()

        mBinding.imgPlayer.setImageResource(R.drawable.img_profile_c)
        mBinding.txtPlayer.text = name

        val rate = "랭킹전 ${rating}점"
        mBinding.txtPlayerRate.text = rate
        mBinding.editInfo.setText(body)

        mBinding.txtInfoCnt.text = "${body.length} / 30"
        mBinding.editInfo.isEnabled = false

        mBinding.btnX.setOnClickListener {
            dial.dismiss()
        }

        if(isFriend) {
            mBinding.btnChange.background = applicationContext.getDrawable(R.drawable.imb_rm_friend)
            mBinding.btnChange.setOnClickListener {
                val map = HashMap<String, String>()
                map["what"] = "802"
                map["name"] = name
                wanderer.send(map)
                dial.dismiss()
            }
        }else {
            mBinding.btnChange.background = applicationContext.getDrawable(R.drawable.imb_add_friend)
            mBinding.btnChange.setOnClickListener {
                val map = HashMap<String, String>()
                map["what"] = "807"
                map["name"] = name
                wanderer.send(map)
            }
        }

    }

    inner class FriendHandler: Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            try {
                val receive = JSONObject(msg.obj.toString())
                when (msg.what) {
                    801 -> {
                        val arr = receive.getJSONArray("friend")
                        val start = mList.size
                        for(i in 0 until arr.length()) {
                            mList.add(PlayerInfo(arr.getJSONObject(i).getString("name")))
                        }
                        mAdapter.notifyItemRangeInserted(start, mList.size)
                    }

                    807 -> {
                        val isValidate = receive.getString("isValidate")
                        when(isValidate[0]) {
                            '0' -> {
                                Toast.makeText(applicationContext, "친구 요청을 실패 했습니다.", Toast.LENGTH_SHORT).show()
                            }
                            '1' -> {
                                Toast.makeText(applicationContext, "친구 요청을 성공 했습니다.", Toast.LENGTH_SHORT).show()
                            }
                            '2' -> {
                                Toast.makeText(applicationContext, "친구 요청을 이미 보냈습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    311 -> {
                        val name = receive.getString("name")
                        val body = receive.getString("body")
                        val rating = receive.getString("rating")
                        val isFriend = receive.getString("isFriend") == "1"

                        if(name == "") {
                            Toast.makeText(applicationContext, "해당 유저를 찾지 못했습니다.", Toast.LENGTH_SHORT).show()
                            showFriendDial()
                        }else {
                            showInfoDial(name, body, isFriend, rating)
                            val isValidate = receive.getString("isValidate") == "1"
                            if(isValidate) {
                                Toast.makeText(applicationContext, "친구 삭제를 성공 했습니다.", Toast.LENGTH_SHORT).show()
                                mList.clear()
                                mAdapter.notifyDataSetChanged()
                            }else {
                                Toast.makeText(applicationContext, "친구 삭제를 실패 했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
}