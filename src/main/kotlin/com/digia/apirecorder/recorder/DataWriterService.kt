package com.digia.apirecorder.recorder

import com.digia.apirecorder.recorder.persistence.ResponseRepository
import com.digia.apirecorder.recorder.persistence.Request
import com.digia.apirecorder.recorder.persistence.Response

import com.digia.apirecorder.recorder.persistence.ResponseType
import mu.KotlinLogging
import okhttp3.Response as Okhttp3Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.LocalDateTime


@Service
class DataWriterService @Autowired constructor(val responseRepository : ResponseRepository) {

    private val log = KotlinLogging.logger {}
    private val lastBodyHashes : MutableMap<Request, ByteArray?> = HashMap()

    fun write(request : Request, timeSinceBeginning : Long, response : Okhttp3Response, responseTime : Long){
        val md = MessageDigest.getInstance("MD5")
        val previousHash = lastBodyHashes[request]
        val responseBody = response.body?.string()
        val hash = if(responseBody != null)  md.digest(responseBody.toByteArray()) else null
        val frame = if(previousHash == null || !hash!!.contentEquals(previousHash)){
            log.info("new data for record id ${request.id}")
            lastBodyHashes[request] = hash
            Response(
                null,
                responseBody,
                request,
                LocalDateTime.now(),
                timeSinceBeginning.toInt(),
                ResponseType.NEW,
                response.code,
                responseTime,
                hash,
                response.headers.toMultimap()
            )
        }
        else{
            log.info("old data for record id ${request.id}")
            Response(
                null,
                null,
                request,
                LocalDateTime.now(),
                timeSinceBeginning.toInt(),
                ResponseType.OLD,
                response.code,
                responseTime,
                hash,
                response.headers.toMultimap()
            )
        }
        responseRepository.save(frame)
    }
}