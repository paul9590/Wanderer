package com.wanderer.client.recycler

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.wanderer.client.PlayerInfo
import com.wanderer.client.R
import com.wanderer.client.Wanderer
import com.wanderer.client.databinding.DialEnterRoomBinding
import com.wanderer.client.databinding.DialPlayerInfoBinding
import com.wanderer.client.databinding.ListPlayerBinding
import com.wanderer.client.databinding.ListRoomBinding

class RoomPlayerRecyclerAdapter(data: ArrayList<PlayerInfo>):
    RecyclerView.Adapter<RoomPlayerRecyclerAdapter.RoomPlayerViewHolder>(){
    private val mData: ArrayList<PlayerInfo>
    private lateinit var context: Context
    private val wanderer = Wanderer.instance

    init {
        mData = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomPlayerViewHolder {

        context = parent.context
        // create a new view-

        return RoomPlayerViewHolder(ListPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RoomPlayerViewHolder, position: Int) {
        val mBinding = holder.mBinding
        bind(mData[position], mBinding)
    }

    private fun bind(item: PlayerInfo, mBinding: ListPlayerBinding) {
        mBinding.imgPlayer.visibility = View.VISIBLE
        mBinding.txtPlayerName.visibility = View.VISIBLE
        mBinding.txtPlayerEmpty.visibility = View.VISIBLE

        if(item.name == "") {
            mBinding.imgPlayer.visibility = View.INVISIBLE
            mBinding.txtPlayerName.visibility = View.INVISIBLE
        }else {
            mBinding.txtPlayerEmpty.visibility = View.INVISIBLE
            mBinding.imgPlayer.setImageResource(R.drawable.img_profile_c)
            mBinding.txtPlayerName.text = item.name
            mBinding.root.setOnClickListener {
                showPlayerInfoDial(item.name)
            }
        }
    }

    private fun showPlayerInfoDial(name: String) {
        val dial = Dialog(context)
        dial.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dial.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val mBinding = DialPlayerInfoBinding.inflate(LayoutInflater.from(context))
        dial.setContentView(mBinding.root)
        mBinding.txtPlayerName.text = name
        mBinding.btnX.setOnClickListener {
            dial.dismiss()
        }
        dial.show()
    }

    override fun getItemCount(): Int = mData.size

    inner class RoomPlayerViewHolder(val mBinding: ListPlayerBinding): RecyclerView.ViewHolder(mBinding.root)
}