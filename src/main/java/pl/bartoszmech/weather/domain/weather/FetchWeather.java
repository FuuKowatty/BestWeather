package pl.bartoszmech.weather.domain.weather;

import pl.bartoszmech.weather.infrastructure.fetch.FetchWeatherResponse;

import java.net.URI;
import java.util.List;

public interface FetchWeather {
    List<FetchWeatherResponse> fetchWeather(List<URI> urls);
}
