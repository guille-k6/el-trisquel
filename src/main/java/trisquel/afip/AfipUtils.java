package trisquel.afip;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AfipUtils {

    public AfipUtils() {
    }

    public static String toAfipDateFormat(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return date.format(formatter);
    }

    public static String toAfipNumberFormat(BigDecimal value) {
        if (value == null) {
            return "0.00";
        }
        // 2 decimales fijos y siempre en formato plano (sin E+6)
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

}
