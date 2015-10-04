package app.Control;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import app.Objects.Appointment;
import app.Objects.Giver;
import app.Objects.Service;

public class LocalStorageAccess {

    private static final String SHARED_PREF_ID = "com.casific.androidcourse1";
    private static final String FACEBOOK_ME_REQUEST_KEY = "meResponse";
    private static final String NAME_KEY = "name";
    private static final String EMAIL_KEY = "email";
    private static final String PICTURE_KEY = "picture";
    private static final String URL_KEY = "url";
    private static final String DATA_KEY = "data";

    // User data
    private String userLoginData = null;
    private String userName;
    private String userEmail;
    private String userImageURL;

    // Members
    private SharedPreferences mSharedPreferences = null;
    static LocalStorageAccess instance = null;

    private Context mContext = null;

    // Init class
    public static void init(Context c) {
        if (instance == null) {

            // Init instance
            instance = new LocalStorageAccess();
            instance.mContext = c;

            // Get shared prefs
            instance.mSharedPreferences = c.getSharedPreferences(SHARED_PREF_ID, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = instance.mSharedPreferences.edit();

            // Load user locally stored data
            instance.loadLocallyStoredUserData();
        }


    }

    private void loadLocallyStoredUserData() {

        // Start with facebook saved info
        userLoginData = mSharedPreferences.getString(FACEBOOK_ME_REQUEST_KEY, null);
        if (userLoginData != null) {
            processFacebookMeRequestResult((userLoginData));
        }
    }

    public static LocalStorageAccess getInstance() {
        if (instance == null) {
            instance = new LocalStorageAccess();
        }

        return instance;
    }

    public void clearDataFromSharedPrefs() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public void processFacebookMeRequestResult(String response) {

        // Store facebook response in shared preferences
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(FACEBOOK_ME_REQUEST_KEY, response);
        editor.commit();

        userLoginData = response;

        if (response != null) {
            JSONObject jsonObject;
            JSONObject tmp;
            try {
                jsonObject = new JSONObject(response);
                if (jsonObject.has(NAME_KEY)) {
                    userName = jsonObject.getString(NAME_KEY);
                }
                if (jsonObject.has(EMAIL_KEY)) {
                    userEmail = jsonObject.getString(EMAIL_KEY);
                }
                if (jsonObject.has(PICTURE_KEY)) {
                    tmp = jsonObject.getJSONObject(PICTURE_KEY);
                    if (tmp.has(DATA_KEY)) {
                        tmp = tmp.getJSONObject(DATA_KEY);
                        if (tmp.has(URL_KEY)) {
                            userImageURL = tmp.getString(URL_KEY);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            userName = null;
            userEmail = null;
            userImageURL = null;
        }
    }

    public boolean isUserDataLoaded() {
        return (userName != null);
    }

    public String getUserImageURL() {
        return userImageURL;
    }

    public String getUserName() {
        return userName;
    }
}