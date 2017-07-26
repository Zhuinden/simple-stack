package com.zhuinden.simplestackexamplemvvm.util;

/**
 * Created by Owner on 2017. 07. 25..
 */

public class Strings {
    public static boolean isNullOrEmpty(String string) {
        return string == null || "".equals(string);
    }

    public static String join(String[] strings) {
        return join(strings, ", ");
    }

    public static String join(String[] strings, String separator) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0, size = strings.length; i < size; i++) {
            stringBuilder.append(strings[i]);
            if(i < strings.length - 1) {
                stringBuilder.append(separator);
            }
        }
        return stringBuilder.toString();
    }
}
