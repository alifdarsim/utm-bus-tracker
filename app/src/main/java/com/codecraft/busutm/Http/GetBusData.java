package com.codecraft.busutm.Http;

import android.os.AsyncTask;
import android.util.Log;

import com.codecraft.busutm.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

public class GetBusData extends AsyncTask<Void, Void, String> {

    public interface MyAsyncTaskListener {
        void onPostExecuteConcluded(String result);
    }

    private MyAsyncTaskListener mListener;

    final public void setListener(MyAsyncTaskListener listener) {
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(Void... arg0) {
        HttpHandler sh = new HttpHandler();
        // Making a request to url and getting response
        String url = "http://kencana.fkm.utm.my/samad/busutm/getbusdata.php";
        String jsonStr = sh.makeServiceCall(url);
        Log.e(TAG, "Response from url: " + jsonStr);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                // Getting JSON Array node
                JSONArray busdata = jsonObj.getJSONArray("bus");
                // looping through All bus data
                MainActivity.busList = new HashMap<>();
                for (int i = 0; i < busdata.length(); i++) {
                    JSONObject c = busdata.getJSONObject(i);
                    String name = c.getString("name");
                    String lat = c.getString("lat");
                    String lng = c.getString("lng");
                    String spd = c.getString("spd");
                    // tmp hash map for single contact
                    List<Float> bus_parameter = new ArrayList<>();
                    bus_parameter.add(Float.parseFloat(lat));
                    bus_parameter.add(Float.parseFloat(lng));
                    bus_parameter.add(Float.parseFloat(spd));
                    // adding contact to contact list
                    MainActivity.busList.put(name, bus_parameter);
                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "Couldn't get anything from server.");
        }
        return null;
    }

    //what happen after all data get.
    @Override
    protected void onPostExecute(String result) {
        if (mListener != null)
            mListener.onPostExecuteConcluded(result);
    }
}