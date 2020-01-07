package com.digia.apirecorder.recorder.persistence

import javax.persistence.*

@Entity
data class ResponseBody(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Int?,
    val hash : String?,
    val body : String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ResponseBody

        if (id != other.id) return false
        if (hash != other.hash) return false
        if (body != other.body) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (hash?.hashCode() ?: 0)
        result = 31 * result + (body?.hashCode() ?: 0)
        return result
    }
}