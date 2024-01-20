package pl.bartoszmech.weather.infrastructure.fetch;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import pl.bartoszmech.weather.domain.weather.FetchWeather;

import java.util.LinkedList;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
@AllArgsConstructor
@Log4j2
public class FetchWeatherImpl implements FetchWeather {

    private final WebClient webClient;

    @Override
    public List<FetchWeatherResponse> fetchWeather(List<String> urls) {
        List<FetchWeatherResponse> fetchedWeathers = new LinkedList<>();
        try {
            urls.forEach(url -> {
                FetchWeatherResponse fetchedWeather = makeWeatherRequest(url);
                if(fetchedWeather == null) {
                    log.error("Response body was null.");
                    throw new ResponseStatusException(NO_CONTENT);
                }
                fetchedWeathers.add(fetchedWeather);
            });
            return fetchedWeathers;
        } catch (ResourceAccessException e) {
            log.error("Error while fetching locations: " + e.getMessage());
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR);
        }
    }

    private FetchWeatherResponse makeWeatherRequest(String url) {

        FetchWeatherResponse response = webClient.get()
                .uri(url)
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono(FetchWeatherResponse.class)
                .block();

        log.info("data downloaded successfully");
        return response;
    }

}
