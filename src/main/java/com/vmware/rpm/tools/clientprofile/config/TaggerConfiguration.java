package com.vmware.rpm.tools.clientprofile.config;

import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.Module;

@Configuration
public class TaggerConfiguration {
    @Bean
    public Module jsonNullableModule() {
        return new JsonNullableModule();
    }
}
