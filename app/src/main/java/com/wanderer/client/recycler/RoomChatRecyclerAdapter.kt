package com.wanderer.client.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wanderer.client.databinding.ListChatBinding

class RoomChatRecyclerAdapter(data: ArrayList<String>):
    RecyclerView.Adapter<RoomChatRecyclerAdapter.RoomChatViewHolder>(){
    private val mData: ArrayList<String>
    private lateinit var context: Context

    init {
        mData = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomChatViewHolder {

        context = parent.context
        // create a new view-

        return RoomChatViewHolder(ListChatBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RoomChatViewHolder, position: Int) {
        val mBinding = holder.mBinding
        bind(mData[position], mBinding)
    }

    private fun bind(item: String, mBinding: ListChatBinding) {
        mBinding.txtChat.text = item
    }

    override fun getItemCount(): Int = mData.size

    inner class RoomChatViewHolder(val mBinding: ListChatBinding): RecyclerView.ViewHolder(mBinding.root)
}