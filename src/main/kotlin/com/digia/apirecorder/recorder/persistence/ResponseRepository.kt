package com.digia.apirecorder.recorder.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.data.domain.PageRequest



@Repository
interface ResponseRepository : JpaRepository<Response, Long> {

    fun findById(id : Int) : Response?
    fun findByRequestId(requestId : Int) : List<Response>?
    @Query("SELECT r from Response r WHERE r.request = ?1 AND r.timeOffset <= ?2 AND r.type = 'NEW' ORDER BY timeOffset DESC")
    fun findNewByRequestAndTimeOffset(request: Request, timeOffset : Int, pageRequest : PageRequest) : List<Response>
}

fun ResponseRepository.findTopByRequestAndTimeOffset(request : Request, timeOffset : Int): Response? {
    val responses = findNewByRequestAndTimeOffset(request, timeOffset, PageRequest.of(0, 1))
    return if(responses.isNotEmpty()) responses[0] else null
}