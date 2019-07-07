package com.djhs16.utils;

import java.util.ArrayList;
import java.util.Map;

public class TextUtils {
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static boolean isValidEmail(final String hex) {
        return hex.matches(EMAIL_PATTERN);
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
        } catch (Exception e) {
        }
        return false;
    }

    public static int parseNullSafeInteger(String numberString) {
        int number = 0;
        try {
            Integer.parseInt(numberString);
        } catch (NumberFormatException e) {
        } catch (Exception e) {
        }
        return number;
    }

    public static String nullString(String nullableString) {
        if (nullableString == null) return "";
        else return nullableString;
    }

    public static <T> boolean isNull(T t) {
        return t == null;
    }

    public static boolean isNullOrEmpty(String s) {
        return (s == null) || (s.length() == 0) || (s.equalsIgnoreCase("null"));
    }

    public static <T> boolean isNullOrEmpty(ArrayList<T> a) {
        return (a == null) || (a.size() == 0);
    }

    public static <T, Y> boolean isNullOrEmpty(Map<T, Y> m) {
        return (m == null) || (m.size() == 0);
    }
}
