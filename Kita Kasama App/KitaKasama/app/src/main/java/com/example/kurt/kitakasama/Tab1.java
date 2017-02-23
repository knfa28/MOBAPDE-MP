package com.example.kurt.kitakasama;

/**
 * Created by Seaver on 4/14/2016.
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kurt.kitakasama.DelayAutoCompleteTextView;
import com.example.kurt.kitakasama.GeoAutoCompleteAdapter;
import com.example.kurt.kitakasama.GeoSearchResult;
import com.example.kurt.kitakasama.R;
import com.example.kurt.kitakasama.UserMapActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by hp1 on 21-01-2015.
 */
public class Tab1 extends Fragment {
    Button startSession;
    private Integer THRESHOLD = 2;
    private DelayAutoCompleteTextView geo_autocomplete;
    ArrayList<Integer> tempCodes = new ArrayList();
    int tempSesh;
    MySQLiteModel localDb;
    LocationManager locationManager;
    Location location;
    LatLng myPos;
    GoogleApiClient mGoogleApiClient;
    SmsManager sms;
    LocalUser localUser;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_1, container, false);
        startSession = (Button) v.findViewById(R.id.btn_start);

        sms = new SmsManager(getContext());

        localDb = new MySQLiteModel(v.getContext());
        for(int i = 0; i < localDb.getListTrackers().size(); i++) {
            int randCode = 0;

            for (int j = 1; j < 1000000; j *= 10) {
                randCode += new Random().nextInt(9) * j;
            }

            tempCodes.add(randCode);
            randCode = 0;

            for (int j = 1; j < 1000000; j *= 10) {
                randCode += new Random().nextInt(9) * j;
            }
            tempSesh = randCode;
        }
        localUser = localDb.getUser(1);

        startSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < localDb.getListTrackers().size(); i++) {
                    sms.send(localDb.getListTrackers().get(i).getTrackerContact(),
                            localUser.getUserName() + " would like you to track his/her session!\n" +
                                    "Destination: " + geo_autocomplete.toString() + "\n" +
                                    "Session Code: " + tempSesh + "\n" +
                                    "Tracker Code: " + tempCodes.get(i));
                }

                locationManager = (LocationManager) v.getContext().getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

                mGoogleApiClient = new GoogleApiClient.Builder(v.getContext())
                        .addApi(LocationServices.API)
                        .addOnConnectionFailedListener(onConnectionFailedListener)
                        .addConnectionCallbacks(connectionCallbacks)
                        .build();

                mGoogleApiClient.connect();

                if (!geo_autocomplete.getText().toString().equals("")) {
                    new InitSSH().execute();
                }

            }
        });

        geo_autocomplete = (DelayAutoCompleteTextView) v.findViewById(R.id.destination_edit);
        geo_autocomplete.setThreshold(THRESHOLD);
        geo_autocomplete.setAdapter(new GeoAutoCompleteAdapter(v.getContext())); // 'this' is Activity instance

        geo_autocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                GeoSearchResult result = (GeoSearchResult) adapterView.getItemAtPosition(position);
                geo_autocomplete.setText(result.getAddress());
            }
        });

        geo_autocomplete.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return v;
    }

    GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Toast.makeText(getContext(), "GPS is not connected.", Toast.LENGTH_SHORT);
        }
    };

    GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setNumUpdates(1);

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            Log.i("TAG", "location " + location.getLatitude() + ", " + location.getLongitude());
        }
    };

    public LatLng getLocationFromAddress(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    class InitSSH extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            MySQLSSHModel.startSession(tempSesh, localDb.getUser(1), tempCodes, localDb.getListTrackers(), myPos);
            System.out.println("STARTED SESSION!");
            return null;
        }

        @Override
        protected void onCancelled() {
            Toast toast = Toast.makeText(getContext(),
                    "Error connecting to Server via SSH", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            LatLng to = getLocationFromAddress(getContext(), geo_autocomplete.getText().toString());
            Intent intent = new Intent(getContext(), UserMapActivity.class);
            intent.putExtra("lat", to.latitude);
            intent.putExtra("lng", to.longitude);
            intent.putExtra("sessionId", tempSesh);
            startActivity(intent);
        }
    }


}
