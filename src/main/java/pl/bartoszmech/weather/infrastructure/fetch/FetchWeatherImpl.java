package pl.bartoszmech.weather.infrastructure.fetch;

import pl.bartoszmech.weather.domain.weather.FetchWeather;

import java.util.List;

public class FetchWeatherImpl implements FetchWeather {
    @Override
    public List<FetchWeatherResponse> fetchFromLocalizations(List<String> urls) {
        return null;
    }
}
