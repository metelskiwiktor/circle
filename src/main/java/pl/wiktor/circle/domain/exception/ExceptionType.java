package pl.wiktor.circle.domain.exception;

public enum ExceptionType {
    UNEXPECTED_FILE_NAME("Niespodziewana nazwa pliku: '%s'"),
    FAILED_PARSING_FILENAME("Wystąpił błąd podczas parsowania nazwy pliku: '%s'"),
    LISTING_FILES_ERROR("Wystąpił błąd podczas wczytywania listy plików"),
    GOOGLE_DRIVE_API_FAIL("Wystąpił błąd w połączeniu Google Drive API: '%s'"),
    GOOGLE_CREDENTIALS_FAIL("Problem z zainicjalizowaniem Google Credentials"),
    GOOGLE_DRIVE_INIT_FAIL("Problem z zainicjalizowaniem Google Drive"),
    NICK_IS_NULL("Nie podano nazwy użytkownika", 400),
    NICK_IS_TOO_SHORT("Nick '%s' posiada za mało liter", 400),
    REWARD_IS_NULL("Nie podano nazwy nagrody", 400),
    REWARD_IS_TOO_SHORT("Nagroda '%s' posiada za mało liter", 400),
    CONTESTANT_NOT_FOUND("Uczestnik o id '%s' nie został znaleziony", 400);

    private final String exceptionMessage;
    private int httpErrorCode;

    ExceptionType(String exceptionMessage, int httpErrorCode) {
        this.exceptionMessage = exceptionMessage;
        this.httpErrorCode = httpErrorCode;
    }

    ExceptionType(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public int getHttpErrorCode() {
        return httpErrorCode;
    }
}
