package com.digia.apirecorder

import com.digia.apirecorder.recorder.persistence.Record
import com.digia.apirecorder.recorder.persistence.RecordRepository
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import javax.sql.DataSource

/*
@RunWith(SpringRunner::class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DbTest @Autowired constructor(private val dataSource : DataSource, private val recordRepository: RecordRepository) {

    @Test
    public fun testPersistence(){
        val record = Record(null, "abcde","name", "", LocalDateTime.now(),  null)
        recordRepository.save(record)
    }
}
*/
