package org.ljmu.thesis.commons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHelper {

    public static <T> T getObject(String json, Class<T> obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        return (T) objectMapper.readValue(json, obj);
    }
}
