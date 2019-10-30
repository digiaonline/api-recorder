package com.digia.apirecorder.recorder.persistence

import javax.persistence.*

@Entity
data class Request(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Int?,
    @ManyToOne
    @JoinColumn(name="record_id", nullable = false)
    val record : Record,
    val period : Int,
    val url : String)