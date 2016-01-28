package com.mlizhi.utils;

import java.util.regex.Pattern;

public class Utils {
    public static boolean matchPhoneNumber(String phoneNumber) {
        return Pattern.compile("^(1)\\d{10}$").matcher(phoneNumber).find();
    }

    public static boolean matchEmail(String phoneNumber) {
        return Pattern.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$").matcher(phoneNumber).matches();
    }
}
