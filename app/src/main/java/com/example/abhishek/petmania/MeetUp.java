package com.example.abhishek.petmania;

/**
 * Created by Abhishek on 3/4/2018.
 */

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MeetUp {

    private String MeetupName;
    private String MeetUpId;
    private String MeetupLink;
    private String MeetupLat;
    private String MeetupLon;

    /**
     *
     * @param jsonArray    {@link JSONArray} response, received in Volley success listener
     * @return  list of meetups
     * @throws JSONException
     */

    public static List<MeetUp> parseJson(JSONArray jsonArray) throws JSONException {

        List<MeetUp> meetUps = new ArrayList<>();

        try {
            // Parsing json array response, loop through each json object
            //Log.d("JSON ARRAY SIZE", jsonArray.length()+"");
            for (int i = 0; i < jsonArray.length(); i++) {
                // Create new MeetUp object from each JSONObject in the JSONArray
                JSONObject meetup = (JSONObject) jsonArray.get(i);
                meetUps.add(new MeetUp(meetup));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(MapPageActivity.getContext(),
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

        return  meetUps;
    }

    /**
     * <p>Class constructor</p>
     * <p>MeetUp JSONObject</p>
     * @param jsonObject    {@link JSONObject} from each item in the search result
     * @throws JSONException     when parser fails to parse the given JSON
     */
    private MeetUp(JSONObject jsonObject) throws JSONException {

        if(jsonObject.has("id")){
            this.setMeetUpId(jsonObject.getString("id"));
        }

        if(jsonObject.has("link")){
            this.setMeetupLink(jsonObject.getString("link"));
        }

        if(jsonObject.has("name")){
            this.setMeetupLon(jsonObject.getString("name"));
        }

        if(jsonObject.has("venue")){

            JSONObject venueData = jsonObject.getJSONObject("venue");

            if(venueData.has("lat")){
                this.setMeetupLat(venueData.getString("lat"));
            }

            if(venueData.has("lon")){
                this.setMeetupLon(venueData.getString("lon"));
            }

        }

    }

    public String getMeetupName() {
        return MeetupName;
    }

    public void setMeetupName(String meetupName) {
        MeetupName = meetupName;
    }

    public String getMeetUpId() {
        return MeetUpId;
    }

    public void setMeetUpId(String meetUpId) {
        MeetUpId = meetUpId;
    }

    public String getMeetupLink() {
        return MeetupLink;
    }

    public void setMeetupLink(String meetupLink) {
        MeetupLink = meetupLink;
    }

    public String getMeetupLat() {
        return MeetupLat;
    }

    public void setMeetupLat(String meetupLat) {
        MeetupLat = meetupLat;
    }

    public String getMeetupLon() {
        return MeetupLon;
    }

    public void setMeetupLon(String meetupLon) {
        MeetupLon = meetupLon;
    }
}
