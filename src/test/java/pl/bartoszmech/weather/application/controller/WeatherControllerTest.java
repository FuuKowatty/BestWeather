package pl.bartoszmech.weather.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.bartoszmech.weather.application.response.WeatherResponse;
import pl.bartoszmech.weather.infrastructure.fetch.FetchWeatherResponse;

import java.util.Arrays;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.common.ContentTypes.APPLICATION_JSON;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static javax.security.auth.callback.ConfirmationCallback.OK;
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
        FetchWeatherResponse fetchWeatherResponse = new FetchWeatherResponse(
                "Jastarnia",
                Arrays.asList(
                        new FetchWeatherResponse.WeatherData(20.5, 10.2, "2024-01-05"),
                        new FetchWeatherResponse.WeatherData(18.9, 8.6, "2024-01-06")
                )
        );
        String json = objectMapper.writeValueAsString(fetchWeatherResponse);
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/"))
                .withQueryParam("city", WireMock.equalTo("Jastarnia"))
                .withQueryParam("key", WireMock.equalTo("123"))
                .willReturn(aResponse()
                        .withStatus(OK)
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withBody("""
                            {
                            "city_name": "Warsaw",
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
                .andExpect(status().isOk())
                .andReturn();

        System.out.println(objectMapper.readValue(response.getResponse().getContentAsString(), WeatherResponse.class));
    }
}
