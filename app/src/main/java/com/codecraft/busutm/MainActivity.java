package com.codecraft.busutm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.Manifest;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.codecraft.busutm.Http.GetBusData;
import com.codecraft.busutm.Model.BusHelper;
import com.codecraft.busutm.Notification.SystemHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback{

    public List<String> BusStopCoordinate = new ArrayList<>();
    private static final String TAG = MainActivity.class.getSimpleName();
    public static HashMap<String, List<Float>> busList = new HashMap<>();
    HashMap<String, List<Float>> busList_saved = new HashMap<>();
    private static HashMap<String, Marker> busMarker = new HashMap<>();
    DrawerLayout drawerLayout;
    private boolean isBusy = false;
    private boolean stop = false;
    private Handler handlerBusData = new Handler();
    private Runnable runnableBusData;
    private Handler handlerLocationUpdate = new Handler();
    private Runnable runnableLocationUpdate;
    public List<Marker> BusStopMarker = new ArrayList<>();
    private LocationManager mLocationManager;
    public boolean followBusMarker = false;
    private String mc;
    private static GoogleMap mGoogleMap;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    FusedLocationProviderClient mFusedLocationClient;
    private boolean isOnlineBusClicked;
    private SystemHelper systemHelper = new SystemHelper(this);
    private TextView busOnlineNumber;
    private ImageView busOnlineArrow;
    private LatLng gMapCameraAnimateLatlng;
    public static String busOnlineFollowName;
    public static boolean isCameraNeedFollow;
    private BusHelper busHelper = new BusHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_main);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/clanpro.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        IconicsDrawable image = new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_menu).sizeDp(20).color(Color.WHITE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationIcon(image);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        SharedPreferences prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
        String name = prefs.getString("name", null);
        if (name != null) {
            mc = prefs.getString("mc", "No name defined");
            String fac = prefs.getString("fac", "No faculty defined");
            View headerView = navigationView.getHeaderView(0);
            TextView navUsername = (TextView) headerView.findViewById(R.id.nav_username);
            navUsername.setText(name);

        } else{
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(myIntent);
        }

        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initBusItem();
        busOnlineNumber = (TextView) findViewById(R.id.bus_online_number);
        busOnlineArrow = (ImageView) findViewById(R.id.bus_online_arrow);
    }

    MyRecyclerAdapter sampleRecyclerAdapter = new MyRecyclerAdapter();
    LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    private int map_containerWidth;
    private CardView online_list;

    public void initBusItem(){
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        sampleRecyclerAdapter = new MyRecyclerAdapter();
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(sampleRecyclerAdapter);
        final FrameLayout map_container = (FrameLayout) findViewById(R.id.map_container);
        final ViewTreeObserver observer= map_container.getViewTreeObserver();
        online_list = (CardView) findViewById(R.id.bus_online_list);

        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.e("Height ", map_container.getHeight() + "");
                map_containerWidth = map_container.getWidth();
                ObjectAnimator moveAnim = ObjectAnimator.ofFloat(online_list, "X", map_containerWidth);
                moveAnim.setDuration(0);
                moveAnim.setInterpolator(new DecelerateInterpolator());
                moveAnim.start();
                map_container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        busIndicator = (CardView) findViewById(R.id.bus_online_indicator);
        busIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Click: ", isOnlineBusClicked + "");
                bus_indicator_clicked(v, online_list);
            }
        });
    }

    private CardView busIndicator;
    private View numberBus_card;
    int width_mapContainer;
    public void bus_indicator_clicked(View v, CardView onlist_list){
        numberBus_card = v;
        FrameLayout map_container = (FrameLayout) findViewById(R.id.map_container);
        width_mapContainer = map_container.getWidth();
        if (isOnlineBusClicked){
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(v, "X", width_mapContainer-systemHelper.dpToPx(61));
            anim1.setDuration(200);
            anim1.setInterpolator(new DecelerateInterpolator());
            ObjectAnimator anim2 = ObjectAnimator.ofFloat(onlist_list, "X", width_mapContainer);
            anim2.setDuration(200);
            anim2.setInterpolator(new DecelerateInterpolator());
            AnimatorSet set1 = new AnimatorSet();
            set1.playTogether(anim1, anim2);
            set1.start();
            isOnlineBusClicked = false;
        } else{
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(v, "X", width_mapContainer);
            anim1.setDuration(200);
            anim1.setInterpolator(new DecelerateInterpolator());
            ObjectAnimator anim2 = ObjectAnimator.ofFloat(onlist_list, "X", width_mapContainer-systemHelper.dpToPx(98));
            anim2.setDuration(200);
            anim2.setInterpolator(new DecelerateInterpolator());
            AnimatorSet set1 = new AnimatorSet();
            set1.playTogether(anim1, anim2);
            set1.start();
            isOnlineBusClicked = true;
        }
    }

    private void MapClickerOption(){
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return false;
            }
        });
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                isCameraNeedFollow = false;
                int width = busIndicator.getWidth();
                ObjectAnimator anim1 = ObjectAnimator.ofFloat(numberBus_card, "X", width_mapContainer-systemHelper.dpToPx(12)-width);
                anim1.setDuration(200);
                anim1.setInterpolator(new DecelerateInterpolator());
                ObjectAnimator anim2 = ObjectAnimator.ofFloat(online_list, "X", width_mapContainer);
                anim2.setDuration(200);
                anim2.setInterpolator(new DecelerateInterpolator());
                AnimatorSet set1 = new AnimatorSet();
                set1.playTogether(anim1, anim2);
                set1.start();
                isOnlineBusClicked = false;
            }
        });
    }

    public static void cameraFollowMarker(String busPlate){
        for(Map.Entry<String, List<Float>> entry : busList.entrySet()) {
            if (entry.getKey().equals(busPlate)) {
                if (busMarker.containsKey(busPlate)) {
                    busOnlineFollowName = busPlate;
                    isCameraNeedFollow = true;
                    Marker marker = busMarker.get(busPlate);
                    CameraUpdate location = CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 16);
                    mGoogleMap.animateCamera(location, 500,null);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppsRunNotification.cancelNotification(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startDownloadBusData();
        startUploadStudentData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handlerBusData.removeCallbacks(runnableBusData);
        handlerLocationUpdate.removeCallbacks(runnableLocationUpdate);
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
//        mLocationManager.removeUpdates(mLocationListener);
    }

    private void LocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                mLastLocation = location;
                try{
                    new AsyncStudentLocation().execute(mc, mLastLocation.getLatitude()+"", mLastLocation.getLongitude()+"");
                }catch (Exception e){

                }
            }
        };

    };

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

    }

    @Override
    public void onMapReady(GoogleMap map) {
        startRevealAnimation();
        mGoogleMap = map;
        mGoogleMap.getUiSettings().setCompassEnabled(false);
        LatLng utmCenter = new LatLng(1.5571551, 103.6386171);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(utmCenter, 15));
        BusStopCoordinate.add("K10, 1.560550, 103.648772");
        BusStopCoordinate.add("K9, 1.558753, 103.649181");
        BusStopCoordinate.add("Fkt, 1.554687, 103.648455");
        BusStopCoordinate.add("S42, 1.554990, 103.648002");
        BusStopCoordinate.add("S05, 1.555385, 103.646117");
        BusStopCoordinate.add("Ktc, 1.555214, 103.644708");
        BusStopCoordinate.add("Fke P19A, 1.558247, 103.640278");
        BusStopCoordinate.add("Fke Lingkaran, 1.558233, 103.640218");
        BusStopCoordinate.add("Msi, 1.556856, 103.638192");
        BusStopCoordinate.add("Ubt, 1.558940, 103.633994");
        BusStopCoordinate.add("Fab, 1.559724, 103.634641");
        BusStopCoordinate.add("N25, 1.562759, 103.639090");
        BusStopCoordinate.add("P07, 1.560029, 103.641417");
        BusStopCoordinate.add("D01, 1.561289, 103.636494");
        BusStopCoordinate.add("Fac Bahasa, 1.561536, 103.638231");
        BusStopCoordinate.add("E04, 1.560718, 103.639760");
        BusStopCoordinate.add("Fkm, 1.559813, 103.640122");
        BusStopCoordinate.add("S37, 1.556897, 103.642547");
        BusStopCoordinate.add("S33, 1.557298, 103.644617");
        BusStopCoordinate.add("Kp, 1.558618, 103.646579");
        BusStopCoordinate.add("Azah, 1.556983, 103.647630");
        BusStopCoordinate.add("S15, 1.555080, 103.644140");
        BusStopCoordinate.add("Arked Lestari, 1.559397, 103.648704");
        BusStopCoordinate.add("Ktr, 1.561762, 103.629424");
        BusStopCoordinate.add("K14, 1.562629, 103.627594");
        BusStopCoordinate.add("K23, 1.563878, 103.627426");
        BusStopCoordinate.add("K28, 1.564897, 103.62788");
        BusStopCoordinate.add("L23, 1.563995, 103.629432");
        BusStopCoordinate.add("K44, 1.563203, 103.629507");
        BusStopCoordinate.add("L35, 1.562611, 103.630219");
        BusStopCoordinate.add("L24, 1.564323, 103.629892");
        BusStopCoordinate.add("L12, 1.564837, 103.631739");
        BusStopCoordinate.add("M35, 1.563867, 103.63359");
        BusStopCoordinate.add("M26, 1.565062, 103.63404");
        BusStopCoordinate.add("M23, 1.565261, 103.634374");
        BusStopCoordinate.add("M07, 1.565341, 103.635933");
        BusStopCoordinate.add("Ktdi, 1.564034, 103.635725");
        BusStopCoordinate.add("Civil, 1.562515, 103.636074");
        BusStopCoordinate.add("D01, 1.561289, 103.636494");
        BusStopCoordinate.add("Fak Bahasa, 1.561536, 103.638231");
        BusStopCoordinate.add("E04, 1.560718, 103.63976");
        BusStopCoordinate.add("Fkm, 1.559813, 103.640122");
        BusStopCoordinate.add("P19 Lingkaran, 1.558233, 103.640218");
        BusStopCoordinate.add("Msi, 1.556856, 103.638192");
        BusStopCoordinate.add("Ubt, 1.55894, 103.633994");
        BusStopCoordinate.add("Meranti, 1.559742, 103.634009");
        BusStopCoordinate.add("Cengal, 1.56131, 103.63224");
        BusStopCoordinate.add("H27, 1.561804, 103.630126");
        BusStopCoordinate.add("Kdoj, 1.575582, 103.619674");
        BusStopCoordinate.add("Kdse Nk Ke Pk, 1.566663, 103.627159");
        BusStopCoordinate.add("Pusat Kesihatan, 1.55881, 103.62753");
        BusStopCoordinate.add("Ktf H14, 1.558765, 103.630618");
        BusStopCoordinate.add("Ktf, 1.559454, 103.631693");
        BusStopCoordinate.add("Meranti, 1.559519, 103.633264");
        BusStopCoordinate.add("Fab, 1.559724, 103.634641");
        BusStopCoordinate.add("D01, 1.561289, 103.636494");
        BusStopCoordinate.add("Fak Bahasa, 1.561536, 103.638231");
        BusStopCoordinate.add("E04, 1.560718, 103.63976");
        BusStopCoordinate.add("Fkm, 1.559813, 103.640122");
        BusStopCoordinate.add("P19A Lingkaran, 1.558233, 103.640218");
        BusStopCoordinate.add("Msi, 1.556856, 103.638192");
        BusStopCoordinate.add("Ubt, 1.55894, 103.633994");
        BusStopCoordinate.add("G11, 1.559427, 103.632578");
        BusStopCoordinate.add("G21, 1.557878, 103.629442");
        BusStopCoordinate.add("Pusat Kesihatan, 1.55881, 103.62753");
        BusStopCoordinate.add("Kdse, 1.566709, 103.626929");
        BusStopCoordinate.add("Kdse2, 1.568017, 103.624381");
        LocationRequest();
        MapClickerOption();
        int i;
        for (i = 0; i < BusStopCoordinate.size(); i++) {
            String[] parts = BusStopCoordinate.get(i).split(", ");
            String part1 = parts[0];
            String part2 = parts[1];
            String part3 = parts[2];
            float latitude = Float.parseFloat(part2);
            float longitude = Float.parseFloat(part3);
            LatLng busStopMarker = new LatLng(latitude, longitude);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(busStopMarker)
                    .icon(bitmapSizeByScall(R.drawable.ic_busstop, 0.3f));
            Marker marker = mGoogleMap.addMarker(markerOptions);
            BusStopMarker.add(marker);
        }
    }

    void startRevealAnimation() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final RelativeLayout frameLayout = (RelativeLayout) findViewById(R.id.framelayout);
            final FrameLayout linearLayout = (FrameLayout) findViewById(R.id.rootll);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            final int width = displayMetrics.widthPixels;
            final int height = displayMetrics.heightPixels;
            ViewTreeObserver viewTreeObserver = linearLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Animator anim = null;
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                            anim = ViewAnimationUtils.createCircularReveal(linearLayout, width/2, height/2, 0, frameLayout.getWidth());
                        }
                        anim.setDuration(500);
                        anim.setInterpolator(new AccelerateInterpolator(2));
                        anim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                            }
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                            }
                        });
                        anim.start();
                        linearLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        }
    }

    public void startDownloadBusData()
    {
        downloadBusData();
        runnableBusData = new Runnable() {
            @Override
            public void run() {
                if(!isBusy) {
                    downloadBusData();
                }
                if(!stop) startDownloadBusData();
            }
        };
        handlerBusData.postDelayed(runnableBusData, 5000);
    }

    public void downloadBusData(){
        GetBusData busData = new GetBusData();
        busData.setListener(new GetBusData.MyAsyncTaskListener() {
            @Override
            public void onPostExecuteConcluded(String result) {
                drawAllMarker();
            }
        });
        busData.execute();
    }

    public void startUploadStudentData()
    {
        runnableLocationUpdate = new Runnable() {
            @Override
            public void run() {
                if(!isBusy) {
                    new AsyncStudentLocation().execute(mc, mLastLocation.getLatitude()+"", mLastLocation.getLongitude()+"");
                }
                if(!stop) startUploadStudentData();
            }
        };
        handlerLocationUpdate.postDelayed(runnableLocationUpdate, 10000);
    }

    private class AsyncStudentLocation extends AsyncTask<String, String, String>
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
                url = new URL("http://kencana.fkm.utm.my/samad/busutm/insertstudentdatalog.php");
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

    public void drawAllMarker() {
        List<String> name = new ArrayList<>();
        List<String> namelast = new ArrayList<>();
        for(Map.Entry<String, List<Float>> entry : busList.entrySet()) {
            String busName = entry.getKey();
            name.add(busName);
            List bus_parameter = entry.getValue();
            LatLng latlng = new LatLng((Float)bus_parameter.get(0), (Float)bus_parameter.get(1));
            if (busList_saved.containsKey(busName) && busMarker.containsKey(busName)){
                Marker marker = busMarker.get(busName);
                Float bearing = getBearing(marker.getPosition(), latlng);
                if (!bearing.isNaN())  marker.setRotation(bearing);
                MarkerAnimation.animateMarkerToICS(marker, latlng, new LatLngInterpolator.LinearFixed());
                if (isCameraNeedFollow){
                    if (busName.equals(busOnlineFollowName)){
                        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latlng, 16);
                        mGoogleMap.animateCamera(location, 5000,null);
                    }
                }
            } else {
                Log.e("BUSNAME: ", busName);
                Drawable circleDrawable = getResources().getDrawable(busHelper.getBusColor(busName));
                BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(latlng)
                        .anchor(0.5f, 0.5f)
                        .flat(true)
                        .title(busName)
                        .icon(markerIcon));
                busMarker.put(busName, marker);
                int positionToAdd = mLayoutManager.findFirstCompletelyVisibleItemPosition();
                sampleRecyclerAdapter.addItem(positionToAdd, busName);
            }
        }
        for(Map.Entry<String, List<Float>> entry : busList_saved.entrySet()) {
            String busName = entry.getKey();
            namelast.add(busName);
        }
        Log.e("dsadas", name.size() + "");
        busOnlineNumber.setText(String.valueOf(namelast.size()));
        if (namelast.size()!=name.size()){
            namelast.removeAll(name);
            for (int i = 0; i < namelast.size(); i++){
                String a = namelast.get(i);
                busMarker.get(a).remove();
                sampleRecyclerAdapter.removeData(a);
            }
        }
        busList_saved = new HashMap<>();
        busList_saved = busList;
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //Method for finding bearing between two points
    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);
        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    public BitmapDescriptor bitmapSizeByScall(int id, float scall_zero_to_one_f) {
        Resources res = this.getResources();
        Bitmap bitmapIn = BitmapFactory.decodeResource(res, id);
        Bitmap bitmapOut = Bitmap.createScaledBitmap(bitmapIn,
                Math.round(bitmapIn.getWidth() * scall_zero_to_one_f),
                Math.round(bitmapIn.getHeight() * scall_zero_to_one_f), false);
        BitmapDescriptor bitOut = BitmapDescriptorFactory.fromBitmap(bitmapOut);
        return bitOut;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            new MaterialDialog.Builder(this)
                    .title("Close app")
                    .content("Are you want to exit. This will stop the app completely")
                    .positiveText("Yes")
                    .negativeText("Cancel")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            MaterialDialog.Builder builder = new MaterialDialog.Builder(MainActivity.this)
                                    .content("Closing app...")
                                    .progress(true, 0);

                            final MaterialDialog dialog1 = builder.build();
                            dialog1.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialog1.dismiss();
                                    AppsRunNotification.cancelNotification(MainActivity.this);
                                    Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("EXIT", true);
                                    startActivity(intent);
                                }
                            }, 750);

                        }
                    })
                    .show();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_help) {
            Intent myIntent = new Intent(MainActivity.this, HelpActivity.class);
            MainActivity.this.startActivity(myIntent);
        } else if (id == R.id.nav_bus_info) {
            Intent myIntent = new Intent(MainActivity.this, RouteActivity.class);
            MainActivity.this.startActivity(myIntent);
        } else if (id == R.id.nav_about) {
            Intent myIntent = new Intent(MainActivity.this, AboutActivity.class);
            MainActivity.this.startActivity(myIntent);
        } else if (id == R.id.nav_logout) {

            new MaterialDialog.Builder(this)
                    .title("Sign out")
                    .content("Are you want to sign out? This will bring you back to sign in page.")
                    .positiveText("Yes")
                    .negativeText("Cancel")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            new MaterialDialog.Builder(MainActivity.this)
                                    .content("Signing out...")
                                    .progress(true, 0)
                                    .show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getSharedPreferences("MyPref", 0).edit().clear().apply();
                                    Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                                    MainActivity.this.startActivity(myIntent);
                                }
                            }, 1500);

                        }
                    })
                    .show();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


}
