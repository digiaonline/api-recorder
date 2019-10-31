package com.digia.apirecorder.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
public interface RequestRepository : JpaRepository<Request, Long> {
    fun findById(id : Int) :Request?
    fun findByRecordId(recordId : Int) : List<Request>?
    @Query("SELECT r from Request r WHERE r.record_id = ?1 AND r.url = ?2 LIMIT 1")
    fun findByRecordIdAndUrl(recordId : Int, url : String) : Request?
}