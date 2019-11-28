package com.digia.apirecorder

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.dom4j.dom.DOMNodeHelper.setPrefix
import org.springframework.beans.factory.annotation.Value
import org.thymeleaf.templateresolver.FileTemplateResolver
import org.thymeleaf.templateresolver.ITemplateResolver


fun main(args: Array<String>) {
    runApplication<RecorderApplication>(*args)
}

@SpringBootApplication
@EnableJpaRepositories
class RecorderApplication{}