package com.digia.apirecorder.recorder

import com.digia.apirecorder.persistence.ResponseRepository
import com.digia.apirecorder.persistence.Request
import com.digia.apirecorder.persistence.Response
import com.digia.apirecorder.persistence.ResponseType
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.LocalDateTime


@Service
class DataWriterService @Autowired constructor(val responseRepository : ResponseRepository) {

    private val log = KotlinLogging.logger {}
    private val lastBodyHashes : MutableMap<Request, ByteArray> = HashMap()

    fun write(request : Request, timeSinceBeginning : Long, responseBody: String, code : Int, responseTime : Long){
        val md = MessageDigest.getInstance("MD5")
        val previousHash = lastBodyHashes[request]
        val hash = md.digest(responseBody.toByteArray())
        val frame = if(previousHash == null || !hash!!.contentEquals(previousHash)){
            log.info("new data for record id ${request.id}")
            lastBodyHashes[request] = hash
            Response(null, responseBody, request, LocalDateTime.now(), timeSinceBeginning.toInt(), ResponseType.NEW, code, responseTime)
        }
        else{
            log.info("old data for record id ${request.id}")
            Response(null, null, request, LocalDateTime.now(), timeSinceBeginning.toInt(), ResponseType.OLD, code, responseTime)
        }
        responseRepository.save(frame)
    }
}