package org.ljmu.thesis.commons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonHelper {

    public static <T> T getObject(String json, Class<T> obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return (T) objectMapper.readValue(json, obj);
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
}
