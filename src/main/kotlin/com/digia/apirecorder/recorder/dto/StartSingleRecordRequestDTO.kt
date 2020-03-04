package com.digia.apirecorder.recorder.dto

data class StartSingleRecordRequestDTO(
    val url : String,
    val period : Int,
    val duration : Long,
    val start : String?,
    val name : String?,
    val method : String = "GET",
    val headers : Map<String, List<String>>?,
    val body : String?,
    val feedItemPath : String?
)