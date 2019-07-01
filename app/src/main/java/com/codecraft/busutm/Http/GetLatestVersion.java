package com.codecraft.busutm.Http;

import android.os.AsyncTask;
import android.util.Log;

import com.codecraft.busutm.MainActivity;
import com.codecraft.busutm.SplashActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

public class GetLatestVersion extends AsyncTask<Void, Void, String> {

    public interface MyAsyncTaskListener {
        void onPostExecuteConcluded(String result);
    }

    private GetLatestVersion.MyAsyncTaskListener mListener;

    final public void setListener(GetLatestVersion.MyAsyncTaskListener listener) {
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(Void... arg0) {
        HttpHandler sh = new HttpHandler();
        // Making a request to url and getting response
        String url = "http://kencana.fkm.utm.my/samad/busutm/version.php";
        String webResponse = sh.makeServiceCall(url);
        Log.e(TAG, "Response from url: " + webResponse);
        if (webResponse != null) {
            SplashActivity.versionCheck = webResponse;
        } else {
            Log.e(TAG, "Couldn't get anything from server.");
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (mListener != null)
            mListener.onPostExecuteConcluded(result);
    }
}