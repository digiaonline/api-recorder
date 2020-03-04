package com.digia.apirecorder.recorder.dto

data class StartRecordingSetRequestDTO(val urlsToRecord : Array<UrlToRecord>, val duration : Long, val start : String?, val name : String?, val globalParameters : Array<ParametersDTO>?) {
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

data class UrlToRecord(val url : String, val parameters : Array<ParametersDTO>?, val period : Int, val body : String?, val headers : Map<String, List<String>>?, val method : String = "GET", val feedItemPath : String?) {
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
        if (feedItemPath != other.feedItemPath) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + (parameters?.contentHashCode() ?: 0)
        result = 31 * result + period
        return result
    }
}

data class ParametersDTO(val name: String, val values : Array<String>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ParametersDTO

        if (name != other.name) return false
        if (!values.contentEquals(other.values)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + values.contentHashCode()
        return result
    }
}