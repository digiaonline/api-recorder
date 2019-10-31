package com.digia.apirecorder.recorder

import mu.KotlinLogging
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.net.URI
import java.net.URL

@Service
class DataReaderService {

    private val okHttpClient = OkHttpClient()
    private val log = KotlinLogging.logger {}

    fun read(url : String, headers : Map<String,String>?) : Response {
        log.info("Reading data from $url")
        val requestBuilder = Request.Builder().url(url)
        headers?.entries?.forEach { entry ->
            requestBuilder.addHeader(entry.key, entry.value)
        }
        return okHttpClient.newCall(requestBuilder.build()).execute()
    }


}