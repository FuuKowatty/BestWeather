package pl.bartoszmech.weather.domain.weather;

import org.springframework.stereotype.Component;
import pl.bartoszmech.weather.application.response.WeatherResponse;
import pl.bartoszmech.weather.infrastructure.fetch.FetchWeatherResponse;

import java.util.List;

@Component
public class BestLocationService {

    public static final int MINIMUM_WIND_SPEED_FOR_BEST_LOCATION = 5;
    public static final int MAXIMUM_WIND_SPEED_FOR_BEST_LOCATION = 18;
    public static final int MINIMUM_TEMPERATURE_FOR_BEST_LOCATION = 5;
    public static final int MAXIMUM_TEMPERATURE_FOR_BEST_LOCATION = 35;

    public WeatherResponse findBestLocation(List<FetchWeatherResponse> locations) {
        List<FetchWeatherResponse> bestLocations = checkIfAnyGoodLocationExists(locations);
        if(bestLocations.isEmpty()) {
            return null;
        }

        if(bestLocations.size() == 1) {
            return WeatherMapper.mapFetchedResponseToApiResponse(bestLocations.get(0));
        }

        return WeatherMapper.mapFetchedResponseToApiResponse(calculateBestLocation(bestLocations));
    }

    private FetchWeatherResponse calculateBestLocation(List<FetchWeatherResponse> bestLocations) {

        return bestLocations.stream()
                .reduce( (acc, v) -> {
                    double windSpeed = v.getData().get(0).getWindSpd();
                    double temp = v.getData().get(0).getTemperature();

                    double currentBestLocationRatio = 3 * acc.getData().get(0).getWindSpd() + acc.getData().get(0).getTemperature();
                    double proposedBestLocationRatio = 3 * windSpeed + temp;

                    return proposedBestLocationRatio > currentBestLocationRatio ? v : acc;
                }).orElse(null);
    }

    private List<FetchWeatherResponse> checkIfAnyGoodLocationExists(List<FetchWeatherResponse> locations) {
        return locations.stream().filter(location -> {
                    FetchWeatherResponse.WeatherData weatherData = location.getData().get(0);
                    return checkWindSpeed(weatherData.getWindSpd()) && checkTemperature(weatherData.getTemperature());
                })
                .toList();
    }

    private boolean checkWindSpeed(double windSpeed) {
        return !(windSpeed < MINIMUM_WIND_SPEED_FOR_BEST_LOCATION) && !(windSpeed > MAXIMUM_WIND_SPEED_FOR_BEST_LOCATION);
    }

    private boolean checkTemperature(double temperature) {
        return !(temperature < MINIMUM_TEMPERATURE_FOR_BEST_LOCATION) && !(temperature > MAXIMUM_TEMPERATURE_FOR_BEST_LOCATION);
    }
}
