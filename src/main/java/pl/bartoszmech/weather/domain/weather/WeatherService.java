package pl.bartoszmech.weather.domain.weather;

import pl.bartoszmech.weather.application.response.WeatherResponse;

public interface WeatherService {
    WeatherResponse getBestLocation(String date);
}
