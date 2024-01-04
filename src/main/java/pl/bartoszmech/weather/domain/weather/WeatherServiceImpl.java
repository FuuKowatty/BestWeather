package pl.bartoszmech.weather.domain.weather;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bartoszmech.weather.application.response.WeatherResponse;
import pl.bartoszmech.weather.infrastructure.fetch.FetchWeatherResponse;

import java.util.List;

@Service
@AllArgsConstructor
public class WeatherServiceImpl implements WeatherService {
    FetchWeather fetcher;
    BestLocationService bestLocationService;
    public WeatherResponse getBestLocation() {
        String[] urls = {
            "https://api.weatherbit.io/v2.0/forecast/daily?city=Jastarnia&key=62b8eaf750a048359a1df467b11c688a",
            "https://api.weatherbit.io/v2.0/forecast/daily?city=Bridgetown&key=62b8eaf750a048359a1df467b11c688a",
            "https://api.weatherbit.io/v2.0/forecast/daily?city=Fortaleza&key=62b8eaf750a048359a1df467b11c688a",
            "https://api.weatherbit.io/v2.0/forecast/daily?city=Pisouri&key=62b8eaf750a048359a1df467b11c688a",
            "https://api.weatherbit.io/v2.0/forecast/daily?city=Le%20Morne&key=62b8eaf750a048359a1df467b11c688a"
        };
        List<FetchWeatherResponse> fetchWeather = fetcher.fetchWeather(urls);
        return bestLocationService.findBestLocation(fetchWeather);
    }
}
