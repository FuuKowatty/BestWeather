package pl.bartoszmech.weather.application.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
public record WeatherResponse(
    String location,
    double averageTemperatureInCelsius,
    double windSpeedMetersPerSecond
){}

