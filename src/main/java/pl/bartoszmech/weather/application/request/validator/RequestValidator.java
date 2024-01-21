package pl.bartoszmech.weather.application.request.validator;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RequestValidator {

    public static final String YEAR_MONTH_DAY_PATTERN = "\\d{4}-\\d{2}-\\d{2}";

    public static void validateDateFormat(String date) {
        if(!date.matches(YEAR_MONTH_DAY_PATTERN)) {
            throw new InvalidDateFormatException();
        }
    }

}
