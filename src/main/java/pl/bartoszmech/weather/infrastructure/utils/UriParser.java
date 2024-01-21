package pl.bartoszmech.weather.infrastructure.utils;

import lombok.extern.log4j.Log4j2;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import pl.bartoszmech.weather.infrastructure.fetch.WeatherConfigurationProperties;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Log4j2
public class UriParser {

    public static List<String> generateUrls(WeatherConfigurationProperties properties) {
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

    public static String extractCityFromUrl(String url) {
        Map<String, List<String>> queryParams = UriComponentsBuilder.fromUriString(url).build().getQueryParams();
        return queryParams.get("city").get(0);
    }

}
