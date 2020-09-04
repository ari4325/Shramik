package com.shramik.india;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.Bundle;

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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SearchWorker extends Fragment {
    String state;
    List<String> name, mobile, states, industry;
    JobsAdapter jobsAdapter;
    int l;


    public SearchWorker() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search_worker, container, false);
        state = ProfileList.ProfileListWorker.get(0).getChoice_of_state();
        mobile = new ArrayList<>();
        name = new ArrayList<>(); states = new ArrayList<>(); industry = new ArrayList<>();
        RecyclerView recyclerView = v.findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        final ProfileModelWorker profileModelWorker = ProfileList.getProfileWorkerById(ProfileList.id);
        jobsAdapter = new JobsAdapter("Worker", getContext(), null, name, mobile, states, industry);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(jobsAdapter);
        final ProgressDialog progressDialog1 = new ProgressDialog(getContext());
        progressDialog1.setMessage("Please wait while we are loading the jobs...");
        progressDialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog1.setIndeterminate(true);
        progressDialog1.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constants.URL_FETCH_JOBS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    progressDialog1.dismiss();
                    JSONArray jsonArray = new JSONArray(response);
                    for(int i = 0 ; i<jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        name.add(0, jsonObject.getString( "name"));
                        mobile.add(0, jsonObject.getString("mobile"));
                        states.add(0, jsonObject.getString("state"));
                        industry.add(0, jsonObject.getString("industry"));
                        jobsAdapter.notifyItemInserted(0);
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
                map.put("choice_of_state", profileModelWorker.getChoice_of_state());
                return map;
            }
        };

        RequestHanler.getInstance(getContext()).addToRequestQueue(request);
        return v;
    }
}