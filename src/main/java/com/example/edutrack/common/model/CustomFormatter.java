package com.example.edutrack.common.model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class CustomFormatter {
    private CustomFormatter() {

    }

    private static String getNumberFormatPattern(boolean includeSign, boolean isInteger) {
        String pattern = includeSign ? "+#,###" : "#,###";
        return isInteger ? pattern : pattern + ".##";
    }

    public static String formatNumberWithSpaces(Number number) {
        return formatNumberWithSpaces(number, false);
    }

    public static String formatNumberWithSpaces(Number number, boolean includeSign) {
        if (number == null) {
            return "";
        }

        // Set custom symbols for space as grouping separator
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator(' ');

        String pattern = getNumberFormatPattern(
                includeSign,
                number instanceof Integer || number instanceof Long
        );

        DecimalFormat formatter = new DecimalFormat(pattern, symbols);
        formatter.setGroupingUsed(true);

        return formatter.format(number);
    }
}
