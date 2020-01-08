package com.digia.apirecorder.recorder.persistence

import javax.persistence.Converter
import java.io.IOException
import java.util.HashMap
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import javax.persistence.AttributeConverter


@Converter
class MapToStringConverter : AttributeConverter<Map<String, List<String>>, String> {
    private var mapper = ObjectMapper()

    override fun convertToDatabaseColumn(data: Map<String, List<String>>?): String {
        var value = ""
        if(data == null || data.isEmpty()){
            return value
        }
        try {
            value = mapper.writeValueAsString(data)
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }

        return value
    }

    override fun convertToEntityAttribute(data: String?): Map<String, List<String>>? {
        if(data == null || data.isEmpty()) return null
        var mapValue: Map<String, List<String>> = HashMap()
        val typeRef = object : TypeReference<HashMap<String, List<String>>>() {

        }
        try {
            mapValue = mapper.readValue<Map<String, List<String>>>(data, typeRef)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return mapValue
    }

}