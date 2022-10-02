package com.wanderer.client

import java.io.Serializable

data class RoomInfo(val name: String, val playerInfo: ArrayList<PlayerInfo>): Serializable

data class PlayerInfo(val name: String = ""): Serializable