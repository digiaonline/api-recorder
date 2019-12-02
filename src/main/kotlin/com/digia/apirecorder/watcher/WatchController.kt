package com.digia.apirecorder.watcher

import com.digia.apirecorder.player.PlayerService
import com.digia.apirecorder.recorder.persistence.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@Controller
class WatchController @Autowired constructor(
    val playerService: PlayerService,
    val recordRepository: RecordRepository,
    val requestRepository: RequestRepository,
    val responseRepository: ResponseRepository
){

    @RequestMapping("/watch/{uuid}/url/**")
    fun watch(@PathVariable("uuid") playUuid : String, @RequestBody body : String?, @RequestHeader headers : Map<String, String>?) : HttpEntity<*> {
        val activePlay = playerService.getActivePlay(playUuid)
        return if(activePlay == null){
            ResponseEntity("Unknown player uuid", HttpStatus.BAD_REQUEST)
        }
        else{
            val record = recordRepository.findByUuid(activePlay.recordUuid)
            val url = ServletUriComponentsBuilder.fromCurrentRequest().toUriString().substringAfter("/url/")
            val requestAttributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes
            val method = requestAttributes.request.method
            val request = requestRepository.findTopByRecordAndMethodAndBodyAndUrl(record!!, method, body, url)
            if(request != null){
                val response = responseRepository.findTopByRequestAndTimeOffset(request, activePlay.currentOffset)
                ResponseEntity(response!!.body, HttpStatus.OK)
            }
            else{
                ResponseEntity("Unknown request", HttpStatus.BAD_REQUEST)
            }
        }
    }
}