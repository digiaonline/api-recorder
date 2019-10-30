package com.digia.apirecorder.recorder.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RecordRepository : JpaRepository<Record, Long> {

    fun findById(id : Int) : Record?
    fun findByUuid(uuid : String) : Record?
}