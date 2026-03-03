package com.project.URLShortener.utils;

import static java.util.Collections.reverse;

public class Base62Encoder {
    private static final String BASE62 =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static String encode(Long id)
    {
        StringBuilder sb=new StringBuilder();
        while(id > 0)
        {
            sb.append(BASE62.charAt((int)(id%62)));
            id=id/62;
        }
        return sb.reverse().toString();
    }
}