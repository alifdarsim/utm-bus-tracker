package com.codecraft.busutm.Http;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetHttpResponse extends AsyncTask<String, String, String>{
    HttpURLConnection conn;
    URL url = null;

    public void setUrl(URL url) {
        this.url = url;
    }

    public void setConn(HttpURLConnection conn) {
        this.conn = conn;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //this method will be running on UI thread
    }
    @Override
    protected String doInBackground(String... params) {
        try {
            // Setup HttpURLConnection class to send and receive data from php and mysql
            conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");

            // setDoInput and setDoOutput method depict handling of both send and receive
            conn.setDoInput(true);
            conn.setDoOutput(true);

            // Append parameters to URL
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("matric", params[0])
                    .appendQueryParameter("lat", params[1])
                    .appendQueryParameter("lng", params[2]);
            String query = builder.build().getEncodedQuery();

            // Open connection for sending data
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            conn.connect();

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return "exception";
        }

        try {

            int response_code = conn.getResponseCode();
            // Check if successful connection made
            if (response_code == HttpURLConnection.HTTP_OK) {

                // Read data sent from server
                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                // Pass data to onPostExecute method
                return(result.toString());

            }else{
                return("unsuccessful");
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "exception";
        } finally {
            conn.disconnect();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        //i really dont care atm
    }

}
