package pl.bartoszmech.weather.application.response;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@ControllerAdvice
public class ResponseErrorHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(ResponseStatusException e) {
        return ResponseEntity.status(SERVICE_UNAVAILABLE).body(new ErrorResponse(e.getMessage()));
    }

}
