package pl.bartoszmech.weather.application.response;

public record WeatherResponse(
        String location,
        double averageTemperatureInCelsius,
        double windSpeedMetersPerSecond
) {
}
