package com.example.abhishek.petmania;

/**
 * Created by Abhishek on 3/4/2018.
 */

public class Event {

    String eId;
    String eName;
    String eAddress;
    String eLatitude;
    String eLongitude;
    String eDescription;
    String eCreator;

    public Event(){

    }

    public Event(String eId, String eName, String eAddress, String eLatitude, String eLongitude, String eDescription, String eCreator){

        this.eId = eId;
        this.eName = eName;
        this.eAddress = eAddress;
        this.eLatitude = eLatitude;
        this.eLongitude = eLongitude;
        this.eDescription = eDescription;
        this.eCreator = eCreator;

    }


    public String geteId() {
        return eId;
    }

    public void seteId(String eId) {
        this.eId = eId;
    }

    public String geteName() {
        return eName;
    }

    public void seteName(String eName) {
        this.eName = eName;
    }

    public String geteAddress() {
        return eAddress;
    }

    public void seteAddress(String eAddress) {
        this.eAddress = eAddress;
    }

    public String geteLatitude() {
        return eLatitude;
    }

    public void seteLatitude(String eLatitude) {
        this.eLatitude = eLatitude;
    }

    public String geteLongitude() {
        return eLongitude;
    }

    public void seteLongitude(String eLongitude) {
        this.eLongitude = eLongitude;
    }

    public String geteDescription() {
        return eDescription;
    }

    public void seteDescription(String eDescription) {
        this.eDescription = eDescription;
    }

    public String geteCreator() {
        return eCreator;
    }

    public void seteCreator(String eCreator) {
        this.eCreator = eCreator;
    }
}
