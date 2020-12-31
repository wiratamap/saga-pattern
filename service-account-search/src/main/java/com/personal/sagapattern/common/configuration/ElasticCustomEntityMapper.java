package com.personal.sagapattern.common.configuration;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.EntityMapper;

public class ElasticCustomEntityMapper implements EntityMapper {

    private ObjectMapper mapper;

    @Autowired 
    public ElasticCustomEntityMapper() {
        this.mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
    }

    @Override
    public String mapToString(Object object) throws IOException {
        return mapper.writeValueAsString(object);
    }

    @Override
    public <T> T mapToObject(String source, Class<T> clazz) throws IOException {
        return mapper.readValue(source, clazz);
    }

    @Override
    public Map<String, Object> mapObject(Object source) {
        return null;
    }

    @Override
    public <T> T readObject(Map<String, Object> source, Class<T> targetType) {
        return null;
    }

}