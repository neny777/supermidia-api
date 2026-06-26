package br.com.supermidia.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {

    public ObjectMapperConfig(ObjectMapper objectMapper) {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new EmptyStringToNullDeserializer());
        objectMapper.registerModule(module);
    }
}
