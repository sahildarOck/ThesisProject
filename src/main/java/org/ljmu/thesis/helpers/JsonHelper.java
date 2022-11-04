package org.ljmu.thesis.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonHelper {
    public static <T> T getObject(String json, Class<T> obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return (T) objectMapper.readValue(json, obj);
    }

    public static <T> T getObject(File file, Class<T> obj) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return (T) objectMapper.readValue(file, obj);
    }

    public static List<String> getFieldNames(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(json);

        Iterator<String> iterator = rootNode.fieldNames();
        List<String> fieldNames = new ArrayList<>();

        while (iterator.hasNext()) {
            fieldNames.add(iterator.next());
        }
        return fieldNames;
    }

    public static String getJsonPrettyString(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }
}
