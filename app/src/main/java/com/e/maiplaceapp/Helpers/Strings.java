package com.e.maiplaceapp.Helpers;

public class Strings {

    public static String capitalize(String str)
    {
        if(str == null) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
