package com.digia.apirecorder.watcher

import com.digia.apirecorder.player.PlayerService
import com.digia.apirecorder.persistence.RecordRepository
import com.digia.apirecorder.persistence.RequestRepository
import com.digia.apirecorder.persistence.ResponseRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class WatchController @Autowired constructor(
     val playerService: PlayerService,
     val recordRepository: RecordRepository,
     val requestRepository: RequestRepository,
     val responseRepository: ResponseRepository){

    @GetMapping("/watch/{uuid}/{url}")
    fun watchGet(@RequestParam("uuid") playUuid : String, @RequestParam("url") url : String) : HttpEntity<*> {
        val activePlay = playerService.getActivePlay(playUuid)
        return if(activePlay == null){
            ResponseEntity("Unknown play uuid", HttpStatus.BAD_REQUEST)
        }
        else{
            val record = recordRepository.findByUuid(activePlay.recordUuid)
            val request = requestRepository.findByRecordIdAndUrl(record!!.id!!, url)
            if(request != null){
                val response = responseRepository.findByRequestIdAndTimeOffset(request.id!!, activePlay.currentOffset)
                ResponseEntity(response!!.body, HttpStatus.OK)
            }
            else{
                ResponseEntity("Unknown request", HttpStatus.BAD_REQUEST)
            }
        }
    }
}