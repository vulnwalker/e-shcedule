package com.eschedule.eclient;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Zund#i on 20/10/2016.
 */

public class Preferences {
    public Context mContext;
    public SharedPreferences pref;
    public SharedPreferences.Editor editor;

    public static final String REG_ID = "regID";

    public Preferences(Context context) {
        mContext = context;
        pref = mContext.getSharedPreferences(App.PREF_NAME, 0);
        editor = pref.edit();
    }

    public void setRegID(String token) {
        editor.putString(REG_ID, token);
        editor.commit();
    }

    public String getRegId() {
        return pref.getString(REG_ID, null);
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }

}
