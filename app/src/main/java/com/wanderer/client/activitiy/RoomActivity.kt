package com.wanderer.client.activitiy

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wanderer.client.*
import com.wanderer.client.databinding.ActivityRoomBinding
import com.wanderer.client.databinding.DialAddRoomBinding
import com.wanderer.client.recycler.ChatRecyclerAdapter
import com.wanderer.client.recycler.RoomPlayerRecyclerAdapter
import org.json.JSONException
import org.json.JSONObject

class RoomActivity : AppCompatActivity(){

    private lateinit var mBinding: ActivityRoomBinding
    private val mList = ArrayList<PlayerInfo>()
    private val chatList = ArrayList<String>()
    private val mAdapter = RoomPlayerRecyclerAdapter(mList)
    private val chatAdapter = ChatRecyclerAdapter(chatList)

    private val wanderer: Wanderer = Wanderer.instance
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRoomBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setAdapter()

        user = intent.getSerializableExtra("user") as User

        val roomInfo = intent.getSerializableExtra("roomInfo") as RoomInfo
        setRoomInfo(roomInfo)
        mBinding.btnRoomChat.setOnClickListener {
            // 욕설 필터링 추가 해야 함
            val chat = mBinding.editRoomChat.text.toString()
            if(chat.length < 30) {
                val map = HashMap<String, String>()
                map["what"] = "601"
                map["chat"] = chat
                mBinding.editRoomChat.setText("")
                wanderer.send(map)
            }else {
                Toast.makeText(applicationContext, "채팅이 너무 깁니다.", Toast.LENGTH_SHORT).show()
            }
        }
        mBinding.btnBack.setOnClickListener {
            quitRoom()
        }

        mBinding.btnStart.setOnClickListener {
            val map = HashMap<String, String>()
            map["what"] = "701"
            wanderer.send(map)
        }

        mBinding.btnRoomSetting.setOnClickListener {
            showAlterRoomDial()
        }
    }

    override fun onBackPressed() {
        quitRoom()
    }

    private fun quitRoom() {
        val map = HashMap<String, String>()
        map["what"] = "304"
        wanderer.send(map)
    }

    private fun setRoomInfo(roomInfo: RoomInfo) {
        mBinding.txtRoomName.text = roomInfo.name
        mList.clear()
        mList.addAll(roomInfo.playerInfo)
        mAdapter.notifyDataSetChanged()
    }

    private fun setAdapter() {
        mBinding.viewRoomUser.adapter = mAdapter
        mBinding.viewRoomUser.layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)

        mBinding.viewChat.adapter = chatAdapter
        mBinding.viewChat.layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
    }

    private fun showAlterRoomDial() {
        val dial = Dialog(this)
        dial.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dial.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val mBinding = DialAddRoomBinding.inflate(this.layoutInflater)
        dial.setContentView(mBinding.root)
        dial.show()
        mBinding.editEnterRoomPw.visibility = View.INVISIBLE
        mBinding.chkRoomLock.setOnCheckedChangeListener {view, isChecked ->
            if(isChecked) {
                mBinding.editEnterRoomPw.visibility = View.VISIBLE
            }else {
                mBinding.editEnterRoomPw.visibility = View.INVISIBLE
                mBinding.editEnterRoomPw.setText("")
            }
        }
        mBinding.txtEnterRoom.text = "방 변경하기"
        mBinding.btnEnterRoomYes.background = applicationContext.getDrawable(R.drawable.imb_alter)
        mBinding.btnEnterRoomYes.setOnClickListener {
            val map = HashMap<String, String>()
            map["what"] = "302"
            map["name"] = mBinding.editRoomName.text.toString()
            map["roomPW"] = mBinding.editEnterRoomPw.text.toString()
            map["max"] = mBinding.txtRoomMax.text.toString()
            wanderer.send(map)
            dial.dismiss()
        }
        mBinding.btnEnterRoomNo.setOnClickListener {
            dial.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        wanderer.setHandler(RoomHandler())
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    inner class RoomHandler: Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            try {
                val receive = JSONObject(msg.obj.toString())
                when (msg.what) {
                    303 -> {
                        val isValidate = receive.getString("isValidate") == "1"
                        if(isValidate) {
                            val arr = receive.getJSONArray("player")
                            mList.clear()
                            val name = receive.getString("name")
                            mBinding.txtRoomName.text = name
                            for(i in 0 until arr.length()) {
                                val data = arr.getJSONObject(i)
                                mList.add(PlayerInfo(data.getString("name").toString()))
                            }
                            mAdapter.notifyDataSetChanged()
                        } else {
                            Toast.makeText(applicationContext, "다시 입장해 주세요.", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                    304 -> {
                        finish()
                    }

                    601 -> {
                        val chat = receive.getString("name") + " : " +  receive.getString("chat")
                        val size = chatList.size
                        chatList.add(chat)
                        chatAdapter.notifyItemInserted(size)
                    }

                    701 -> {
                        // 게임 시작
                        val intent = Intent(applicationContext, GameActivity :: class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        val playerList = Array(4) { mList[it].name }
                        intent.putExtra("user", user)
                        intent.putExtra("player", playerList)
                        startActivity(intent)
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
}