package com.shramik.india;

import java.util.List;

public class ProfileModelEmployer {
    private String id;
    private String name;
    private String number;
    private String state;
    private String industry;
    String latitude;
    String longitude;
    ProfileModelEmployer(){

    }
    ProfileModelEmployer(String id, String name, String number, String state, String industry, String latitude, String longitude){
        this.id = id;
        this.name = name;
        this.number = number;
        this.state = state;
        this.industry = industry;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public String getId(){return id;}
    public String getName(){
        return name;
    }
    public String getNumber(){
        return number;
    }
    public String getState(){
        return state;
    }
    public String getIndustry(){
        return industry;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
