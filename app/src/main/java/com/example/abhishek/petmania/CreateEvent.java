package com.example.abhishek.petmania;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;

/**
 * Created by Abhishek on 2/26/2018.
 */

public class CreateEvent extends AppCompatActivity {

    Button button;
    EditText name, addr, desc;
    DatabaseReference databaseEvent;
    String user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_createevent);

        button = (Button) findViewById(R.id.createButton);
        name = (EditText) findViewById(R.id.e_name);
        addr = (EditText) findViewById(R.id.e_addr);
        desc = (EditText) findViewById(R.id.e_desc);

        databaseEvent = FirebaseDatabase.getInstance().getReference("event");


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Barcode.GeoPoint point = getLocationFromAddress(addr.getText().toString());
                //Toast.makeText(getApplicationContext(), point.lat + " | " + point.lng, Toast.LENGTH_LONG).show();
                storeEvent(point. lat, point.lng);
                Intent intent = new Intent(getApplicationContext(), MapPageActivity.class);
                intent.putExtra("Name", user);
                startActivity(intent);
                finish();

            }
        });

    }

    public Barcode.GeoPoint getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        Barcode.GeoPoint p1 = null;

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new Barcode.GeoPoint((double) (location.getLatitude()),
                    (double) (location.getLongitude()));

            return p1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p1;
    }

    void storeEvent(double latitude, double longitude){

        String Event_name = name.getText().toString();
        String Event_addr = addr.getText().toString();
        String Event_Desc = desc.getText().toString();
        String lat =  Double.toString(latitude);
        String lon = Double.toString(longitude);

        final SessionManager sessionDetails = new SessionManager(CreateEvent.this);
        user = sessionDetails.getName();
        String id = databaseEvent.push().getKey();
        Event event = new Event(id, Event_name, Event_addr, lat, lon, Event_Desc, sessionDetails.getName());
        databaseEvent.child(id).setValue(event);
        Toast.makeText(getApplicationContext(), "Event Added", Toast.LENGTH_LONG).show();
    }
}
