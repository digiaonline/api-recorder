package com.digia.apirecorder.player

data class ActivePlay(val playUuid : String, val recordUuid : String, var speed : Int, var currentOffset : Int)