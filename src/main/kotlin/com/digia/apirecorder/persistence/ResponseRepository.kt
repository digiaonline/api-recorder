package com.digia.apirecorder.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ResponseRepository : JpaRepository<Response, Long> {

    fun findById(id : Int) : Response?
    fun findByRequestId(requestId : Int) : List<Response>?
    @Query("SELECT r from Response r WHERE r.request_id = ?1 AND time_offset < ?2 SORT BY time_offset LIMIT 1")
    fun findByRequestIdAndTimeOffset(requestId: Int, timeOffset : Int) : Response?
}