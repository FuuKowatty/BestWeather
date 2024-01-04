package pl.bartoszmech.weather.domain.weather;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bartoszmech.weather.application.response.WeatherResponse;
import pl.bartoszmech.weather.infrastructure.fetch.FetchWeatherResponse;

import java.util.List;

@Service
@AllArgsConstructor
public class WeatherServiceImpl implements WeatherService {
    public static final String DATE_DONT_MATCH = "Provided date does not match any of the downloaded dates";
    FetchWeather fetcher;
    BestLocationService bestLocationService;
    public WeatherResponse getBestLocation(String date) {
        String[] urls = {
            "https://api.weatherbit.io/v2.0/forecast/daily?city=Jastarnia&key=62b8eaf750a048359a1df467b11c688a",
            "https://api.weatherbit.io/v2.0/forecast/daily?city=Bridgetown&key=62b8eaf750a048359a1df467b11c688a",
            "https://api.weatherbit.io/v2.0/forecast/daily?city=Fortaleza&key=62b8eaf750a048359a1df467b11c688a",
            "https://api.weatherbit.io/v2.0/forecast/daily?city=Pisouri&key=62b8eaf750a048359a1df467b11c688a",
            "https://api.weatherbit.io/v2.0/forecast/daily?city=Le%20Morne&key=62b8eaf750a048359a1df467b11c688a"
        };
        List<FetchWeatherResponse> fetchWeather = fetcher.fetchWeather(urls);
        if (checkIfAnyMatchClientDate(date, fetchWeather)) {
            throw new InvalidDateException(DATE_DONT_MATCH);
        }
        return bestLocationService.findBestLocation(fetchWeather);
    }
    private boolean checkIfAnyMatchClientDate(String date, List<FetchWeatherResponse> fetchWeather) {
        return fetchWeather.stream().noneMatch(location -> isClientDateMatch(location, date));
    }
    private boolean isClientDateMatch(FetchWeatherResponse fetchedWeathers, String date) {
        List<FetchWeatherResponse.WeatherData> weatherData = fetchedWeathers.getData();
        return weatherData.stream()
                .anyMatch(data -> data.getDatetime().equals(date));
    }

}
