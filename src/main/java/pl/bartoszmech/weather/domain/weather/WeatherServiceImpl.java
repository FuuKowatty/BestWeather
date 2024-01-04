package pl.bartoszmech.weather.domain.weather;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import pl.bartoszmech.weather.application.response.WeatherResponse;
import pl.bartoszmech.weather.infrastructure.fetch.FetchWeatherResponse;
import pl.bartoszmech.weather.infrastructure.fetch.WeatherConfigurationProperties;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class WeatherServiceImpl implements WeatherService {
    public static final String DATE_DONT_MATCH = "Provided date does not match any of the downloaded dates";
    FetchWeather fetcher;
    BestLocationService bestLocationService;
    private final WeatherConfigurationProperties properties;
    public WeatherResponse getBestLocation(String date) {
        List<URI> urls = generateUrls();
        List<FetchWeatherResponse> fetchWeather = fetcher.fetchWeather(urls);
        if (checkIfAnyFetchedDateMatchesClientDate(date, fetchWeather)) {
            throw new InvalidDateException(DATE_DONT_MATCH);
        }
        return bestLocationService.findBestLocation(filterLocationsByClientDate(date, fetchWeather));
    }

    private List<URI> generateUrls() {
        List<URI> urls = new LinkedList<>();
        String baseUrl = properties.baseUrl();
        String apiKey = properties.apiKey();

        for (String city : properties.cityNames().split(",")) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl);
            UriComponents components = builder.queryParam("city", city.trim())
                    .queryParam("key", apiKey)
                    .build();

            URI url = components.toUri();
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

