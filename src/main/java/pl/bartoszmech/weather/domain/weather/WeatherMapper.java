package pl.bartoszmech.weather.domain.weather;

import lombok.NoArgsConstructor;
import pl.bartoszmech.weather.application.response.WeatherResponse;
import pl.bartoszmech.weather.infrastructure.fetch.FetchWeatherResponse;

@NoArgsConstructor
public class WeatherMapper {
    public static WeatherResponse mapFetchedResponseToApiResponse(FetchWeatherResponse fetchedWeatherResponse) {
        return new WeatherResponse(
                fetchedWeatherResponse.getCityName(),
                fetchedWeatherResponse.getData().get(0).getTemperature(),
                fetchedWeatherResponse.getData().get(0).getWindSpd()
        );
    }
}
