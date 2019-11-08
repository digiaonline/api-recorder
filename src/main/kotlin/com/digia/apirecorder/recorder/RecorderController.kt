package com.digia.apirecorder.recorder

import com.digia.apirecorder.recorder.dto.StartRecordingSetRequestDTO
import com.digia.apirecorder.recorder.dto.StartSingleRecordRequestDTO
import mu.KotlinLogging
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.time.Instant

@Controller
class RecorderController @Autowired constructor(val dataService : DataService){

    private val log = KotlinLogging.logger {}

    @PostMapping("/record/single/start")
    fun startRecording(@RequestBody startSingleRecordRequest : StartSingleRecordRequestDTO) : HttpEntity<*> {
        return try{
            val uuid = dataService.startRecording(
                startSingleRecordRequest.url,
                startSingleRecordRequest.period,
                startSingleRecordRequest.duration,
                if(startSingleRecordRequest.start != null) Instant.parse(startSingleRecordRequest.start) else null
            )
             ResponseEntity(uuid, HttpStatus.CREATED)
        }
        catch(e : Exception){
            log.error("Error while starting the recorder", e)
            ResponseEntity("something went wrong: ${e.message}", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PostMapping("/record/set/start")
    fun startRecording(@RequestBody startRecordingSetRequest : StartRecordingSetRequestDTO) : HttpEntity<*> {
        return try{
            val uuid = dataService.startRecording(
                startRecordingSetRequest.urlsToRecord,
                startRecordingSetRequest.duration,
                if(startRecordingSetRequest.start != null) Instant.parse(startRecordingSetRequest.start) else null
            )
            ResponseEntity(uuid, HttpStatus.CREATED)
        }
        catch(e : Exception){
            log.error("Error while starting the recorder", e)
            ResponseEntity("something went wrong: ${e.message}", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PostMapping("record/stop/{recordingId}")
    fun stopRecording(@PathVariable("recordingId") recordingId : String) : HttpEntity<*>{
        dataService.stopRecording(recordingId)
        return ResponseEntity("Record $recordingId stopped", HttpStatus.OK)
    }
}