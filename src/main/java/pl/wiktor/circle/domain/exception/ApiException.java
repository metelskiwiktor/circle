package pl.wiktor.circle.domain.exception;

public class ApiException extends RuntimeException {
    private final String message;
    private final int httpCode;

    public ApiException(ExceptionType exceptionType, Object... messageArgs) {
        this.message = String.format(exceptionType.getExceptionMessage(), messageArgs);
        this.httpCode = exceptionType.getHttpErrorCode();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getHttpCode() {
        return httpCode;
    }
}
