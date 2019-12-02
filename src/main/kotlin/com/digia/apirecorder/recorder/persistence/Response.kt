package com.digia.apirecorder.recorder.persistence

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
    val responseTime : Long,
    val hash : ByteArray?,
    @Convert(converter = MapToStringConverter::class)
    val headers : Map<String, String>?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Response

        if (id != other.id) return false
        if (body != other.body) return false
        if (request != other.request) return false
        if (timestamp != other.timestamp) return false
        if (timeOffset != other.timeOffset) return false
        if (type != other.type) return false
        if (responseCode != other.responseCode) return false
        if (responseTime != other.responseTime) return false
        if (hash != null) {
            if (other.hash == null) return false
            if (!hash.contentEquals(other.hash)) return false
        } else if (other.hash != null) return false
        if (headers != other.headers) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (body?.hashCode() ?: 0)
        result = 31 * result + request.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + timeOffset
        result = 31 * result + type.hashCode()
        result = 31 * result + (responseCode ?: 0)
        result = 31 * result + responseTime.hashCode()
        result = 31 * result + (hash?.contentHashCode() ?: 0)
        result = 31 * result + (headers?.hashCode() ?: 0)
        return result
    }
}

enum class ResponseType{
    NEW,
    OLD
}