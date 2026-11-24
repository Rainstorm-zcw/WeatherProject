package org.example.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 兼容三种经纬度入参写法：
 * <ul>
 *     <li>JSON 数组：[39.9, 31.2]</li>
 *     <li>逗号分隔字符串："39.9,31.2"</li>
 *     <li>单个数字：39.9</li>
 * </ul>
 *
 * @author windy
 */
public class DoubleListDeserializer extends JsonDeserializer<List<Double>> {

    @Override
    public List<Double> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken() == JsonToken.START_ARRAY) {
            List<Double> result = new ArrayList<>();
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                addValue(result, parser);
            }
            return result;
        }
        if (parser.currentToken() == JsonToken.VALUE_STRING) {
            String value = parser.getValueAsString();
            if (value == null || value.trim().isEmpty()) {
                return new ArrayList<>();
            }
            return Arrays.stream(value.split(","))
                    .map(String::trim)
                    .filter(item -> !item.isEmpty())
                    .map(Double::valueOf)
                    .collect(Collectors.toList());
        }
        List<Double> result = new ArrayList<>();
        addValue(result, parser);
        return result;
    }

    private void addValue(List<Double> result, JsonParser parser) throws IOException {
        if (parser.currentToken() == JsonToken.VALUE_NUMBER_INT
                || parser.currentToken() == JsonToken.VALUE_NUMBER_FLOAT) {
            result.add(parser.getDoubleValue());
        } else if (parser.currentToken() == JsonToken.VALUE_STRING) {
            String value = parser.getValueAsString();
            if (value != null && !value.trim().isEmpty()) {
                result.add(Double.valueOf(value.trim()));
            }
        }
    }
}
