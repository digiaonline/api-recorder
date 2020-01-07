package com.digia.apirecorder.recorder

import com.digia.apirecorder.recorder.persistence.*

import mu.KotlinLogging
import org.apache.tomcat.util.codec.binary.Base64
import okhttp3.Response as Okhttp3Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.LocalDateTime


@Service
class DataWriterService @Autowired constructor(val responseRepository : ResponseRepository, val responseBodyRepository : ResponseBodyRepository) {

    private val log = KotlinLogging.logger {}
    private val lastBodyHashes : MutableMap<Request, String?> = HashMap()

    fun write(request : Request, timeSinceBeginning : Long, response : Okhttp3Response, responseTime : Long){
        val md = MessageDigest.getInstance("MD5")
        val previousHash = lastBodyHashes[request]
        val responseBody = response.body?.string()
        val hash = if(responseBody != null) Base64.encodeBase64String(md.digest(responseBody.toByteArray())) else null
        var responseBodyInDB = responseBodyRepository.findTopByHash(hash)
        if(responseBodyInDB == null){
            log.info("new data hash for request id ${request.id}")
            val newResponseBody = ResponseBody(null, hash, responseBody)
            responseBodyInDB = responseBodyRepository.save(newResponseBody)
        }
        val frame = if(previousHash == null || !hash!!.contentEquals(previousHash)){
            log.info("new data for request id ${request.id}")
            lastBodyHashes[request] = hash
            Response(
                null,
                null,
                request,
                LocalDateTime.now(),
                timeSinceBeginning.toInt(),
                ResponseType.NEW,
                response.code,
                responseTime,
                response.headers.toMultimap(),
                responseBodyInDB!!
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
                response.headers.toMultimap(),
                responseBodyInDB!!
            )
        }
        responseRepository.save(frame)
    }
}