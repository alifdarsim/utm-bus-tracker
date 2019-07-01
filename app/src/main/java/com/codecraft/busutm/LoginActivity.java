package com.codecraft.busutm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jaeger.library.StatusBarUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {

    String lt;
    String execution;
    String _eventId;
    String username;
    String password;
    SharedPreferences prefs;
    @BindView(R.id.loginName)
    TextView loginName;
    @BindView(R.id.loginPassword)
    TextView loginPassword;
    MaterialDialog dialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/clanpro.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
        String nameTxt = prefs.getString("loginNameText", null);
        String passwordTxt = prefs.getString("loginPassText", null);
        if (nameTxt != null){
            loginName.setText(nameTxt);
            loginPassword.setText(passwordTxt);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @OnClick(R.id.loginButton)
    void loginClick(){
        String username = loginName.getText().toString();
        String password = loginPassword.getText().toString();
        if(TextUtils.isEmpty(username) && TextUtils.isEmpty(password)){
            showToast("Please enter ACID ID and password.");
        } else if (TextUtils.isEmpty(username)){
            showToast("Please enter ACID ID.");
        } else if (TextUtils.isEmpty(password)){
            showToast("Please enter ACID password.");
        } else{
            new StudentSignIn().execute(username, password);
        }
    }

    private class StudentSignIn extends AsyncTask<String, String, String>
    {
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
            MaterialDialog.Builder builder = new MaterialDialog.Builder(LoginActivity.this)
                    .content("Signing in...")
                    .progress(true, 0);

            dialog1 = builder.build();
            dialog1.show();
        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://pmaya.fkm.utm.my/onlineregister/acid1.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("userid", params[0])
                        .appendQueryParameter("pass", params[1]);
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
            if (!result.equals("0")){
                try{
                    final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
                    if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                        dialog1.dismiss();
                        new MaterialDialog.Builder(LoginActivity.this)
                                .title("GPS disable")
                                .content("This apps required GPS to function properly. Please turn on your GPS first then login again.")
                                .positiveText("SETTING")
                                .negativeText("CANCEL")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivity(intent);
                                    }
                                })
                                .show();
                    } else {

                        //just some algorithm for parsing the data output
                        String[] part = result.split("<p>");
                        final String name = part[2];
                        String matric_card = part[3];
                        String misc = part[4];
                        String[] part2 = misc.split(",");
                        String faculty = part2[1];
                        faculty = faculty.substring(3);

                        //store require info into the local system
                        prefs = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("name", name);
                        editor.putString("mc", matric_card);
                        editor.putString("fac", faculty);
                        editor.putString("loginNameText", username);
                        editor.putString("loginPassText", password);
                        editor.apply();

                        //store data into online database for research purpose maybe???
                        new StoringStudentInfo().execute(name, matric_card, faculty);

                        //after save data to internal memory, then go to main page, give a little delay to make it beautiful
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog1.dismiss();
                                showToast("Login as " + name);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }, 2000);
                    }

                } catch (Exception e){
                    dialog1.dismiss();
                    Toast.makeText(LoginActivity.this, "No internet access", Toast.LENGTH_LONG).show();
                }
            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
                Toast.makeText(LoginActivity.this, "No internet detected", Toast.LENGTH_LONG).show();
            }
            else{
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog1.dismiss();
                        Log.e("Log: ", "Wrong password or ACID ID.");
                        Toast.makeText(LoginActivity.this, "Wrong ACID ID or password", Toast.LENGTH_LONG).show();
                    }
                }, 2000);
            }
        }
    }

    void showToast(String text){
        Toast.makeText(LoginActivity.this, text, Toast.LENGTH_LONG).show();
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            AppsRunNotification.cancelNotification(this);
            Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private class StoringStudentInfo extends AsyncTask<String, String, String>
    {
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://kencana.fkm.utm.my/samad/busutm/insertstudentdata.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
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
                        .appendQueryParameter("name", params[0])
                        .appendQueryParameter("mc", params[1])
                        .appendQueryParameter("fac", params[2]);
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

        }
    }
}
