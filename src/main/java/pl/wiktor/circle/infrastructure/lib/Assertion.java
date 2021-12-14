package pl.wiktor.circle.infrastructure.lib;

import pl.wiktor.circle.domain.exception.ApiException;

import java.util.Objects;
import java.util.function.Supplier;

import static org.apache.logging.log4j.util.Strings.isBlank;

public final class Assertion {
    private Assertion() {
    }

    public static void isBiggerEqualsThan(int requiredMinimumSize, String value, Supplier<ApiException> exception) {
        notEmpty(value, exception);
        isTrue(value.length() >= requiredMinimumSize, exception);
    }

    public static void isTrue(boolean value, Supplier<ApiException> exception) {
        if (!value) {
            throw exception.get();
        }
    }

    public static void notEmpty(String value, Supplier<ApiException> exception) {
        notNull(value, exception);
        if (isBlank(value)) {
            throw exception.get();
        }
    }

    public static void notNull(Object object, Supplier<ApiException> exception) {
        if (Objects.isNull(object)) {
            throw exception.get();
        }
    }
}
