package com.digia.apirecorder.recorder


import com.digia.apirecorder.recorder.dto.ParametersDTO
import com.digia.apirecorder.recorder.dto.StartRecordingSetRequestDTO
import com.digia.apirecorder.recorder.dto.StartSingleRecordRequestDTO
import com.digia.apirecorder.recorder.persistence.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random


@Service
class RecordService @Autowired constructor(val recordRepository: RecordRepository,
                                           val requestRepository: RequestRepository,
                                           val responseRepository: ResponseRepository,
                                           val responseBodyRepository: ResponseBodyRepository,
                                           val dataWriter: DataWriterService,
                                           val dataReader: DataReaderService) {

    private val log = KotlinLogging.logger {}
    private val activeRecordings : MutableMap<String, MutableSet<Job>> = mutableMapOf()
    private val objectMapper : ObjectMapper = ObjectMapper()

    fun startRecording(startSingleRecordRequest : StartSingleRecordRequestDTO) : String{
        val uuid = UUID.randomUUID().toString()

        val record = Record(
            null,
            uuid,
            startSingleRecordRequest.name,
            objectMapper.writeValueAsString(startSingleRecordRequest),
            LocalDateTime.now(),
            LocalDateTime.now().plusSeconds(startSingleRecordRequest.duration)
        )
        val request = Request(
            null,
            record,
            startSingleRecordRequest.period,
            startSingleRecordRequest.url.substringAfter("://"),
            startSingleRecordRequest.method,
            startSingleRecordRequest.headers,
            startSingleRecordRequest.body,
            startSingleRecordRequest.feedItemPath,
            startSingleRecordRequest.feedItemUrlTemplate
        )
        recordRepository.save(record)
        requestRepository.save(request)
        val job = startRecordingJob(startSingleRecordRequest.url, request, startSingleRecordRequest.duration, if(startSingleRecordRequest.start != null) Instant.parse(startSingleRecordRequest.start) else null)
        activeRecordings[uuid] = mutableSetOf(job)
        return uuid
    }

    fun startRecording(startRecordingSetRequest : StartRecordingSetRequestDTO) : String{
        val uuid = UUID.randomUUID().toString()
        val record = Record(
            null,
            uuid,
            startRecordingSetRequest.name,
            objectMapper.writeValueAsString(startRecordingSetRequest),
            LocalDateTime.now(),
            LocalDateTime.now().plusSeconds(startRecordingSetRequest.duration)
        )
        recordRepository.save(record)
        activeRecordings[uuid] = mutableSetOf()
        for(urlToRecord in startRecordingSetRequest.urlsToRecord){
            //Creating urls based on parameters
            var urls = listOf<String>(urlToRecord.url)
            if(urlToRecord.parameters != null) urls = injectParameters(urls, urlToRecord.parameters)
            if(startRecordingSetRequest.globalParameters != null) urls = injectParameters(urls, startRecordingSetRequest.globalParameters)
            //Creating jobs based on the urls
            for(url in urls){
                //Even if the body is null, bodies will contain one null element
                var bodies = listOf<String?>(urlToRecord.body)
                if(urlToRecord.body != null){
                    if(urlToRecord.parameters != null) bodies = injectParameters( bodies as List<String>, urlToRecord.parameters)
                    if(startRecordingSetRequest.globalParameters != null) bodies = injectParameters(bodies as List<String>, startRecordingSetRequest.globalParameters)
                }
                for(body in bodies){
                    val request = Request(
                        null,
                        record,
                        urlToRecord.period,
                        url.substringAfter("://"),
                        urlToRecord.method,
                        urlToRecord.headers,
                        body,
                        urlToRecord.feedItemPath,
                        urlToRecord.feedItemUrlTemplate
                    )
                    requestRepository.save(request)
                    val job = startRecordingJob(url, request, startRecordingSetRequest.duration, if(startRecordingSetRequest.start != null) Instant.parse(startRecordingSetRequest.start) else null)
                    activeRecordings[uuid]!!.add(job)
                }
            }
        }
        return uuid
    }

    private fun injectParameters(targetStrings : List<String>, parameters : Array<ParametersDTO>) : List<String>{
        var sourceUrlTargetStringsList = mutableListOf<String>()
        sourceUrlTargetStringsList.addAll(targetStrings)
        for(parameter in parameters){
            val resultUrlList = mutableListOf<String>()
            for(stringToUpdate in sourceUrlTargetStringsList){
                if(stringToUpdate.contains(parameter.name)){
                    for(parameterValue in parameter.values){
                        if(!parameterValue.contains("...")){
                            resultUrlList.add(stringToUpdate.replace(parameter.name, parameterValue))
                        }
                        else{
                            val firstValue = Integer.parseInt(parameterValue.substringBefore("..."))
                            val lastValue = Integer.parseInt(parameterValue.substringAfter("..."))
                            for(i in firstValue .. lastValue){
                                resultUrlList.add(stringToUpdate.replace(parameter.name, i.toString()))
                            }
                        }
                    }
                }
                else{
                    resultUrlList.add(stringToUpdate)
                }
            }
            sourceUrlTargetStringsList = resultUrlList
        }
        return sourceUrlTargetStringsList
    }

    private fun startRecordingJob(url : String, request : Request, recordingDuration : Long, start : Instant?, knownItems : MutableSet<String> = mutableSetOf()) : Job{
        return GlobalScope.launch(Dispatchers.IO){
            log.info("Starting recording $url for record ${request.id}")
            val recordingBeginningTime = start?:Instant.now()
            delay(Duration.between(Instant.now(), recordingBeginningTime).toMillis())
            val randomOffset = if (request.period != 0) request.period else 30
            delay(Random.nextInt(randomOffset)* 1000L) //random offset so that all the recordings won't start at the same time
            var stopRecording = false //if the period is 0, this will be set to true after the first loop
            while(!stopRecording && recordingBeginningTime.plusMillis(recordingDuration * 1000L).isAfter(Instant.now())){
                val frameBeginningTime = Instant.now()
                try {
                    val requestTime = Instant.now()
                    val httpResponse = dataReader.read(url, request.headers, request.body, request.method)
                    val responseTime = Instant.now()
                    val response = dataWriter.write(
                        request,
                        if (request.period != 0) Duration.between(recordingBeginningTime, frameBeginningTime).toMillis() / 1000L else 0,
                        httpResponse,
                        Duration.between(requestTime, responseTime).toMillis()
                    )
                    if(request.feedItemPath != null && response.type == ResponseType.NEW){
                        createFeedItems(request, response, knownItems, recordingDuration)
                    }
                }
                catch(e : Exception){
                    log.warn("$url recording failed: ${e.message}", e)
                }
                val duration = Duration.between(frameBeginningTime, Instant.now())
                delay(request.period  * 1000 - duration.toMillis())
                stopRecording = (request.period == 0) //Record single value if period is 0
            }
            log.info("Finished recording for request ${request.id}")
        }
    }

    fun createFeedItems(request : Request, response : Response, knownItems : MutableSet<String>, recordingDuration : Long){
        //New data in the feed, let's check the new items only
        //We can't make any assumption on what JsonPath.read will return (depends on the content of the json)
        //But we have to convert it to string
        val rawItems = JsonPath.read<List<Any>>(response.responseBody.body, request.feedItemPath)
        val items : List<String> =
        if(request.feedItemUrlTemplate != null){
            rawItems.map {request.feedItemUrlTemplate.replace("ITEM_PLACEHOLDER", it.toString())}
        }
        else{
            rawItems.map { it.toString() }
        }
        items.forEach { item -> run{
            if(!knownItems.contains(item)){
                val subRequest = Request(
                    record = request.record,
                    period = 0,
                    url = item.substringAfter("://"),
                    method = "GET",
                    parentRequest = request,
                    headers = request.headers
                )
                requestRepository.save(subRequest)
                startRecordingJob(item, subRequest, recordingDuration, null)
                knownItems.add(item)
            }
        }}
    }

    fun stopRecording(uuid : String){
        val record = recordRepository.findByUuid(uuid)
        if(record != null){
            activeRecordings[uuid]?.forEach{ job -> job.cancel()}
            record.end = LocalDateTime.now()
            recordRepository.save(record)
        }
    }

    fun delete(uuid : String){
        val record = recordRepository.findByUuid(uuid) ?: throw Exception("Record not found")
        val requests = requestRepository.findByRecordId(record.id!!)
        requests?.forEach {
            val responses = responseRepository.findByRequestId(it.id!!)
            responseRepository.deleteAll(responses)
            requestRepository.delete(it)
        }
        recordRepository.delete(record)
    }

    fun listRecordings() : List<Record>{
        return recordRepository.findAll()
    }

}