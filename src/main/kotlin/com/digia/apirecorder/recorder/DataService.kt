package com.digia.apirecorder.recorder

import com.digia.apirecorder.recorder.dto.UrlToRecord
import com.digia.apirecorder.persistence.*
import com.digia.apirecorder.recorder.dto.StartRecordingSetRequestDTO
import com.digia.apirecorder.recorder.dto.StartSingleRecordRequestDTO
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.*
import mu.KotlinLogging


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jackson.JsonObjectSerializer
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.RandomAccess
import kotlin.random.Random

@Service
class DataService @Autowired constructor(val recordRepository: RecordRepository, val requestRepository: RequestRepository, val dataWriter: DataWriterService, val dataReader: DataReaderService) {

    private val log = KotlinLogging.logger {}
    private val activeRecordings : MutableMap<String, MutableSet<Job>> = mutableMapOf()
    private val objectMapper : ObjectMapper = ObjectMapper()

    fun startRecording(startSingleRecordRequest : StartSingleRecordRequestDTO) : String{
        val uuid = UUID.randomUUID().toString()

        val recordSet = Record(null, uuid, startSingleRecordRequest.name, objectMapper.writeValueAsString(startSingleRecordRequest), LocalDateTime.now(), LocalDateTime.now().plusSeconds(startSingleRecordRequest.duration))
        val record = Request(null, recordSet, startSingleRecordRequest.period, startSingleRecordRequest.url.substringAfter("://"))
        recordRepository.save(recordSet)
        requestRepository.save(record)
        val job = startRecordingJob(startSingleRecordRequest.url, record, startSingleRecordRequest.duration, if(startSingleRecordRequest.start != null) Instant.parse(startSingleRecordRequest.start) else null)
        activeRecordings[uuid] = mutableSetOf(job)
        return uuid
    }

    fun startRecording(startRecordingSetRequest : StartRecordingSetRequestDTO) : String{
        val uuid = UUID.randomUUID().toString()
        val record = Record(null,uuid, startRecordingSetRequest.name, objectMapper.writeValueAsString(startRecordingSetRequest), LocalDateTime.now(), LocalDateTime.now().plusSeconds(startRecordingSetRequest.duration))
        recordRepository.save(record)
        activeRecordings[uuid] = mutableSetOf()
        for(urlToRecord in startRecordingSetRequest.urlsToRecord){
            if(urlToRecord.parameters != null){
                //Creating urls based on parameters
                var urlsWithoutInjectedParameters = listOf<String>(urlToRecord.url)
                for(parameter in urlToRecord.parameters){
                    val urlsWithInjectedParameters = mutableListOf<String>()
                    for(urlToUpdate in urlsWithoutInjectedParameters){
                        for(parameterValue in parameter.values){
                            if(!parameterValue.contains("...")){
                                urlsWithInjectedParameters.add(urlToUpdate.replace(parameter.name, parameterValue))
                            }
                            else{
                                val firstValue = Integer.parseInt(parameterValue.substringBefore("..."))
                                val lastValue = Integer.parseInt(parameterValue.substringAfter("..."))
                                for(i in firstValue .. lastValue){
                                    urlsWithInjectedParameters.add(urlToUpdate.replace(parameter.name, i.toString()))
                                }
                            }

                        }
                    }
                    urlsWithoutInjectedParameters = urlsWithInjectedParameters
                }
                //Creating jobs based on the urls
                for(url in urlsWithoutInjectedParameters){
                    val request = Request(null, record, urlToRecord.period, url.substringAfter("://"))
                    requestRepository.save(request)
                    val job = startRecordingJob(url, request, startRecordingSetRequest.duration, if(startRecordingSetRequest.start != null) Instant.parse(startRecordingSetRequest.start) else null)
                    activeRecordings[uuid]!!.add(job)
                }
            }
            else{
                val request = Request(null, record, urlToRecord.period, urlToRecord.url.substringAfter("://"))
                requestRepository.save(request)
                val job = startRecordingJob(urlToRecord.url, request, startRecordingSetRequest.duration, if(startRecordingSetRequest.start != null) Instant.parse(startRecordingSetRequest.start) else null)
                activeRecordings[uuid]!!.add(job)
            }
        }
        return uuid
    }

    private fun startRecordingJob(url : String, request : Request, recordingDuration : Long, start : Instant?) : Job{
        return GlobalScope.launch(Dispatchers.IO){
            log.info("Starting recording ${request.url} for record ${request.id}")
            val recordingBeginningTime = start?:Instant.now()
            delay(Duration.between(recordingBeginningTime, Instant.now()).toMillis())
            if(request.period == 0){
                delay(Random.nextInt(30) * 1000L)
                try {
                    val httpResponse = dataReader.read(url, null)
                    val responseBody = httpResponse.body?.string()
                    if (responseBody != null) {
                        dataWriter.write(
                            request,
                            0,
                            responseBody
                        )
                    }
                }
                catch(e : Exception){
                    log.warn("$url recording failed: ${e.message}")
                }
            }
            else{
                delay(Random.nextInt(request.period) * 1000L) //random offset so that all the recordings won't start at the same time
                while(recordingBeginningTime.plusMillis(recordingDuration * 1000L ).isAfter(Instant.now())){
                    val frameBeginningTime = Instant.now()
                    try {
                        val httpResponse = dataReader.read(url, null)
                        val responseBody = httpResponse.body?.string()
                        if (responseBody != null) {
                            dataWriter.write(
                                request,
                                Duration.between(recordingBeginningTime, frameBeginningTime).toMillis() / 1000L,
                                responseBody
                            )
                        }
                    }
                    catch(e : Exception){
                        log.warn("$url recording failed: ${e.message}")
                    }
                    val duration = Duration.between(frameBeginningTime, Instant.now())
                    delay(request.period * 1000 - duration.toMillis())
                }
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