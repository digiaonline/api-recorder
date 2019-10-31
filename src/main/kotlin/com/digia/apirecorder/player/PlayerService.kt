package com.digia.apirecorder.player

import com.digia.apirecorder.persistence.RecordRepository
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.Exception
import java.util.*

@Service
class PlayerService @Autowired constructor(val recordRepository : RecordRepository){

    private val log = KotlinLogging.logger {}
    private val activePlays : MutableMap<String, ActivePlay> = mutableMapOf()
    private val ticker : Job = GlobalScope.launch(Dispatchers.Default)
    {
        delay(1000)
        tick()
    }

    public fun getActivePlay(playUuid : String) : ActivePlay?{
        return activePlays[playUuid]
    }

    public fun create(recordUuid : String) :String?{
        if(recordRepository.findByUuid(recordUuid) == null){
            throw Exception("Unknown recordUuid")
        }
        val playUuid = UUID.randomUUID().toString()
        activePlays[playUuid] = ActivePlay(playUuid, recordUuid, 0, 0)
        return playUuid
    }

    public fun remove(playUuid : String){
        if(activePlays[playUuid] == null){
            throw Exception("Unknown playUuid")
        }
        activePlays.remove(playUuid)
    }

    public fun updateActivePlay(playUuid : String, speed : Int? = null, offset : Int? = null){
        val activePlay = activePlays[playUuid]
        if(activePlay != null){
            if(speed != null) activePlay.speed = speed
            if(offset != null) activePlay.currentOffset = offset
        }
        else{
            throw Exception("Unknown playUuid")
        }
    }

    private fun tick(){
        activePlays.forEach{
            activePlayEntry -> activePlayEntry.value.currentOffset += activePlayEntry.value.speed
        }
    }


}