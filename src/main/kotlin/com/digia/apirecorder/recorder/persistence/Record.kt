package com.digia.apirecorder.recorder.persistence

import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class Record(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Int?,
    val uuid : String,
    val start : LocalDateTime,
    var end : LocalDateTime?)
