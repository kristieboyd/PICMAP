package com.example.picmap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v4.app.ActivityCompat;
import android.os.Build;
import android.os.Bundle;
import com.google.android.gms.common.api.GoogleApiClient;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.SupportMapFragment;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity
        implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, LocationListener {

    private GoogleMap mMap;
    SharedPreferences sharedPreferences;
    int locationCount = 0;

    GoogleApiClient mGoogleApiClient;
    Marker mLocationMarker;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_maps);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        }

    private void drawMarker(LatLng point){
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        // Setting latitude and longitude for the marker
        markerOptions.position(point);

        // Adding marker on the Google Map
        mMap.addMarker(markerOptions);
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                requestPermissions(new String[] {
                                android.Manifest.permission.ACCESS_COARSE_LOCATION
                        },
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            else {
                requestPermissions(new String[] {
                                android.Manifest.permission.ACCESS_COARSE_LOCATION
                        },
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle));

            if (!success) {
                Log.e("MapsActivity", "Style parsing failed.");
            }
        }
        catch (Resources.NotFoundException e) {
            Log.e("MapsActivity", "Can't find style. Error: ", e);
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();

                mMap.setMyLocationEnabled(true);
            }
        }
            else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
             }

        // Opening the sharedPreferences object
        sharedPreferences = getSharedPreferences("location", 0);

        // Getting number of locations already stored
        locationCount = sharedPreferences.getInt("locationCount", 0);

        // Getting stored zoom level if exists else return 0
        String zoom = sharedPreferences.getString("zoom", "0");

        // If locations are already saved
        if(locationCount!=0){

            String lat = "";
            String lng = "";

            // Iterating through all the locations stored
            for(int i=0;i<locationCount;i++){

                // Getting the latitude of the i-th location
                lat = sharedPreferences.getString("lat"+i,"0");

                // Getting the longitude of the i-th location
                lng = sharedPreferences.getString("lng"+i,"0");

                // Drawing marker on the map
                drawMarker(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
            }

            // Moving CameraPosition to last clicked position
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))));

            // Setting the zoom level in the map on last position is clicked
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(Float.parseFloat(zoom)));
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

        @Override
        public void onMapClick(LatLng point) {
            //Do what you want on obtained latLng
            Intent intent = new Intent(MapsActivity.this, editPhotosOptions.class);
            startActivity(intent);
            locationCount++;

            // Drawing marker on the map
            drawMarker(point);

            // Opening the editor object to write data to sharedPreferences */
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Storing the latitude for the i-th location
            editor.putString("lat"+ Integer.toString((locationCount-1)), Double.toString(point.latitude));

            // Storing the longitude for the i-th location
            editor.putString("lng"+ Integer.toString((locationCount-1)), Double.toString(point.longitude));

            // Storing the count of locations or marker count
            editor.putInt("locationCount", locationCount);

            // Storing the zoom level to the shared preferences */
            editor.putString("zoom", Float.toString(mMap.getCameraPosition().zoom));

            // Saving the values stored in the shared preferences */
            editor.commit();

            Toast.makeText(getBaseContext(), "Marker is added to the Map", Toast.LENGTH_SHORT).show();

        }
    });

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {

                // Removing the marker and circle from the Google Map
                mMap.clear();

                // Opening the editor object to delete data from sharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Clearing the editor
                editor.clear();

                // Committing the changes
                editor.commit();

                // Setting locationCount to zero
                locationCount=0;
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        // Connect to Google Play Services
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Retrieve last known location
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    // Display one location at a time
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mLocationMarker != null) {
            mLocationMarker.remove();
        }

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
            {


                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else {
                    // Do nothing
                }
                return;
            }
        }
    }
}