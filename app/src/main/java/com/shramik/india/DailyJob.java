package com.shramik.india;

import android.location.Location;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DailyJob extends Fragment {
    String latitude, longitude;
    ProfileModelEmployer profileModelEmployer;
    List<String> names, age, mobile, workers;
    WorkerAdapter workerAdapter;
    int k;



    public DailyJob() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_daily_job, container, false);
        ProfileList.load();

        GPSTracker gpsTracker = new GPSTracker(getContext());
        names = new ArrayList<>(); age = new ArrayList<>(); mobile = new ArrayList<>(); workers = new ArrayList<>();
            profileModelEmployer = ProfileList.ProfileListEmployer.get(0);
            latitude = profileModelEmployer.getLatitude();
            longitude = profileModelEmployer.getLongitude();

        if(latitude.equals("0.0") || longitude.equals("0.0")){
            if(gpsTracker.canGetLocation){
                latitude = gpsTracker.getLatitude()+"";
                longitude = gpsTracker.getLongitude()+"";
                ProfileList.ProfileListEmployer.get(0).latitude = latitude;
                ProfileList.ProfileListEmployer.get(0).longitude = longitude;
                ProfileList.save();
            }else{
                gpsTracker.showSettingsAlert();
            }
        }

        Toast.makeText(getContext(), latitude+" "+longitude, Toast.LENGTH_SHORT).show();

        RecyclerView recyclerView = v.findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        workerAdapter = new WorkerAdapter(getContext(), names, age, mobile, workers);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(workerAdapter);

        final StringRequest request = new StringRequest(Request.Method.POST, Constants.URL_GET_LAT_LONG, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    k = 0;
                    names.clear();
                    age.clear();
                    mobile.clear();
                    workers.clear();
                    JSONArray jsonArray = new JSONArray(response);
                    for(int i = 0; i<jsonArray.length(); i++){
                        final JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Location location1 = new Location("Employer Point");
                        try {
                            location1.setLatitude(Double.parseDouble(latitude));
                            location1.setLongitude(Double.parseDouble(longitude));
                            Location location2 = new Location("Worker Point");
                            location2.setLatitude(Double.parseDouble(jsonObject.getString("latitude")));
                            location2.setLongitude(Double.parseDouble(jsonObject.getString("longitude")));
                            int distance = (int) location1.distanceTo(location2);
                            Toast.makeText(getContext(), distance+"", Toast.LENGTH_SHORT).show();
                            if (distance < 10000) {
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_FETCH_WORKER_LAT_LONG, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONArray jsonArray1 = new JSONArray(response);
                                            for (int j = 0; j < jsonArray1.length(); j++) {
                                                JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                                                names.add(jsonObject1.getString("name"));
                                                age.add(jsonObject1.getString("age"));
                                                mobile.add(jsonObject1.getString("mobile"));
                                                workers.add(jsonObject1.getString("workers"));
                                                workerAdapter.notifyItemInserted(0);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> map = new HashMap<>();
                                        try {
                                            map.put("latitude", jsonObject.getString("latitude"));
                                            map.put("longitude", jsonObject.getString("longitude"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        return map;
                                    }
                                };

                                RequestHanler.getInstance(getContext()).addToRequestQueue(stringRequest);
                            }
                        }catch (Exception e){
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("home_state", profileModelEmployer.getState());
                return map;
            }
        };

        RequestHanler.getInstance(getContext()).addToRequestQueue(request);
        return v;
    }
}