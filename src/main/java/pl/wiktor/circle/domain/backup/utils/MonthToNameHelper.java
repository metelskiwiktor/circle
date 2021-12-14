package pl.wiktor.circle.domain.backup.utils;

import java.time.Month;
import java.util.HashMap;
import java.util.Map;

public class MonthToNameHelper {
    private static final Map<Month, String> polishNames = new HashMap<>();

    static {
        polishNames.put(Month.JANUARY, "styczeń");
        polishNames.put(Month.FEBRUARY, "luty");
        polishNames.put(Month.MARCH, "marzec");
        polishNames.put(Month.APRIL, "kwiecień");
        polishNames.put(Month.MAY, "maj");
        polishNames.put(Month.JUNE, "czerwiec");
        polishNames.put(Month.JULY, "lipiec");
        polishNames.put(Month.AUGUST, "sierpień");
        polishNames.put(Month.SEPTEMBER, "wrzesień");
        polishNames.put(Month.OCTOBER, "październik");
        polishNames.put(Month.NOVEMBER, "listopad");
        polishNames.put(Month.DECEMBER, "grudzień");
    }

    public static String getPolishMonthName(Month month) {
        return polishNames.get(month);
    }
}
