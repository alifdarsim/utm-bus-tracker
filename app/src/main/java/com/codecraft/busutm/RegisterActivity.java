package com.codecraft.busutm;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.jaeger.library.StatusBarUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.web) WebView webView;
    String lt;
    String execution;
    String _eventId;
    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/clanpro.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        StatusBarUtil.setTransparent(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        final ProgressBar progressWheel = (ProgressBar) findViewById(R.id.progress);
        progressWheel.setVisibility(View.INVISIBLE);

        WebView webView = (WebView) findViewById(R.id.web);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
        Log.v("UA", webView.getSettings().getUserAgentString());
        String url = "https://my.utm.my/"; //Urls provided by other activity
        if (url != null && !url.equals("")) {
            webView.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress) {
//                    progressWheel.setVisibility(View.VISIBLE);
                }
            });
            webView.loadUrl(url);
            webView.evaluateJavascript(
                    "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                    new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String html) {
                            Log.d("HTML", html);
                            // code here
                        }
                    });
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @OnClick(R.id.loginButton)
    void loginClick(){
        new FetchWeatherTask().execute();
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String forecastJsonStr = null;
            try {
                final String FORECAST_BASE_URL = "https://sso.utm.my/login?";
                final String USERNAME_PARAM = "username";
                final String PASSWORD_PARAM = "password";
                final String SUBMIT_PARAM = "submit";
                final String AUTO_PARAM = "auto";
                final String SERVICE_PARAM = "service";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL)
                        .buildUpon()
                        .appendQueryParameter(USERNAME_PARAM, "mnooralif2")
                        .appendQueryParameter(PASSWORD_PARAM, "930630085549")
                        .appendQueryParameter(SUBMIT_PARAM, "")
                        .appendQueryParameter(AUTO_PARAM, "true")
                        .appendQueryParameter(SERVICE_PARAM, "https%3A%2F%2Fmy.utm.my%2Fiportal.php")
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v("URIIIIIIIIIII: ", "Built URI " + builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                Log.e("JAWAPAN: ", forecastJsonStr);
                forecastJsonStr = forecastJsonStr.substring(forecastJsonStr.indexOf("css;jsessionid=") + 15);
                forecastJsonStr = forecastJsonStr.substring(0, forecastJsonStr.indexOf("\" />"));
                Log.e("JAWAPAN BARU: ", forecastJsonStr);
            } catch (IOException e) {
                Log.e("LALALLA", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("CLOSE STREAMMMM: ", "Error closing stream", e);
                    }
                }
            }
            return null;
        }
    }
}
