package app.Control;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import app.GlobalConstants;

public class NetworkAccess {

    // Members
    Context mContext = null;
    private static NetworkAccess instance = null;

    public static NetworkAccess getInstance(Context c) {
        if (instance == null) {
            instance = new NetworkAccess();
        }

        instance.mContext = c;

        return instance;
    }

    /**
     * Services
     */

    private String parseError(VolleyError error) {
        String message = "Internal Error";
        String responseBody = "";
        try {
            // Get error message
            responseBody = new String(error.networkResponse.data, "utf-8");
            Log.e("ServerAccess", "Response body: " + responseBody);
            message = responseBody;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    /**
     * Interfaces
     */

    public static interface NetworkAccessCallback_StringResponse {
        public void onErrorResponse(String error);

        public void onResponse(String response);
    }

    public static interface NetworkAccessCallback_JsonArrayResponse {
        public void onErrorResponse(String error);

        public void onResponse(JSONArray response);
    }

    /**
     * Network calls
     */
}
