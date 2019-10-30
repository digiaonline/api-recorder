package com.digia.apirecorder.recorder

import com.digia.apirecorder.recorder.dto.StartRecordingSetRequestDTO
import com.digia.apirecorder.recorder.dto.StartSingleRecordRequestDTO
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.net.URL
import java.time.Instant

@Controller
class RecorderController @Autowired constructor(val dataManager : DataManager){

    companion object{
        private val LOGGER = LoggerFactory.getLogger(RecorderController::class.java)
    }

    @PostMapping("/record/single/start")
    fun startRecording(@RequestBody startSingleRecordRequest : StartSingleRecordRequestDTO) : HttpEntity<*> {
        return try{
            val uuid = dataManager.startRecording(
                startSingleRecordRequest.url,
                startSingleRecordRequest.period,
                startSingleRecordRequest.duration
            )
             ResponseEntity(uuid, HttpStatus.CREATED)
        }
        catch(e : Exception){
            LOGGER.error("Error while starting the recorder", e)
            ResponseEntity("something went wrong: ${e.message}", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PostMapping("/record/set/start")
    fun startRecording(@RequestBody startRecordingSetRequest : StartRecordingSetRequestDTO) : HttpEntity<*> {
        return try{
            val uuid = dataManager.startRecording(startRecordingSetRequest.urlsToRecord, startRecordingSetRequest.duration)
            ResponseEntity(uuid, HttpStatus.CREATED)
        }
        catch(e : Exception){
            LOGGER.error("Error while starting the recorder", e)
            ResponseEntity("something went wrong: ${e.message}", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PostMapping("record/stop/*")
    fun stopRecording(recordingId : String) : HttpEntity<*>{
        dataManager.stopRecording(recordingId)
        return ResponseEntity("Record $recordingId stopped", HttpStatus.OK)
    }
}