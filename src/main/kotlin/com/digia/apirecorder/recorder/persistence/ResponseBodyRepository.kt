package com.digia.apirecorder.recorder.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface ResponseBodyRepository : JpaRepository<ResponseBody, Long> {

    fun findTopByHash(hash : String?) : ResponseBody?
}