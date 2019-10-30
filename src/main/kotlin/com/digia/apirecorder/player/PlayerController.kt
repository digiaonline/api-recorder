package com.digia.apirecorder.player

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@Controller
class PlayerController {


    @PostMapping("/startplay/*")
    fun startPlayingRecording() : String{
        return "ok"
    }
}