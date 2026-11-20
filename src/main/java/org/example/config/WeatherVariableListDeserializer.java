package org.example.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.example.mete.WeatherVariable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WeatherVariableListDeserializer extends JsonDeserializer<List<WeatherVariable>> {
    @Override
    public List<WeatherVariable> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        List<WeatherVariable> result = new ArrayList<>();

        // 如果传的是 JSON 数组 ["A", "B"]
        if (p.currentToken() == JsonToken.START_ARRAY) {
            while (p.nextToken() != JsonToken.END_ARRAY) {
                result.add(WeatherVariable.fromString(p.getValueAsString()));
            }
        }
        // 如果传的是逗号分隔的字符串 "A,B"
        else if (p.currentToken() == JsonToken.VALUE_STRING) {
            String value = p.getValueAsString();
            if (value != null && !value.trim().isEmpty()) {
                result.addAll(Arrays.stream(value.split(","))
                        .map(String::trim)
                        .map(WeatherVariable::fromString)
                        .collect(Collectors.toList()));
            }
        }

        return result.isEmpty() ? new ArrayList<>(WeatherVariable.DEFAULT_VARIABLES) : result;
    }
}