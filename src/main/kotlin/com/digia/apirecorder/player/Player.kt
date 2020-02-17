package com.digia.apirecorder.player

data class Player(val playUuid : String, val recordUuid : String, var speed : Int, var currentOffset : Int)