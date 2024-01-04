package pl.bartoszmech.weather.infrastructure.fetch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Data
public class FetchWeatherResponse {
    @JsonProperty("city_name")
    String cityName;
    @JsonProperty("data")
    List<WeatherData> data;

    @Getter
    public static class WeatherData {
        @JsonProperty("temp")
        double temperature;
        @JsonProperty("wind_spd")
        private double windSpd;
        @JsonProperty("datetime")
        private String datetime;
    }
}
