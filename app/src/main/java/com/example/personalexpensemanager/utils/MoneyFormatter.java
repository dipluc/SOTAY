package com.example.personalexpensemanager.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MoneyFormatter {

    public static String formatMoney(long amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
        return decimalFormat.format(amount) + " ₫";
    }

    public static String formatMoney(double amount) {
        return formatMoney((long) amount);
    }

    public static long parseMoney(String input) {
        if (input == null) return 0;
        String clean = input.replaceAll("[^0-9]", "");
        if (clean.isEmpty()) return 0;
        try {
            return Long.parseLong(clean);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}