package com.digia.apirecorder.recorder.persistence

import javax.persistence.Converter
import java.io.IOException
import java.util.HashMap
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import javax.persistence.AttributeConverter


@Converter
class MapToStringConverter : AttributeConverter<Map<String, String>, String> {
    private var mapper = ObjectMapper()

    override fun convertToDatabaseColumn(data: Map<String, String>?): String {
        var value = ""
        if(data == null){
            return value
        }
        try {
            value = mapper.writeValueAsString(data)
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }

        return value
    }

    override fun convertToEntityAttribute(data: String?): Map<String, String>? {
        if(data == null) return null
        var mapValue: Map<String, String> = HashMap()
        val typeRef = object : TypeReference<HashMap<String, Any>>() {

        }
        try {
            mapValue = mapper.readValue<Map<String, String>>(data, typeRef)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return mapValue
    }

}