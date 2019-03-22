package com.peihou.willgood2.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UUID {


    public static String getUUID(Context context){
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        String identity = preference.getString("identity", null);
        if (identity == null) {
            identity = java.util.UUID.randomUUID().toString();
            preference.edit().putString("identity", identity);
        }
        return identity;
    }

}
