package com.digia.apirecorder.player

import org.springframework.web.bind.annotation.GetMapping

class WatchController {

    @GetMapping("/watch/*")
    fun watchGet() : String{
        return ""
    }
}