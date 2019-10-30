package com.digia.apirecorder.recorder.dto

data class StartRecordingSetRequestDTO(val urlsToRecord : Array<UrlToRecord>, val duration : Long) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StartRecordingSetRequestDTO

        if (!urlsToRecord.contentEquals(other.urlsToRecord)) return false
        if (duration != other.duration) return false

        return true
    }

    override fun hashCode(): Int {
        var result = urlsToRecord.contentHashCode()
        result = 31 * result + duration.hashCode()
        return result
    }
}

data class UrlToRecord(val url : String, val parameters : Array<ParametersDTO>?, val period : Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UrlToRecord

        if (url != other.url) return false
        if (parameters != null) {
            if (other.parameters == null) return false
            if (!parameters.contentEquals(other.parameters)) return false
        } else if (other.parameters != null) return false
        if (period != other.period) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + (parameters?.contentHashCode() ?: 0)
        result = 31 * result + period
        return result
    }
}

data class ParametersDTO(val parameterName: String, val parameterValues : Array<String>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ParametersDTO

        if (parameterName != other.parameterName) return false
        if (!parameterValues.contentEquals(other.parameterValues)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = parameterName.hashCode()
        result = 31 * result + parameterValues.contentHashCode()
        return result
    }
}