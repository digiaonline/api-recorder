package com.digia.apirecorder.persistence

import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class Record(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Int?,
    val uuid : String,
    val name : String?,
    val definition : String,
    val start : LocalDateTime,
    var end : LocalDateTime?)
