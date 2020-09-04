package com.shramik.india;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProfileList {
    static List<ProfileModelEmployer> ProfileListEmployer = new ArrayList<>();
    static List<ProfileModelWorker> ProfileListWorker = new ArrayList<>();
    static String occp;
    static String id;
    static Context context;
    static SharedPreferences sharedPreferences;
    static void setContext(Context c){
        context = c;
        sharedPreferences = context.getSharedPreferences("ProfileModel", Context.MODE_PRIVATE);
    }
    static void setOccupation(String s){
        occp = s;
    }
    static void setId(String i){
        id = i;
    }
    static void save(){
        Gson gson = new Gson();
        String profileListEmployerJson = gson.toJson(ProfileListEmployer);
        String profileListWorkerJson = gson.toJson(ProfileListWorker);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ProfileListEmployer", profileListEmployerJson);
        editor.putString("ProfileListWorker", profileListWorkerJson);
        editor.putString("Occupation", occp);
        editor.commit();
    }
    static void load(){
        ProfileListEmployer.clear();
        ProfileListWorker.clear();
        Gson gson = new Gson();
        Type type = new TypeToken<List<ProfileModelEmployer>>() {
        }.getType();
        Type type1 = new TypeToken<List<ProfileModelWorker>>(){}.getType();
        String profileListEmployerJson = sharedPreferences.getString("ProfileListEmployer", "");
        String profileListWorkerJson = sharedPreferences.getString("ProfileListWorker", "");
        occp = sharedPreferences.getString("Occupation", "");
        List<ProfileModelEmployer> arrPackage1 = gson.fromJson(profileListEmployerJson, type);
        List<ProfileModelWorker> arrPackage2 = gson.fromJson(profileListWorkerJson, type1);
        if(arrPackage1!=null){
            ProfileListEmployer.addAll(arrPackage1);
        }
        if(arrPackage2!=null){
            ProfileListWorker.addAll(arrPackage2);
        }
    }
    static void addDataEmployer(ProfileModelEmployer profileModelEmployer){
        ProfileListEmployer.add(profileModelEmployer);
    }

    static ProfileModelWorker getProfileWorkerById(String i){
        for(int j = 0; j< ProfileListWorker.size(); j++){
            if(ProfileListWorker.get(j).getId().equals(i)){
                return ProfileListWorker.get(j);
            }
        }
        return ProfileListWorker.get(0);
    }

    static int getProfileWorkerNumberById(String i){
        for(int j = 0; j< ProfileListWorker.size(); j++){
            if(ProfileListWorker.get(j).getId().equals(i)){
                return j;
            }
        }
        return -1;
    }

    static void addDataWorker(ProfileModelWorker profileModelWorker){
        ProfileListWorker.add(profileModelWorker);
    }
}
