package pl.bartoszmech.weather.application.request.validator;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import pl.bartoszmech.weather.application.response.ErrorResponse;
import pl.bartoszmech.weather.domain.weather.InvalidDateException;

import java.util.Locale;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@ControllerAdvice
@AllArgsConstructor
@Log4j2
public class ValidateRequestHandler {

    MessageSource messageSource;

    @ExceptionHandler(InvalidDateFormatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDateFormatException(InvalidDateFormatException e) {
        String message = getLocateMessage("date.bad_format");
        return ResponseEntity.status(BAD_REQUEST).body(new ErrorResponse(message));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleDateNotProvidedException(MissingServletRequestParameterException e) {
        String message = getLocateMessage("date.not.provided");
        return ResponseEntity.status(BAD_REQUEST).body(new ErrorResponse(message));
    }

    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<ErrorResponse> handleDateOutOfRange(InvalidDateException e) {
        String message = getLocateMessage("date.not.range");
        return ResponseEntity.status(BAD_REQUEST).body(new ErrorResponse(message));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleUnavailableServiceException(ResponseStatusException e) {
        String message = getLocateMessage("service.unavailable");
        return ResponseEntity.status(SERVICE_UNAVAILABLE).body(new ErrorResponse(message));
    }

    private String getLocateMessage(String property) {
        Locale locale = LocaleContextHolder.getLocale();
        log.info("Provided locale is: " + locale);
        return messageSource.getMessage(property, null, locale);
    }

}
