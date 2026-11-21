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

/**
 * 兼容两种入参写法：
 * <ul>
 *     <li>JSON 数组：["temperature_2m", "wind_speed_10m"]</li>
 *     <li>逗号分隔字符串："temperature_2m,wind_speed_10m"</li>
 * </ul>
 *
 * @author windy
 */
public class WeatherVariableListDeserializer extends JsonDeserializer<List<WeatherVariable>> {

    @Override
    public List<WeatherVariable> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        List<WeatherVariable> result = new ArrayList<>();

        if (parser.currentToken() == JsonToken.START_ARRAY) {
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                result.add(WeatherVariable.fromString(parser.getValueAsString()));
            }
        } else if (parser.currentToken() == JsonToken.VALUE_STRING) {
            String value = parser.getValueAsString();
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
