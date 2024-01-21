package pl.bartoszmech.weather.infrastructure.fetch;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import pl.bartoszmech.weather.domain.weather.FetchWeather;
import pl.bartoszmech.weather.domain.weather.InvalidDateException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
@AllArgsConstructor
@Log4j2
@Profile("!dev")
public class FetchWeatherImpl implements FetchWeather {

    private final WebClient webClient;

    @Override
    public List<FetchWeatherResponse> fetchWeather(List<String> urls, String date) {
        List<FetchWeatherResponse> fetchedWeathers;
        try {
            fetchedWeathers = handleFetchWeather(urls);
        } catch (Exception e) {
            log.error("Error while fetching locations: " + e.getMessage());
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR);
        }

        if (checkIfAnyFetchedDateMatchesClientDate(date, fetchedWeathers)) {
            throw new InvalidDateException();
        }

        return fetchedWeathers;
    }

    private List<FetchWeatherResponse> handleFetchWeather(List<String> urls) {
        return Flux.fromIterable(urls)
                .flatMap(this::makeWeatherRequest)
                .collectList()
                .block();
        }

    private Mono<FetchWeatherResponse> makeWeatherRequest(String url) {
        return webClient.get()
                .uri(url)
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono(FetchWeatherResponse.class)
                .doOnSuccess(response -> log.info("data downloaded successfully"));
    }

    private boolean checkIfAnyFetchedDateMatchesClientDate(String date, List<FetchWeatherResponse> fetchWeather) {
        return fetchWeather.stream()
                .noneMatch(location -> isClientDateMatch(location, date));
    }

    private boolean isClientDateMatch(FetchWeatherResponse fetchedWeathers, String date) {
        List<FetchWeatherResponse.WeatherData> weatherData = fetchedWeathers.getData();
        return weatherData.stream()
                .anyMatch(data -> data.getDatetime().equals(date));
    }

}
