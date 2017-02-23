package com.example.kurt.kitakasama;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class TrackerMapActivity extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    Button sendConfirm;
    Button sendCheck;
    SmsManager sms;
    Marker user;
    int sessionCode, trackerCode;
    boolean isSessionRunning = true;
    boolean hasSessionEnded = false;
    int asyncProcess;
    LatLng curr;
    Session sessionUser;
    MySQLiteModel localDb;
    LocalUser localUser;
    FloatingActionButton pause, resume, stop;
    TextView sessionStatus;
    String status, trackerStatus, lastMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.trackerMap);
        mapFragment.getMapAsync(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        sessionCode = getIntent().getIntExtra("sessionCode", -999);
        trackerCode = getIntent().getIntExtra("trackerCode", -999);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        pause = (FloatingActionButton) findViewById(R.id.pause_track_button);
        resume = (FloatingActionButton) findViewById(R.id.play_track_button);
        stop = (FloatingActionButton) findViewById(R.id.stop_track_button);
        sessionStatus = (TextView) findViewById(R.id.track_status_text);

        sendConfirm = (Button) findViewById(R.id.btn_confirm);
        sendCheck = (Button) findViewById(R.id.btn_check);
        sms = new SmsManager(getBaseContext());

        localDb = new MySQLiteModel(getBaseContext());
        localUser = localDb.getUser(1);

        sendConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncProcess = 2;
                sms.send(sessionUser.getSessionContact(), localUser.getConfirmMsg());
                Toast.makeText(getApplicationContext(),
                        "User has been notified", Toast.LENGTH_LONG).show();
                lastMessage = localUser.getConfirmMsg();
                asyncProcess = 5;
                new InitSSH().execute();
            }
        });

        sendCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncProcess = 2;
                sms.send(sessionUser.getSessionContact(), localUser.getCheckMsg());
                Toast.makeText(getApplicationContext(),
                        "User has been notified", Toast.LENGTH_LONG).show();
                lastMessage = localUser.getCheckMsg();
                asyncProcess = 5;
                new InitSSH().execute();
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackerStatus = "Paused";
                asyncProcess = 4;
                new InitSSH().execute();
                pause.setVisibility(View.INVISIBLE);
                resume.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),
                        "Tracking Paused", Toast.LENGTH_LONG).show();
            }
        });

        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "Connected";
                asyncProcess = 4;
                new InitSSH().execute();
                pause.setVisibility(View.VISIBLE);
                resume.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(),
                        "Tracking Resumed", Toast.LENGTH_LONG).show();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "Disconnected";
                asyncProcess = 4;
                new InitSSH().execute();
                pause.setVisibility(View.INVISIBLE);
                resume.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),
                        "Tracking Stopped", Toast.LENGTH_LONG).show();
            }
        });

        while(hasSessionEnded) {
            asyncProcess = 0;
            new InitSSH().execute();

            if (isSessionRunning) {
                asyncProcess = 3;
                new InitSSH().execute();
                sessionStatus.setText(status);
                setUpMapIfNeeded();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        setUpMapIfNeeded();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
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

        if (id == R.id.nav_session_status) {

        } else if (id == R.id.nav_pause_track) {

        } else if (id == R.id.nav_stop_track) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.trackerMap))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        if(sessionCode != -999 && trackerCode != -999) {
            if(user != null)
            user.remove();
            asyncProcess = 1;
            new InitSSH().execute();

            while(curr == null)
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            user = mMap.addMarker(new MarkerOptions()
                    .position(curr)
                    .snippet("Trackee Location")
                    );

            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);

            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(curr, 15);
            mMap.animateCamera(cu, 4000, null);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(curr));
        }


        /*
        LatLng curr = new LatLng(14.567627, 120.9930695);
        mMap.addMarker(new MarkerOptions()
                .position(curr)
                .snippet("My University")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
                .title("DLSU"));

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(curr, 15);
        mMap.animateCamera(cu, 4000, null);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(curr));
        */
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest locationRequest = new LocationRequest();

        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return;
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, locationListener);
        }
    }

    com.google.android.gms.location.LocationListener locationListener = new com.google.android.gms.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            user.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    };

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        LocationRequest locationRequest = new LocationRequest();

        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (requestCode == 0) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, locationListener);
                mMap.setMyLocationEnabled(true);
                return;
            }
        }
    }

    class InitSSH extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            switch(asyncProcess) {
                case 0:
                    if (MySQLSSHModel.getSessionStatus(sessionCode, trackerCode).equals("Running"))
                        isSessionRunning = true;
                    else if (MySQLSSHModel.getSessionStatus(sessionCode, trackerCode).equals("Stopped"))
                        hasSessionEnded = true;
                    else
                        isSessionRunning = false;
                    break;
                case 1:
                    curr = MySQLSSHModel.getSessionLocation(sessionCode, trackerCode);
                    break;
                case 2:
                    sessionUser = MySQLSSHModel.getSession(sessionCode);
                    break;
                case 3:
                    status = MySQLSSHModel.getSessionStatus(sessionCode, trackerCode);
                    break;
                case 4:
                    MySQLSSHModel.updateTrackerStatus(sessionCode, trackerCode, trackerStatus);
                    break;
                case 5:
                MySQLSSHModel.updateTrackerMessage(sessionCode, trackerCode, lastMessage);
                break;
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
