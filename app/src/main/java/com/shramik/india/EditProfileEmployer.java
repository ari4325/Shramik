package com.shramik.india;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditProfileEmployer extends Fragment {
    EditText name, mobile, industry;
    String password;
    AutoCompleteTextView state;
    Bundle bundle;
    String id;
    GPSTracker gpsTracker;
    String latitude = "";
    String longitude = "";


    public EditProfileEmployer() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_profile_employer, container, false);
        name = v.findViewById(R.id.name);
        mobile = v.findViewById(R.id.mobile);
        industry = v.findViewById(R.id.industry);
        state = v.findViewById(R.id.state);
        bundle = getArguments();

        try {
            password = bundle.getString("password");
        }catch (Exception e){
            e.printStackTrace();
        }

        gpsTracker = new GPSTracker(getContext());
        if(gpsTracker.canGetLocation){
            latitude = gpsTracker.getLatitude()+"";
            longitude = gpsTracker.getLongitude()+"";
        }else{
            gpsTracker.showSettingsAlert();
        }

        if(bundle.getString("reference").equals("new_login")){
            mobile.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(3).trim());
            mobile.setEnabled(false);
        }else{
            name.setText(ProfileList.ProfileListEmployer.get(0).getName());
            state.setText(ProfileList.ProfileListEmployer.get(0).getState());
            industry.setText(ProfileList.ProfileListEmployer.get(0).getIndustry());
            mobile.setText(ProfileList.ProfileListEmployer.get(0).getNumber());
            mobile.setEnabled(false);
        }

        final String[] states = {"Andhra Pradesh",
                "Arunachal Pradesh",
                "Assam",
                "Bihar",
                "Chhattisgarh",
                "Goa",
                "Gujarat",
                "Haryana",
                "Himachal Pradesh",
                "Jammu and Kashmir",
                "Jharkhand",
                "Karnataka",
                "Kerala",
                "Madhya Pradesh",
                "Maharashtra",
                "Manipur",
                "Meghalaya",
                "Mizoram",
                "Nagaland",
                "Odisha",
                "Punjab",
                "Rajasthan",
                "Sikkim",
                "Tamil Nadu",
                "Telangana",
                "Tripura",
                "Uttarakhand",
                "Uttar Pradesh",
                "West Bengal",
                "Andaman and Nicobar Islands",
                "Chandigarh",
                "Dadra and Nagar Haveli",
                "Daman and Diu",
                "Delhi",
                "Lakshadweep",
                "Puducherry"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, states);
        state.setThreshold(1);
        state.setAdapter(adapter);

        v.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bundle.getString("reference").equals("new_login")){
                    StringRequest request = new StringRequest(Request.Method.POST, Constants.URL_CREATE_EMPLOYER, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                final JSONObject jsonObject = new JSONObject(response);
                                if(jsonObject.getBoolean("error")){
                                    Toast.makeText(getContext(), "There was some error, please check your internet connection and try again" , Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getContext(), "Profile Created Successfully" , Toast.LENGTH_SHORT).show();
                                    final ProgressDialog progressDialog = new ProgressDialog(getContext());
                                    progressDialog.setIndeterminate(true);
                                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    progressDialog.setMessage("Saving Data...");
                                    progressDialog.show();
                                    StringRequest request1 = new StringRequest(Request.Method.POST, Constants.URL_FETCH_ID_EMPLOYER, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONArray jsonArray = new JSONArray(response);
                                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                                id = jsonObject1.getString("id");
                                                ProfileModelEmployer profileModelEmployer = new ProfileModelEmployer(id, name.getText().toString(), mobile.getText().toString(), state.getText().toString(), industry.getText().toString()
                                                            , latitude, longitude);
                                                ProfileList.setContext(getContext());
                                                ProfileList.load();
                                                ProfileList.setOccupation("Employer");
                                                ProfileList.addDataEmployer(profileModelEmployer);
                                                ProfileList.save();
                                                progressDialog.dismiss();
                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ProfileViewEmployer()).commit();
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
                                            map.put("mobile" , FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(3).trim());
                                            return map;
                                        }
                                    };

                                    RequestHanler.getInstance(getContext()).addToRequestQueue(request1);
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
                            map.put("name", name.getText().toString());
                            map.put("mobile", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(3).trim());
                            map.put("state", state.getText().toString());
                            map.put("industry", industry.getText().toString());
                            map.put("password", password);
                            return map;
                        }
                    };

                    RequestHanler.getInstance(getContext()).addToRequestQueue(request);
                }else{
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_UPDATE_EMPLOYER, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if(jsonObject.getBoolean("error")){
                                    Toast.makeText(getContext(), "There was some error. Please try again later.", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getContext(), "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ProfileViewEmployer()).commit();
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
                            map.put("state", state.getText().toString());
                            map.put("industry", state.getText().toString());
                            map.put("id", ProfileList.ProfileListEmployer.get(0).getId());
                            return map;
                        }
                    };

                    RequestHanler.getInstance(getContext()).addToRequestQueue(stringRequest);
                }
            }
        });

        return v;
    }
}