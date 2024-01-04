package pl.bartoszmech.weather.infrastructure.fetch;

import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.server.ResponseStatusException;

public class RestTemplateResponseErrorHandler extends DefaultResponseErrorHandler {
    public static final String SERVER_SIDE_ERROR = "Error while using http client";
    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        final HttpStatusCode statusCode = httpResponse.getStatusCode();
        if (statusCode.is5xxServerError()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, SERVER_SIDE_ERROR);
        } else if (statusCode.is4xxClientError()) {
            if (statusCode == HttpStatus.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            } else if (statusCode == HttpStatus.UNAUTHORIZED) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }
        }
    }
}
