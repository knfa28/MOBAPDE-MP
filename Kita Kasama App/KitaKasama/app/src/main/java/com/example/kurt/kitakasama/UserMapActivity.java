package com.example.kurt.kitakasama;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kurt.kitakasama.R;
import com.example.kurt.kitakasama.SmsManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class UserMapActivity extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    Button sendNeutral;
    Button sendNegative;
    Button sendExtreme;
    SmsManager sms;
    Location location;
    LocationManager locationManager;
    int LOCATION_REFRESH_TIME = 1000;
    int LOCATION_REFRESH_DISTANCE = 1000;
    MySQLiteModel localDb;
    LocalUser localUser;
    Marker user;
    GoogleApiClient mGoogleApiClient;
    LatLng myPos, currPos;
    FloatingActionButton pause, resume, stop;
    TextView sessionStatus;
    int asyncProcess;
    int sessionId;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.userMap);
        mapFragment.getMapAsync(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        pause = (FloatingActionButton) findViewById(R.id.pause_button);
        resume = (FloatingActionButton) findViewById(R.id.play_button);
        stop = (FloatingActionButton) findViewById(R.id.stop_button);
        sessionStatus = (TextView) findViewById(R.id.session_status_text);

        localDb = new MySQLiteModel(getBaseContext());
        localUser = localDb.getUser(1);

        sessionId = getIntent().getIntExtra("sessionId", -999);

        locationManager = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        } else {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        //mGoogleApiClient = new GoogleApiClient.Builder(v.getContext()).addApi(LocationServices.API).build();

        //location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null)
            myPos = new LatLng(location.getLatitude(), location.getLongitude());
        else {
            Log.e("ERR", "location is null");

        }

        mGoogleApiClient = new GoogleApiClient.Builder(getBaseContext())
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(onConnectionFailedListener)
                .addConnectionCallbacks(connectionCallbacks)
                .build();

        mGoogleApiClient.connect();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setUpMapIfNeeded();

        sendNeutral = (Button) findViewById(R.id.btn_neutral);
        sendNegative = (Button) findViewById(R.id.btn_negative);
        sendExtreme = (Button) findViewById(R.id.btn_extreme);
        sms = new SmsManager(getBaseContext());

        sendNeutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sms.sendToAll(localDb.getListTrackers(), localUser.getNeutralMsg());
                Toast.makeText(getApplicationContext(),
                        "Trackers have been notified", Toast.LENGTH_LONG).show();
            }
        });

        sendNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sms.sendToAll(localDb.getListTrackers(), localUser.getNegativeMsg());
                Toast.makeText(getApplicationContext(),
                        "Trackers have been notified", Toast.LENGTH_LONG).show();
            }
        });

        sendExtreme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sms.sendToAll(localDb.getListTrackers(), localUser.getExtremeMsg());
                Toast.makeText(getApplicationContext(),
                        "Trackers have been notified", Toast.LENGTH_LONG).show();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "Paused";
                asyncProcess = 0;
                new InitSSH().execute();
                sessionStatus.setText("Session: Paused");
                pause.setVisibility(View.INVISIBLE);
                resume.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),
                        "Session Paused", Toast.LENGTH_LONG).show();
            }
        });

        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "Running";
                asyncProcess = 0;
                new InitSSH().execute();
                sessionStatus.setText("Session: Running");
                pause.setVisibility(View.VISIBLE);
                resume.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(),
                        "Session Resumed", Toast.LENGTH_LONG).show();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "Stopped";
                asyncProcess = 0;
                new InitSSH().execute();
                sessionStatus.setText("Session: Stopped");
                pause.setVisibility(View.INVISIBLE);
                resume.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),
                        "Session Stopped", Toast.LENGTH_LONG).show();
                sms.sendToAll(localDb.getListTrackers(), localUser.getUserName() + " has ended the Session!");
                Toast.makeText(getApplicationContext(),
                        "Trackers have been notified", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_tracker_status) {

        } else if (id == R.id.nav_pause_session) {

        } else if (id == R.id.nav_stop_session) {

        } else if (id == R.id.nav_tracker) {

        } else if (id == R.id.nav_add_tracker) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.userMap))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        LatLng myPos = new LatLng(location.getLatitude(), location.getLongitude());

        user  = mMap.addMarker(new MarkerOptions()
                        .position(myPos)
                        .snippet("My Location")
                        .title("Current Position")
        );

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(myPos, 15);
        mMap.animateCamera(cu, 4000, null);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myPos));

        Double lat = getIntent().getDoubleExtra("lat", 0);
        Double lng = getIntent().getDoubleExtra("lng", 0);

        LatLng toLatLng = new LatLng(lat,lng);

        Marker destination  = mMap.addMarker(new MarkerOptions()
                        .position(toLatLng)
                        .snippet("Destination Location")
                        .title("Destination Location")
        );


    }
    GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Toast.makeText(getBaseContext(), "GPS is not connected.", Toast.LENGTH_SHORT);
        }
    };

    GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(100);

            if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }else {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient,
                        locationRequest,
                        locationListener
                );

                LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }
        }

        @Override
        public void onConnectionSuspended(int i) {

        }
    };

    com.google.android.gms.location.LocationListener locationListener = new com.google.android.gms.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            user.remove();
            LatLng myPos = new LatLng(location.getLatitude(), location.getLongitude());

            user  = mMap.addMarker(new MarkerOptions()
                            .position(myPos)
                            .snippet("My Location")
            );

            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);

            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(myPos, 15);
            mMap.animateCamera(cu, 4000, null);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myPos));

            currPos = myPos;
            asyncProcess = 1;
            new InitSSH().execute();
        }
    };

    class InitSSH extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if(sessionId != -999) {
                switch (asyncProcess) {
                    case 0:
                        MySQLSSHModel.updateSessionStatus(sessionId, status);
                        break;
                    case 1:
                        MySQLSSHModel.updateSessionLocation(sessionId, currPos);
                        break;
                }
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            Toast toast = Toast.makeText(getBaseContext(),
                    "Error connecting to Server via SSH", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();

        }
    }

}
