package com.pwc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pwc.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Slf4j
@Configuration
public class CommonConfig {

    /**
     * Inject a MessageSource bean for i18n
     * @return
     */
    @Bean
    protected MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("strings");
        return messageSource;
    }

    /**
     * Inject an ObjectMapper bean for json serialise/deserialise
     * @return
     */
    @Bean
    protected ObjectMapper objectMapper() {
        log.debug("ObjectMapper bean has been created!");
        return JsonUtils.getObjectMapper();
    }
}
