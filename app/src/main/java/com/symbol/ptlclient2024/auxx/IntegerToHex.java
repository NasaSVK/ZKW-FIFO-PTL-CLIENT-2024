package com.symbol.ptlclient2024.auxx;

public class IntegerToHex {
    static final String digits = "0123456789ABCDEF";
    public static String integerToHex(long input) {
        if (input <= 0)
            return "0";
        StringBuilder hex = new StringBuilder();
        while (input > 0) {
            long digit = input % 16;
            hex.insert(0, digits.charAt((int)digit));
            input = input / 16;
        }
        return hex.toString();
    }
}
