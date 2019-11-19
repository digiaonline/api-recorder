package com.digia.apirecorder.persistence

import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class Response(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Int?,
    val body : String?,
    @ManyToOne
    @JoinColumn(name= "request_id", nullable = false)
    val request : Request,
    val timestamp: LocalDateTime,
    @Column(name = "time_offset", nullable = false)
    val timeOffset : Int,
    @Enumerated(EnumType.STRING)
    val type: ResponseType,
    @Column(name = "response_code", nullable = false)
    val responseCode : Int?,
    @Column(name = "response_time", nullable = false)
    val responseTime : Long)

enum class ResponseType{
    NEW,
    OLD
}