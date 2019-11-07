package com.digia.apirecorder.recorder

import com.digia.apirecorder.recorder.dto.UrlToRecord
import com.digia.apirecorder.persistence.*
import kotlinx.coroutines.*
import mu.KotlinLogging


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@Service
class DataService @Autowired constructor(val recordRepository: RecordRepository, val requestRepository: RequestRepository, val dataWriter: DataWriterService, val dataReader: DataReaderService) {

    private val log = KotlinLogging.logger {}
    private val activeRecordings : MutableMap<String, MutableSet<Job>> = mutableMapOf()

    fun startRecording(url : String, period : Int, recordingDuration : Long, start : Instant?) : String{
        val uuid = UUID.randomUUID().toString()
        val recordSet = Record(null, uuid, LocalDateTime.now(), LocalDateTime.now().plusSeconds(recordingDuration))
        val record = Request(null, recordSet, period, url.substringAfter("://"))
        recordRepository.save(recordSet)
        requestRepository.save(record)
        val job = startRecordingJob(url, record, recordingDuration, start)
        activeRecordings[uuid] = mutableSetOf(job)
        return uuid
    }

    fun startRecording(urlsToRecord : Array<UrlToRecord>, recordingDuration : Long, start : Instant?) : String{
        val uuid = UUID.randomUUID().toString()
        val record = Record(null,uuid, LocalDateTime.now(), LocalDateTime.now().plusSeconds(recordingDuration))
        recordRepository.save(record)
        activeRecordings[uuid] = mutableSetOf()
        for(urlToRecord in urlsToRecord){
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
                    val job = startRecordingJob(url, request, recordingDuration, start)
                    activeRecordings[uuid]!!.add(job)
                }
            }
            else{
                val request = Request(null, record, urlToRecord.period, urlToRecord.url.substringAfter("://"))
                requestRepository.save(request)
                val job = startRecordingJob(urlToRecord.url, request, recordingDuration, start)
                activeRecordings[uuid]!!.add(job)
            }
        }
        return uuid
    }

    private fun startRecordingJob(url : String, request : Request, recordingDuration : Long, start : Instant?) : Job{
        return GlobalScope.launch(Dispatchers.IO){
            log.info("Starting recording ${request.url} for record ${request.id}")
            val recordingBeginningTime = start?:Instant.now()
            while(recordingBeginningTime.plusMillis(recordingDuration * 1000L ).isAfter(Instant.now())){
                val frameBeginningTime = Instant.now()
                val httpResponse = dataReader.read(url, null)
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