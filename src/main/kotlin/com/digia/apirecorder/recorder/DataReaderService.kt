package com.digia.apirecorder.recorder

import mu.KotlinLogging
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.net.URI
import java.net.URL
import okhttp3.RequestBody.Companion.toRequestBody

@Service
class DataReaderService {

    private val okHttpClient = OkHttpClient()
    private val log = KotlinLogging.logger {}

    fun read(url : String, headers : Map<String,List<String>>?, body : String?, method : String) : Response {
        log.info("Reading data from $url")
        val requestBuilder = Request.Builder().url(url)
        headers?.entries?.forEach { entry ->
            entry.value.forEach{value ->  requestBuilder.addHeader(entry.key, value)}
        }
        requestBuilder.method(method, body?.toRequestBody())
        return okHttpClient.newCall(requestBuilder.build()).execute()
    }


}