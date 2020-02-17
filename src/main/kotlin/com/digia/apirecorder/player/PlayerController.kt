package com.digia.apirecorder.player

import com.digia.apirecorder.player.dto.CreatePlayerRequestDTO
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/player")
class PlayerController @Autowired constructor(val playerService : PlayerService) {

    private val log = KotlinLogging.logger {}

    @PostMapping("/create")
    fun createPlay(@RequestBody createPlayerRequestDTO: CreatePlayerRequestDTO) : HttpEntity<*> {
        return try{
            val playUuid = playerService.create(createPlayerRequestDTO)
            ResponseEntity(playUuid, HttpStatus.CREATED)
        }
        catch(e : Exception){
            log.error("unable to create player", e)
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }

    @PostMapping("/{uuid}/remove")
    fun removePlay(@PathVariable("uuid") playUuid : String) : HttpEntity<*> {
        return try{
            playerService.remove(playUuid)
            ResponseEntity("", HttpStatus.OK)
        }
        catch(e : Exception){
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }


    @PutMapping("/{uuid}/stop")
    fun stopPlay(@PathVariable("uuid") playUuid : String) : HttpEntity<*> {
        return try{
            playerService.updateActivePlay(playUuid, 0, 0)
            ResponseEntity("", HttpStatus.OK)
        }
        catch(e : Exception){
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }

    @PutMapping("/{uuid}/pause")
    fun pausePlay(@PathVariable("uuid") playUuid : String) : HttpEntity<*> {
        return try{
            playerService.updateActivePlay(playUuid, 0)
            ResponseEntity("", HttpStatus.OK)
        }
        catch(e : Exception){
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }

    @PutMapping("/{uuid}/play")
    fun play(@PathVariable("uuid") playUuid : String) : HttpEntity<*> {
        return try {
            playerService.updateActivePlay(playUuid, 1)
            ResponseEntity("", HttpStatus.OK)
        }
        catch(e : Exception){
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }

    @PutMapping("/{uuid}/speed/{speed}")
    fun speed(@PathVariable("uuid") playUuid : String, @PathVariable("speed") speed : Int) : HttpEntity<*> {
        return try {
            playerService.updateActivePlay(playUuid, speed)
            ResponseEntity("", HttpStatus.OK)
        }
        catch(e : Exception){
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }

    @PutMapping("/{uuid}/offset/{offset}")
    fun offset(@PathVariable("uuid") playUuid : String, @PathVariable("offset") offset : Int) : HttpEntity<*> {
        return try {
            playerService.updateActivePlay(playUuid, speed= null, offset= offset)
            ResponseEntity("", HttpStatus.OK)
        }
        catch(e : Exception){
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/list")
    fun listPlayers() : HttpEntity<*>{
        return try{
            ResponseEntity(playerService.getActivePlays().values, HttpStatus.OK)
        }
        catch(e : Exception){
            ResponseEntity(e.message, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}