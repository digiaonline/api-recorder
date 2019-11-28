package com.digia.apirecorder.recorder

import com.digia.apirecorder.recorder.dto.StartRecordingSetRequestDTO
import com.digia.apirecorder.recorder.dto.StartSingleRecordRequestDTO
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/record")
class RecorderController @Autowired constructor(val recorderService : RecordService){

    private val log = KotlinLogging.logger {}

    /**
     * @param startSingleRecordRequest : recording request for a single url
     * @return the recording uuid
     */
    @PostMapping("/start/single")
    fun startRecording(@RequestBody startSingleRecordRequest : StartSingleRecordRequestDTO) : HttpEntity<*> {
        return try{
            val uuid = recorderService.startRecording(
                startSingleRecordRequest
            )
             ResponseEntity(uuid, HttpStatus.CREATED)
        }
        catch(e : Exception){
            log.error("Error while starting the recorder", e)
            ResponseEntity("something went wrong: ${e.message}", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    /**
     * @param startRecordingSetRequest: recording request for a url set
     * @return the recording uuid
     */
    @PostMapping("/start/set")
    fun startRecording(@RequestBody startRecordingSetRequest : StartRecordingSetRequestDTO) : HttpEntity<*> {
        return try{
            val uuid = recorderService.startRecording(
                startRecordingSetRequest
            )
            ResponseEntity(uuid, HttpStatus.CREATED)
        }
        catch(e : Exception){
            log.error("Error while starting the recorder", e)
            ResponseEntity("something went wrong: ${e.message}", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    /**
     * @param recordingId: the recording uuid to stop
     * @return OK
     */
    @PostMapping("/stop/{recordingId}")
    fun stopRecording(@PathVariable("recordingId") recordingId : String) : HttpEntity<*>{
        recorderService.stopRecording(recordingId)
        return ResponseEntity("Record $recordingId stopped", HttpStatus.OK)
    }

    /**
     * @return list of records
     */
    @GetMapping("/list")
    fun listRecorders() : HttpEntity<*>{
        return ResponseEntity(recorderService.listRecordings(), HttpStatus.OK)
    }
}