package pl.bartoszmech.weather.application.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartoszmech.weather.application.response.WeatherResponse;
import pl.bartoszmech.weather.domain.weather.WeatherService;

@RestController
@AllArgsConstructor
public class WeatherController {

    WeatherService weatherService;

    @GetMapping( "/api/best-weather")
    public WeatherResponse getWeather() {

        weatherService.getBestLocation();

        return null;
    }
}
