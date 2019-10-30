package com.digia.apirecorder.utils

import java.sql.Date
import java.time.LocalDate
import javax.persistence.AttributeConverter
import javax.persistence.Converter


@Converter(autoApply = true)
class LocalDateAttributeConverter : AttributeConverter<LocalDate, Date> {

    override fun convertToDatabaseColumn(locDate: LocalDate?): Date? {
        return if (locDate == null) null else Date.valueOf(locDate)
    }

    override fun convertToEntityAttribute(sqlDate: Date?): LocalDate? {
        return sqlDate?.toLocalDate()
    }
}