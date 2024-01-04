package pl.bartoszmech.weather.application.request.validator;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.bartoszmech.weather.application.response.ValidationResponse;
import pl.bartoszmech.weather.domain.weather.InvalidDateException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class ValidateRequestHandler {
    @ExceptionHandler(InvalidDateFormatException.class)
    public ResponseEntity<ValidationResponse> handleValidationExceptions(InvalidDateFormatException e) {
        return ResponseEntity.status(BAD_REQUEST).body(new ValidationResponse(e.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ValidationResponse> handleValidationExceptions(MissingServletRequestParameterException e) {
        return ResponseEntity.status(BAD_REQUEST).body(new ValidationResponse(e.getMessage()));
    }

    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<ValidationResponse> handleValidationExceptions(InvalidDateException e) {
        return ResponseEntity.status(BAD_REQUEST).body(new ValidationResponse(e.getMessage()));
    }
}
