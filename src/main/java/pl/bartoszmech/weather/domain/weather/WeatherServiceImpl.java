package pl.bartoszmech.weather.domain.weather;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import pl.bartoszmech.weather.application.response.WeatherResponse;
import pl.bartoszmech.weather.infrastructure.fetch.FetchWeatherResponse;
import pl.bartoszmech.weather.infrastructure.fetch.WeatherConfigurationProperties;

import java.net.URI;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Log4j2
public class WeatherServiceImpl implements WeatherService {

    private final FetchWeather fetcher;
    private final BestLocationService bestLocationService;
    private final WeatherConfigurationProperties properties;

    public WeatherResponse getBestLocation(String date) {
        List<String> urls = generateUrls();
        List<FetchWeatherResponse> fetchWeather = fetcher.fetchWeather(urls);
        if (checkIfAnyFetchedDateMatchesClientDate(date, fetchWeather)) {
            throw new InvalidDateException();
        }
        return bestLocationService.findBestLocation(filterLocationsByClientDate(date, fetchWeather));
    }

    private List<String> generateUrls() {
        List<String> urls = new LinkedList<>();
        String baseUrl = properties.baseUrl();
        String apiKey = properties.apiKey();
        for (String city : properties.cityNames().split(",")) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl);
            UriComponents components = builder.queryParam("city", city.trim())
                    .queryParam("key", apiKey)
                    .build();

            log.info("generated url " + components.toUri());
            String url = components.toUriString();
            urls.add(url);
        }
        return urls;
    }

    private List<FetchWeatherResponse> filterLocationsByClientDate(String date, List<FetchWeatherResponse> fetchWeather) {
        return fetchWeather.stream()
            .map(location -> new FetchWeatherResponse(
                    location.getCityName(),
                    filterByClientDate(location.getData(), date)))
            .toList();
    }

    private List<FetchWeatherResponse.WeatherData> filterByClientDate(List<FetchWeatherResponse.WeatherData> weatherData, String date) {
        return weatherData.stream().filter(data -> Objects.equals(data.getDatetime(), date)).toList();
    }

    private boolean checkIfAnyFetchedDateMatchesClientDate(String date, List<FetchWeatherResponse> fetchWeather) {
        return fetchWeather.stream().noneMatch(location -> isClientDateMatch(location, date));
    }

    private boolean isClientDateMatch(FetchWeatherResponse fetchedWeathers, String date) {
        List<FetchWeatherResponse.WeatherData> weatherData = fetchedWeathers.getData();
        return weatherData.stream()
                .anyMatch(data -> data.getDatetime().equals(date));
    }

}

