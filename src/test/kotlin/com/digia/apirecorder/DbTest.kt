package com.digia.apirecorder

import com.digia.apirecorder.recorder.persistence.Record
import com.digia.apirecorder.recorder.persistence.RecordRepository
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDateTime
import javax.sql.DataSource

@RunWith(SpringRunner::class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DbTest @Autowired constructor(private val dataSource : DataSource, private val recordRepository: RecordRepository) {

    @Test
    public fun testPersistence(){
        val record = Record(null, "abcde", LocalDateTime.now(), null)
        recordRepository.save(record)
    }
}