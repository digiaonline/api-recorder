package com.digia.apirecorder.recorder.persistence

import com.fasterxml.jackson.annotation.JsonFormat
import javax.persistence.*

@Entity
data class Request(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Int? = null,
    @ManyToOne
    @JoinColumn(name="record_id", nullable = false)
    val record : Record,
    val period : Int,
    val url : String,
    val method : String,
    @Convert(converter = MapToStringConverter::class)
    val headers : Map<String, String>? = null,
    val body : String? = null,
    @Column(name = "feed_item_path")
    val feedItemPath : String? = null,
    @ManyToOne
    @JoinColumn(name="parent_request_id", nullable = true)
    val parentRequest : Request? = null
)

