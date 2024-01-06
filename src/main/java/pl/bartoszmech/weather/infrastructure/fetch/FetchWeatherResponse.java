package pl.bartoszmech.weather.infrastructure.fetch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FetchWeatherResponse {
    @JsonProperty("city_name")
    String cityName;
    @JsonProperty("data")
    List<WeatherData> data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WeatherData {
        @JsonProperty("temp")
        double temperature;
        @JsonProperty("wind_spd")
        private double windSpd;
        @JsonProperty("datetime")
        private String datetime;
    }
}
