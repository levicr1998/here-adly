package com.here.adly.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.here.adly.models.User;

public class SessionManager {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String SHARED_SESSION_NAME = "login";
    String SESSION_KEY = "session_user";

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(SHARED_SESSION_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void saveSession(User user) {
        String id = user.id;
        editor.putString(SESSION_KEY, id).commit();
    }

    public String getSession() {
        String noSession = "none";
        return preferences.getString(SESSION_KEY, noSession);
    }

    public void removeSession() {
        editor.putString(SESSION_KEY, "none");
    }
}
