package com.example.abhishek.petmania;

/**
 * Created by Abhishek on 3/4/2018.
 */

import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonRequest;
import com.example.abhishek.petmania.JsonRequest;

import java.util.List;

import static com.example.abhishek.petmania.MapPageActivity.Name;


public class JsonController {

    private final int TAG = 100;

    // Personal API Key for MeetUP
    private static final String API_KEY = "97e3b04c437a79671b307167723239";

    private String Latitude = Double.toString(MapPageActivity.latitude);
    private String Longitude = Double.toString(MapPageActivity.longitude);

    private OnResponseListener responseListener;

    public JsonController(OnResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    // Adds request to volley request queue
    public void sendRequest() {

        // Request Method
        int method = Request.Method.GET;

        if (Latitude.length() == 0 && Longitude.length() == 0) {
            Latitude = "37.774929";
            Longitude = "-122.419416";
        }

        // Url with GET parameters
        String url = "https://api.meetup.com/find/upcoming_events?&sign=" +
                "true&photo-host=public&lon=" + Longitude +
                "&lat=" + Latitude + "&page=25&text=Pets&radius=20" + "&key=" + API_KEY;


        // Create new request using JsonRequest
        JsonRequest request
                = new JsonRequest(
                method,
                url,
                //done with request, let you know if it works with 1/2 listeners
                new Response.Listener<List<MeetUp>>() {
                    @Override
                    public void onResponse(List<MeetUp> meetUps) {
                        responseListener.onSuccess(meetUps); //seen in main activity
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        responseListener.onFailure(error.getMessage());
                    }
                }
        );
        // Add tag to request
        request.setTag(TAG);

        // Get RequestQueue from VolleySingleton
        VolleySingleton.getInstance(MapPageActivity.getContext()).addToRequestQueue(request);
    }

    // Cancels all request pending in request queue
    public void cancelAllRequests() {
        VolleySingleton.getInstance(MapPageActivity.getContext()).cancelAllRequests(TAG);
    }

    // Interface to communicate between {@link android.app.Activity} and {@link JsonRequest}
    public interface OnResponseListener {
        void onSuccess(List<MeetUp> meetUps);
        void onFailure(String errorMessage);
    }


}



