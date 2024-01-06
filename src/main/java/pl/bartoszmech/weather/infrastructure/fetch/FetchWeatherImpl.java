package pl.bartoszmech.weather.infrastructure.fetch;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import pl.bartoszmech.weather.domain.weather.FetchWeather;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
@AllArgsConstructor
@Log4j2
public class FetchWeatherImpl implements FetchWeather {
    RestTemplate restTemplate;
    @Override
    public List<FetchWeatherResponse> fetchWeather(List<String> urls) {
        final HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(createHeader());
        List<FetchWeatherResponse> fetchedWeathers = new LinkedList<>();
        try {
            for (String uri : urls) {
                FetchWeatherResponse fetchedWeather = makeWeatherRequest(requestEntity, uri);
                if(fetchedWeather == null) {
                    log.error("Response body was null.");
                    throw new ResponseStatusException(NO_CONTENT);
                }
                fetchedWeathers.add(fetchedWeather);
            }
            return fetchedWeathers;
        } catch (ResourceAccessException e) {
            log.error("Error while fetching locations: " + e.getMessage());
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR);
        }
    }

    private FetchWeatherResponse makeWeatherRequest(HttpEntity<HttpHeaders> requestEntity, String url) {
        ResponseEntity<FetchWeatherResponse> response = restTemplate.exchange(
                url,
                GET,
                requestEntity,
                new ParameterizedTypeReference<>() {
                });
        log.info("data downloaded successfully");
        return response.getBody();
    }

    private HttpHeaders createHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        return headers;
    }
}
