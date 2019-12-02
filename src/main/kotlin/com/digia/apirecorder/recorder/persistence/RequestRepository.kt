package com.digia.apirecorder.recorder.persistence

import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
public interface RequestRepository : JpaRepository<Request, Long> {
    fun findById(id : Int) : Request?
    fun findByRecordId(recordId : Int) : List<Request>?
    fun findTopByRecordAndMethodAndBodyAndUrl(record : Record, method : String, body : String?, url : String) : Request?
}