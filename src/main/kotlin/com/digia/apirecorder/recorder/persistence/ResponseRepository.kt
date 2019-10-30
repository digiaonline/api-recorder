package com.digia.apirecorder.recorder.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ResponseRepository : JpaRepository<Response, Long> {

    fun findById(id : Int) : Response?
    fun findByRequestId(requestId : Int) : List<Response>?
    fun findByRequestIdAndTimeOffset(requestId: Int, timeOffset : Int) : Response?
}