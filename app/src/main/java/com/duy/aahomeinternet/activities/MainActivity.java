package com.duy.aahomeinternet.activities;


import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.duy.aahomeinternet.FirebaseHandler;
import com.duy.aahomeinternet.R;
import com.duy.aahomeinternet.Settings;
import com.duy.aahomeinternet.data.Database;
import com.duy.aahomeinternet.data.Preferences;
import com.duy.aahomeinternet.fragment.SectionsPagerAdapter;
import com.duy.aahomeinternet.items.DeviceItem;
import com.duy.aahomeinternet.utils.Variable;
import com.duy.aahomeinternet.utils.VoiceUtils;
import com.duy.aahomeinternet.weather.WeatherAsyncTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        ViewPager.OnPageChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public static final int ACTIVITY_ADD_DEVICE = 1;
    public static final int ACTIVITY_ADD_MODE = 2;
    public static final String TAG = MainActivity.class.getName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int REQ_CODE_SPEECH_INPUT = 1242;
    private Preferences preferences;
    private ViewPager viewPager;
    private FloatingActionButton fab;
    private Database database;
    private TextView txtLightSensor;
    private FirebaseHandler mFirebase;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebase = new FirebaseHandler(this);

        initData();
        initView();

    }

    protected void initData() {
        SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setting.registerOnSharedPreferenceChangeListener(this);
        preferences = new Preferences(getApplicationContext());
        database = new Database(getApplicationContext());
    }

    protected void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), MainActivity.this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(sectionsPagerAdapter.getCount());
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setUpTabLayout(tabLayout);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
        TextView txtNameAcc = (TextView) findViewById(R.id.txt_acc_name);
        txtNameAcc.setText(preferences.getString(Preferences.EMAIL));

        Button btnLogout = (Button) findViewById(R.id.btn_sign_out);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.putString(Preferences.PASSWORD, "");
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button btnSetting = (Button) findViewById(R.id.btn_setting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
            }
        });
        CheckBox swAutoLogin = (CheckBox) findViewById(R.id.sw_auto_login);
        swAutoLogin.setChecked(Preferences.getBoolean(this, Preferences.AUTO_LOGIN));

        swAutoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Preferences.putBoolean(MainActivity.this, Preferences.AUTO_LOGIN, b);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        preferences.putInt(Preferences.LAST_PAGE, viewPager.getCurrentItem());
        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    public boolean isEnableWifi() {
        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        switch (position) {
            case 1:
//                fab.show();
                fab.setImageResource(R.drawable.ic_add_white_24dp);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, DeviceActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("type", 0);
                        intent.putExtra("data", bundle);
                        startActivityForResult(intent, Variable.ACTIVITY_DEVICE);
                    }
                });
                break;
            default:
                fab.setImageResource(R.drawable.ic_mic_white_24dp);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        promptSpeechInput();
                    }
                });
                break;
        }
    }

    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    protected void showDialog(String s) {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(MainActivity.this);
        aBuilder.setMessage(s);
        aBuilder.setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        aBuilder.create().show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }

    //
    protected void setUpTabLayout(TabLayout tabLayout) {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_white_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_devices_other_white_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_chrome_reader_mode_white_24dp);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_insert_chart_white_24dp);
    }


    //Google API Location
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MainActivity.this);
        } else {
            onLocationChanged(location);
            new WeatherAsyncTask(MainActivity.this, location.getLatitude(), location.getLongitude()).execute();
        }
    }

    //Google API Location
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            /*
             * Google Play services can resolve some errors it detects.
             * If the error has a resolution, try sending an Intent to
             * start a Google Play services activity that can resolve
             * error.
             */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                    /*
                     * Thrown if Google Play services canceled the original
                     * PendingIntent
                     */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
                /*
                 * If no resolution is available, display a dialog to the
                 * user with the error.
                 */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //You had this as int. It is advised to have Lat/Loing as double.
        try {
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
            StringBuilder builder = new StringBuilder();

            List<Address> address = geoCoder.getFromLocation(lat, lng, 1);
            int maxLines = address.get(0).getMaxAddressLineIndex();
            for (int i = 0; i < maxLines; i++) {
                String addressStr = address.get(0).getAddressLine(i);
                builder.append(addressStr);
                builder.append(" ");
            }

            String fnialAddress = builder.toString(); //This is the complete address.
//            txtLocation.setText(fnialAddress);

            Log.d(TAG, "onLocationChanged: " + fnialAddress);
        } catch (IOException e) {
            // Handle IOException
        } catch (NullPointerException e) {
            // Handle NullPointerException
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Variable.ACTIVITY_DEVICE:
                if (resultCode == Variable.ACTIVITY_ADD_DEVICE) {
                    try {
                        Bundle bundle = data.getBundleExtra("data");
                        final DeviceItem deviceItem = (DeviceItem) bundle.getSerializable(Preferences.DEVICE);
                        ArrayList<DeviceItem> arrayList = database.getAllDevice();
                        arrayList.add(deviceItem);
                        String url = "users/" + mFirebase.getUid() + "/" + "devices/";
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference(url);
                        db.removeValue();
                        db.setValue(arrayList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (resultCode == Variable.ACTIVITY_CHANGE_INFO_DEVICE) {
                    try {
                        Bundle bundle = data.getBundleExtra("data");
                        final DeviceItem deviceItem = (DeviceItem) bundle.getSerializable(Preferences.DEVICE);
                        database.updateDevice(deviceItem);
                        ArrayList<DeviceItem> arrayList = database.getAllDevice();
                        String url = "users/" + mFirebase.getUid() + "/" + "devices/";
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference(url);
                        db.removeValue();
                        db.setValue(arrayList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case REQ_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    VoiceUtils voice = new VoiceUtils(mFirebase);
                    voice.getCommand(result.get(0));
                    Toast.makeText(this, result.get(0), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public interface ChartListener {
        void updateChart();
    }
}
