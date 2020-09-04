package com.shramik.india;

import android.app.Dialog;
import android.app.DownloadManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class PostFragment extends Fragment {
    List<String> name, industry, state, id, mobile;
    JobsAdapter jobsAdapter;
    View v;
    public PostFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_post, container, false);
        name = new ArrayList<>(); industry = new ArrayList<>(); state = new ArrayList<>(); id = new ArrayList<>(); mobile = new ArrayList<>();
        RecyclerView recyclerView = v.findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        jobsAdapter = new JobsAdapter("Post", getContext(), id, name, mobile, state, industry);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(jobsAdapter);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_FETCH_JOBS_MOBILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for(int i = 0; i<jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        id.add(0, jsonObject.getString("id"));
                        name.add(0, jsonObject.getString("name"));
                        mobile.add(0, jsonObject.getString("mobile"));
                        industry.add(0, jsonObject.getString("industry"));
                        state.add(0, jsonObject.getString("state"));
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
                Map<String , String> map = new HashMap<>();
                map.put("mobile", ProfileList.ProfileListEmployer.get(0).getNumber());
                return map;
            }
        };

        RequestHanler.getInstance(getContext()).addToRequestQueue(stringRequest);

        v.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new AddPost()).commit();
            }
        });

        return v;
    }
}