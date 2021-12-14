package pl.wiktor.circle.domain.exception;

public class ApplicationException extends RuntimeException {
    private final String message;

    public ApplicationException(ExceptionType exceptionType, Object... messageArgs) {
        this.message = String.format(exceptionType.getExceptionMessage(), messageArgs);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
