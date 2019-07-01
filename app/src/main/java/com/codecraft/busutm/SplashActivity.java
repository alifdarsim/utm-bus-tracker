package com.codecraft.busutm;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.codecraft.busutm.Http.GetLatestVersion;

public class SplashActivity extends AppCompatActivity {

    int versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;
    public static String versionCheck = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra("EXIT", false)) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // only for gingerbread and newer versions
                finishAndRemoveTask();
            } else {
                finishAffinity();
            }
        } else {
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
            AppsRunNotification.cancelNotification(this);
            AppsRunNotification.BackgroundRunningNotification(this);
            setContentView(R.layout.activity_splash);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    GetLatestVersion latestVersion = new GetLatestVersion();
                    latestVersion.setListener(new GetLatestVersion.MyAsyncTaskListener() {
                        @Override
                        public void onPostExecuteConcluded(String result) {
                            Log.e("VERSION", "app: " + versionName + ", online: " + versionCheck);
                            try{
                                versionCheck = versionCheck.replace("\n", "").replace("\r", "");
                                if (versionCheck.equals(versionName)){
                                    Log.e("VERSIOasN", "app: " + versionName + ", online: " + versionCheck);
                                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else if (versionCheck.equals("false")){
//                                    Toast.makeText(SplashActivity.this, "Server is down. Please try again later", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.e("VERSIO2N", "app: " + versionName + ", online: " + versionCheck);
//                                    Toast.makeText(SplashActivity.this, "There is new update for the apps. Consider to update it in the setting.", Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                    latestVersion.execute();
                }
            }, 250);
        }
    }
}
