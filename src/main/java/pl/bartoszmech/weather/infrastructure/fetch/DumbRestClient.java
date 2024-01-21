package pl.bartoszmech.weather.infrastructure.fetch;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.bartoszmech.weather.domain.weather.FetchWeather;
import pl.bartoszmech.weather.infrastructure.utils.UriParser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

@Component
@Profile("dev")
public class DumbRestClient implements FetchWeather {

    private static final double MIN_TEMPERATURE = -10.0;
    private static final double MAX_TEMPERATURE = 35.0;

    private static final double MIN_WIND_SPEED = 1.0;
    private static final double MAX_WIND_SPEED = 30.0;

    @Override
    public List<FetchWeatherResponse> fetchWeather(List<String> urls, String date) {
        Random random = new Random();
        return urls.stream()
                .map(url -> new FetchWeatherResponse(
                        UriParser.extractCityFromUrl(url),
                        List.of(
                                new FetchWeatherResponse.WeatherData(
                                        generateRandomTemperature(random),
                                        generateRandomWindSpeed(random),
                                        date)
                        )
                ))
                .toList();
    }

    private double generateRandomTemperature(Random random) {
        double temperature = MIN_TEMPERATURE + random.nextDouble() * (MAX_TEMPERATURE - MIN_TEMPERATURE);
        return new BigDecimal(temperature).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    private double generateRandomWindSpeed(Random random) {
        double windSpeed = MIN_WIND_SPEED + random.nextDouble() * (MAX_WIND_SPEED - MIN_WIND_SPEED);
        return new BigDecimal(windSpeed).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

}