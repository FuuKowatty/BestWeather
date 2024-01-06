package pl.bartoszmech.weather.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import pl.bartoszmech.weather.application.response.ErrorResponse;
import pl.bartoszmech.weather.application.response.WeatherResponse;

import static com.github.tomakehurst.wiremock.common.ContentTypes.APPLICATION_JSON;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import static org.apache.hc.core5.http.HttpStatus.SC_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WeatherControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @RegisterExtension
    public static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();


    @DynamicPropertySource
    public static void propertyOverride(DynamicPropertyRegistry registry) {
        registry.add("api.weather.cityNames", () -> "Jastarnia");
        registry.add("api.weather.apiKey", () -> "123");
        registry.add("api.weather.baseUrl", wireMockServer::baseUrl);
    }



    @Test
    public void should_success_return_best_location() throws Exception{
        //given
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/"))
                .withQueryParam("city", WireMock.equalTo("Jastarnia"))
                .withQueryParam("key", WireMock.equalTo("123"))
                .willReturn(WireMock.aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withBody("""
                            {
                            "city_name": "Jastarnia",
                                "data": [
                                    {
                                        "temp": 20.5,
                                        "wind_spd": 10.2,
                                        "datetime": "2024-01-05"
                                    },
                                    {
                                        "temp": 18.9,
                                        "wind_spd": 8.6,
                                        "datetime": "2024-01-06"
                                    }
                                ]
                            }""".trim())));

        //when
        MvcResult response = mockMvc.perform(get("/api/best-weather")
                        .queryParam("date", "2024-01-05")
                .contentType(APPLICATION_JSON_VALUE))
        //then
                .andExpect(status().isOk())
                .andReturn();
        WeatherResponse weatherResponse = objectMapper.readValue(response.getResponse().getContentAsString(), WeatherResponse.class);
        Assertions.assertEquals(weatherResponse.location(), "Jastarnia");
        Assertions.assertEquals(weatherResponse.averageTemperatureInCelsius(), 20.5);
        Assertions.assertEquals(weatherResponse.windSpeedMetersPerSecond(), 10.2);
    }

    @Test
    public void should_return_service_error_when_external_api_error() throws Exception {
        //given
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/"))
                .withQueryParam("city", WireMock.equalTo("Jastarnia"))
                .withQueryParam("key", WireMock.equalTo("123"))
                .willReturn(WireMock.aResponse()
                        .withStatus(SC_SERVER_ERROR)
                        .withHeader("Content-Type", APPLICATION_JSON)));

        //when
        MvcResult response = mockMvc.perform(get("/api/best-weather")
                        .queryParam("date", "2024-01-05")
                        .contentType(APPLICATION_JSON_VALUE))
                //then
                .andExpect(status().isServiceUnavailable())
                .andReturn();

        ErrorResponse  errorResponse = objectMapper.readValue(response.getResponse().getContentAsString(), ErrorResponse.class);
        Assertions.assertEquals(
                errorResponse.message(),
                "500 INTERNAL_SERVER_ERROR \"Error while using http client\"");
    }
}
