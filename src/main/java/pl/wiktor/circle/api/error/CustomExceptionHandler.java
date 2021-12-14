package pl.wiktor.circle.api.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.wiktor.circle.domain.exception.ApiException;

import java.time.Clock;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
public class CustomExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);
    private final DateTimeFormatter formatter;
    private final Clock clock;

    public CustomExceptionHandler(DateTimeFormatter formatter, Clock clock) {
        this.formatter = formatter;
        this.clock = clock;
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Error> handleDomainException(ApiException exception) {
        logger.error("API Domain error occurred:", exception);
        exception.printStackTrace();

        HttpStatus httpStatus = HttpStatus.valueOf(exception.getHttpCode());
        Error body = new Error(httpStatus.value(), exception.getMessage(), formatter.format(clock.instant()));

        return new ResponseEntity<>(body, httpStatus);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleException(Exception exception) {
        logger.error("Internal server error occurred:", exception);
        exception.printStackTrace();

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        Error body = new Error(httpStatus.value(), "Nieznany błąd", formatter.format(clock.instant()));

        return new ResponseEntity<>(body, httpStatus);
    }

    private static class Error {
        private final int status;
        private final String message;
        private final String timestamp;

        private Error(int status, String message, String timestamp) {
            this.status = status;
            this.message = message;
            this.timestamp = timestamp;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }
}
