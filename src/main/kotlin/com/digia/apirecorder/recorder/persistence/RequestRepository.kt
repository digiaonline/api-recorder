package com.digia.apirecorder.recorder.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
public interface RequestRepository : JpaRepository<Request, Long> {
    fun findById(id : Int) :Request?
    fun findByRecordId(recordId : Int) : List<Request>?
    fun findByRecordIdAndUrl(recordId : Int, url : String) : Request?
}