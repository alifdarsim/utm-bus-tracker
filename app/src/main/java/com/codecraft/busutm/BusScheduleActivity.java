package com.codecraft.busutm;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.chrisbanes.photoview.PhotoView;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BusScheduleActivity extends AppCompatActivity {

    PhotoView mPhotoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/clanpro.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mPhotoView.setScale(1,true);
                finish();
            }
        });
        mPhotoView = (PhotoView) findViewById(R.id.imageEnhance);
        mPhotoView.setImageResource(R.drawable.jadual);
        mPhotoView.post(new Runnable() {
            @Override
            public void run() {
                mPhotoView.setScale(2.3f,true);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        finish();
        mPhotoView.setScale(1,true);
    }
}
