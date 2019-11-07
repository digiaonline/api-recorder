package com.digia.apirecorder.player

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class PlayerController @Autowired constructor(val playerService : PlayerService) {

    private val log = KotlinLogging.logger {}

    @PostMapping("/player/{uuid}/create")
    fun createPlay(@PathVariable("uuid") recordUuid : String) : HttpEntity<*> {
        return try{
            val playUuid = playerService.create(recordUuid)
            ResponseEntity(playUuid, HttpStatus.CREATED)
        }
        catch(e : Exception){
            log.error("unable to create player", e)
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }

    @PostMapping("/player/{uuid}/remove")
    fun removePlay(@PathVariable("uuid") playUuid : String) : HttpEntity<*> {
        return try{
            playerService.remove(playUuid)
            ResponseEntity("", HttpStatus.OK)
        }
        catch(e : Exception){
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }


    @PutMapping("/player/{uuid}/stop")
    fun stopPlay(@PathVariable("uuid") playUuid : String) : HttpEntity<*> {
        return try{
            playerService.updateActivePlay(playUuid, 0, 0)
            ResponseEntity("", HttpStatus.OK)
        }
        catch(e : Exception){
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }

    @PutMapping("/player/{uuid}/pause")
    fun pausePlay(@PathVariable("uuid") playUuid : String) : HttpEntity<*> {
        return try{
            playerService.updateActivePlay(playUuid, 0)
            ResponseEntity("", HttpStatus.OK)
        }
        catch(e : Exception){
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }

    @PutMapping("/player/{uuid}/play")
    fun play(@PathVariable("uuid") playUuid : String) : HttpEntity<*> {
        return try {
            playerService.updateActivePlay(playUuid, 1)
            ResponseEntity("", HttpStatus.OK)
        }
        catch(e : Exception){
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }

    @PutMapping("/player/{uuid}/speed/{speed}")
    fun speed(@PathVariable("uuid") playUuid : String, @PathVariable("speed") speed : Int) : HttpEntity<*> {
        return try {
            playerService.updateActivePlay(playUuid, speed)
            ResponseEntity("", HttpStatus.OK)
        }
        catch(e : Exception){
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }

    @PutMapping("/player/{uuid}/offset/{offset}")
    fun offset(@PathVariable("uuid") playUuid : String, @PathVariable("offset") offset : Int) : HttpEntity<*> {
        return try {
            playerService.updateActivePlay(playUuid, offset)
            ResponseEntity("", HttpStatus.OK)
        }
        catch(e : Exception){
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }
}