package pl.bartoszmech.weather.application.response;

public record WeatherResponse(
        String location,
        String averageTemperatureInCelsius,
        String windSpeedMetersPerSecond
) {
}
