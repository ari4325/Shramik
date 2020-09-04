package com.shramik.india;

public class ProfileModelWorker {
    private String id;
    private String name;
    private String age;
    private String number;
    private String home_state;
    private String choice_of_state;
    private String smartphone;
    private String added_workers;
    private String latitude;
    private String longitude;
    ProfileModelWorker(){
    }
    ProfileModelWorker(String id, String name, String age, String number, String home_state, String choice_of_state, String smartphone , String added_workers, String latitude, String longitude){
        this.id = id;
        this.name = name;
        this.age = age;
        this.number = number;
        this.home_state = home_state;
        this.choice_of_state = choice_of_state;
        this.smartphone = smartphone;
        this.added_workers = added_workers;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public String getId(){return id;
    }
    public String getName(){
        return name;
    }
    public String getAge(){
        return age;
    }
    public String getNumber(){
        return number;
    }
    public String getHome_state(){
        return home_state;
    }
    public String getChoice_of_state(){
        return choice_of_state;
    }
    public String getSmartphone(){
        return smartphone;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getAdded_workers() {
        return added_workers;
    }

    public String getLatitude() {
        return latitude;
    }
}
