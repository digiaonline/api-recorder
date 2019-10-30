package com.digia.apirecorder.recorder

import com.digia.apirecorder.recorder.dto.ParametersDTO
import com.digia.apirecorder.recorder.dto.UrlToRecord
import com.digia.apirecorder.recorder.persistence.*
import kotlinx.coroutines.*
import mu.KotlinLogging


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.HashMap

@Service
class DataManager @Autowired constructor(val recordRepository: RecordRepository, val requestRepository: RequestRepository, val dataWriter: DataWriter, val dataReader: DataReader) {

    private val log = KotlinLogging.logger {}
    private val activeRecordings : MutableMap<String, MutableSet<Job>> = HashMap()

    fun startRecording(url : String, period : Int, recordingDuration : Long) : String{
        val uuid = UUID.randomUUID().toString()
        val recordSet = Record(null, uuid, LocalDateTime.now(), LocalDateTime.now().plusSeconds(recordingDuration))
        val record = Request(null, recordSet, period, url)
        recordRepository.save(recordSet)
        requestRepository.save(record)
        val job = startRecordingJob(record, recordingDuration)
        activeRecordings[uuid] = mutableSetOf(job)
        return uuid
    }

    fun startRecording(urlsToRecord : Array<UrlToRecord>, recordingDuration : Long) : String{
        val uuid = UUID.randomUUID().toString()
        val recordSet = Record(null,uuid, LocalDateTime.now(), LocalDateTime.now().plusSeconds(recordingDuration))
        recordRepository.save(recordSet)
        activeRecordings[uuid] = mutableSetOf()
        for(urlToRecord in urlsToRecord){
            val record = Request(null, recordSet, urlToRecord.period, urlToRecord.url)
            requestRepository.save(record)
            val job = startRecordingJob(record, recordingDuration)
            activeRecordings[uuid]!!.add(job)
        }
        return uuid
    }

    private fun startRecordingJob(request : Request, recordingDuration : Long) : Job{
        return GlobalScope.launch(Dispatchers.IO){
            log.info("Starting recording ${request.url} for record ${request.id}")
            val recordingBeginningTime = Instant.now()
            while(recordingBeginningTime.plusMillis(recordingDuration * 1000L ).isAfter(Instant.now())){
                val frameBeginningTime = Instant.now()
                val httpResponse = dataReader.read(request.url, null)
                val responseBody = httpResponse.body?.string()
                if(responseBody != null){
                    dataWriter.write(request, Duration.between(recordingBeginningTime, frameBeginningTime ).toMillis() / 1000L, responseBody)
                }
                val duration = Duration.between(frameBeginningTime, Instant.now())
                delay(request.period * 1000 - duration.toMillis())
            }
            log.info("Finished recording for record ${request.id}")
        }
    }

    fun stopRecording(uuid : String){
        val recordSet = recordRepository.findByUuid(uuid)
        if(recordSet != null){
            activeRecordings[uuid]?.forEach{ job -> job.cancel()}
            recordSet.end = LocalDateTime.now()
            recordRepository.save(recordSet)
        }
    }

}