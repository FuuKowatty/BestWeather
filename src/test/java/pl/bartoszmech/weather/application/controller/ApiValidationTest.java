package pl.bartoszmech.weather.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.bartoszmech.weather.application.response.ErrorResponse;

import static com.github.tomakehurst.wiremock.common.ContentTypes.APPLICATION_JSON;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiValidationTest {

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

    @BeforeEach
    public void setupWireMockResponse() {
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
    }

    @Test
    public void should_return_bad_request_when_provide_date_which_not_existing_in_api() throws Exception{
        //given
        //when
        mockMvc.perform(get("/api/best-weather")
                        .queryParam("date", "2023-01-05")
                        .contentType(APPLICATION_JSON_VALUE))
                //then
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void should_return_bad_request_when_provide_invalid_date_format() throws Exception{
        //given
        //when
        MvcResult response = mockMvc.perform(get("/api/best-weather")
                        .queryParam("date", "01.05.2024")
                        .contentType(APPLICATION_JSON_VALUE))
                //then
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse  errorResponse = objectMapper.readValue(response.getResponse().getContentAsString(), ErrorResponse.class);
        Assertions.assertEquals(
                "Provided date format is invalid. Please make sure it is followed by \"YYYY-MM-DD\"",
                errorResponse.message()
        );
    }

    @Test
    public void should_return_bad_request_when_not_provide_date() throws Exception {
        //given
        //when
        MvcResult response = mockMvc.perform(get("/api/best-weather")
                        .contentType(APPLICATION_JSON_VALUE))
                //then
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse  errorResponse = objectMapper.readValue(response.getResponse().getContentAsString(), ErrorResponse.class);
        Assertions.assertEquals(
                "Required request parameter 'date' for method parameter type String is not present",
                errorResponse.message()
        );
    }
}
