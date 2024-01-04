package pl.bartoszmech.weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pl.bartoszmech.weather.infrastructure.fetch.WeatherConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(WeatherConfigurationProperties.class)
public class WeatherApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherApplication.class, args);
	}

}
