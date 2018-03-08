package com.example.abhishek.petmania;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by Abhishek on 2/24/2018.
 */

public class MapPageActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    public static double latitude;
    public static double longitude;
    private int PROXIMITY_RADIUS = 10000;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    private Object[] DataTransfer = new Object[2];
    boolean flagEvents = false;
    public static String Name;
    DatabaseReference eventDatabase;

    JsonController mJsonController;

    private static MapPageActivity instance;
    public static Context getContext(){

        return instance.getApplicationContext();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_module);

        Name = getIntent().getExtras().getString("Name");

        eventDatabase = FirebaseDatabase.getInstance().getReference("event");

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            finish();
        }
        else {
            Log.d("onCreate","Google Play Services available.");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapID);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab
        );
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "You have been redirected to create event page", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(getApplicationContext(), CreateEvent.class);
                startActivity(intent);
            }
        });



    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    public boolean CalculationByDistance(double YourLat, double YourLong, double TargetLat , double TargetLong){

        int radius = 6371;
        double dLat = Math.toRadians(TargetLat - YourLat);
        double dLon = Math.toRadians(TargetLong - YourLong);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(YourLat))
                * Math.cos(Math.toRadians(TargetLat)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = radius * c;
        double km = valueResult / 1;
        //Toast.makeText(getApplicationContext(), radius*c + "", Toast.LENGTH_LONG).show();
        if((radius * c)/1.6 <= 30.0)
            return true;
        else
            return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_place, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.logout){
            new SessionManager(MapPageActivity.this).remove();
            Intent intentToLogin = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intentToLogin);
            finish();
        }

        if (item.getItemId() == R.id.petstores) {

            String petStores = "petstores";
            Log.d("onClick", "Button is Clicked");
            mMap.clear();

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title("Your Current Location")
                    .snippet("You are here right now")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            String url = getUrl(latitude, longitude, petStores);
            DataTransfer[0] = mMap;
            DataTransfer[1] = url;
            Log.d("onClick", url);
            GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
            getNearbyPlacesData.execute(DataTransfer);
            //Toast.makeText(MapPageActivity.this,"Button is Clicked!!! Nearby PetStores" + mLastKnownLocation.getLatitude() + " | " + mLastKnownLocation.getLongitude(), Toast.LENGTH_LONG).show();
        }

        if(item.getItemId() == R.id.events && flagEvents == false){
            Toast.makeText(getApplicationContext(), " Nearby Events within 30 miles ", Toast.LENGTH_SHORT).show();
            item.setTitle("Events");
            flagEvents = true;
            mMap.clear();

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title("Your Current Location")
                    .snippet("You are here right now")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            eventDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){

                        Event eventTemp = userSnapshot.getValue(Event.class);

                        Double lat = Double.parseDouble(eventTemp.geteLatitude());
                        Double lang = Double.parseDouble(eventTemp.geteLongitude());

                        if (CalculationByDistance(latitude, longitude, lat, lang)) {

                            String desc = eventTemp.geteDescription();

                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(lat, lang))
                                    .title(eventTemp.geteName())
                                    .snippet(desc)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else if(item.getItemId() == R.id.events && flagEvents == true){
            item.setTitle("Nearby Events");
            flagEvents = false;
            mMap.clear();
            onMapReady(mMap);
        }


        if(item.getItemId() == R.id.meetup){

            mMap.clear();
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title("Your Current Location")
                    .snippet("You are here right now")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            mJsonController = new JsonController(new JsonController.OnResponseListener() {
                @Override
                public void onSuccess(List<MeetUp> meetUps) {

                    Log.d("MeetUp Numbers", meetUps.size()+"");
                    if(meetUps.size() > 0){

                        for(MeetUp i: meetUps){

                            if(i.getMeetupLat()!=null && i.getMeetupLon()!=null) {

                                Double lat = Double.parseDouble(i.getMeetupLat());
                                Double lang = Double.parseDouble(i.getMeetupLon());

                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(lat, lang))
                                        .title(i.getMeetUpId())
                                        .snippet(i.getMeetupLink())
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                                Log.d("Id, Link, Lat, Lon", i.getMeetUpId()+", "+ i.getMeetupLink() + ", " + i.getMeetupLat() + ", " + i.getMeetupLon());
                            }
                        }

                        //Toast.makeText(getApplicationContext(), meetUps.toString(), Toast.LENGTH_LONG).show();
                        //Log.d("MeetUp Json Object", meetUps.toString());
                    }

                }

                @Override
                public void onFailure(String errorMessage) {

                    Toast.makeText(getApplicationContext(), "Failed to retrieve data", Toast.LENGTH_LONG).show();
                }
            });

            mJsonController.sendRequest();

        }

        return true;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


        eventDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){

                    Event eventTemp = userSnapshot.getValue(Event.class);

                    Double lat = Double.parseDouble(eventTemp.geteLatitude());
                    Double lang = Double.parseDouble(eventTemp.geteLongitude());
                    String desc = eventTemp.geteDescription();

                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lang))
                            .title(eventTemp.geteName())
                            .snippet(desc)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

                }

                //onLoginFailed();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mMap.getUiSettings().setZoomControlsEnabled(true);

    }



    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyApJhMmDJtyHTY_kiWxhVetoV-M3khP62Q");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "entered");

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        Toast.makeText(MapPageActivity.this,"Your Current Location", Toast.LENGTH_LONG).show();

        Log.d("onLocationChanged", String.format("latitude:%.3f longitude:%.3f",latitude,longitude));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }
        Log.d("onLocationChanged", "Exit");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }


}