package com.digia.apirecorder.player

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class PlayerController @Autowired constructor(val playerService : PlayerService) {

    @PostMapping("/player/{uuid}/create")
    fun createPlay(@RequestParam("uuid") recordUuid : String) : HttpEntity<*> {
        return try{
            val playUuid = playerService.create(recordUuid)
            ResponseEntity(playUuid, HttpStatus.CREATED)
        }
        catch(e : Exception){
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }

    @PostMapping("/player/{uuid}/remove")
    fun removePlay(@RequestParam("uuid") playUuid : String) : HttpEntity<*> {
        return try{
            playerService.remove(playUuid)
            ResponseEntity("", HttpStatus.OK)
        }
        catch(e : Exception){
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }


    @PutMapping("/player/{uuid}/stop")
    fun stopPlay(@RequestParam("uuid") playUuid : String) : HttpEntity<*> {
        return try{
            playerService.updateActivePlay(playUuid, 0, 0)
            ResponseEntity("", HttpStatus.OK)
        }
        catch(e : Exception){
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }

    @PutMapping("/player/{uuid}/pause")
    fun pausePlay(@RequestParam("uuid") playUuid : String) : HttpEntity<*> {
        return try{
            playerService.updateActivePlay(playUuid, 0)
            ResponseEntity("", HttpStatus.OK)
        }
        catch(e : Exception){
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }

    @PutMapping("/player/{uuid}/play")
    fun play(@RequestParam("uuid") playUuid : String) : HttpEntity<*> {
        return try {
            playerService.updateActivePlay(playUuid, 1)
            ResponseEntity("", HttpStatus.OK)
        }
        catch(e : Exception){
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }
}