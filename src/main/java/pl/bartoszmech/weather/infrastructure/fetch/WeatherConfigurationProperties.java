package pl.bartoszmech.weather.infrastructure.fetch;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "api.weather")
public record WeatherConfigurationProperties(
        String cityNames,
        String apiKey,
        String baseUrl
) {}
