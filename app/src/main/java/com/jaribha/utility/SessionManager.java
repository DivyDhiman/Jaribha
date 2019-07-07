/**
 * Singleton class responsible and used to save and retrieve data from shared prefrence
 *
 * @author Kailash Chouhan
 */

package com.jaribha.utility;

import android.content.Context;

import com.djhs16.utils.ComplexPreferences;
import com.jaribha.models.UserData;

public class SessionManager {

    private static SessionManager instance;
    private final String PREFERENCE_NAME = "JARIBHA_PREFERENCE";
    private final Context mContext;

    /**
     * Method to get single instance of this class
     *
     * @return SessionManager instance of class
     */
    public static SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    private SessionManager(Context context) {
        mContext = context;
    }

    private ComplexPreferences getPreference() {
        return ComplexPreferences.getComplexPreferences(mContext, PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void saveUser(UserData user) {
        deleteUser();
        ComplexPreferences preferences = getPreference();
        preferences.putObject(UserData.class.getName(), user);
        preferences.commit();
    }

    public UserData getUser() {
        ComplexPreferences preferences = getPreference();
        UserData userData = preferences.getObject(UserData.class.getName(), UserData.class, null);
        return userData;
    }

    public void deleteUser() {
        getPreference().clear().commit();
    }
}
