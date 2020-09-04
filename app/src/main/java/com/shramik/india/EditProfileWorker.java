package com.shramik.india;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class EditProfileWorker extends Fragment {
    Button save, back;
    EditText name, age, mobile;
    AutoCompleteTextView home_state, choice_of_state;
    String occp;
    List<String> fields = new ArrayList<>();
    FirebaseAuth firebaseAuth;
    int smartphone;
    String password;
    String id = "";
    ProfileModelWorker profileModelWorker;
    ProgressDialog progressDialog;
    String latitude = "", longitude = "";
    GPSTracker gpsTracker;
    String mVerificationId;
    TextView enterOTP;
    Dialog dialog;

    public EditProfileWorker() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.edit_profile_worker, container, false);
        gpsTracker = new GPSTracker(getContext());
        if(gpsTracker.canGetLocation){
            latitude = gpsTracker.getLatitude()+"";
            longitude = gpsTracker.getLongitude()+"";
        }else{
            gpsTracker.showSettingsAlert();
        }
        /*employer = v.findViewById(R.id.employerCard);
        labour = v.findViewById(R.id.labourCard);
        industry = v.findViewById(R.id.industryCard);
        construction = v.findViewById(R.id.constructionCard);
        farm = v.findViewById(R.id.farmCard);
        household = v.findViewById(R.id.householdCard);
        save = v.findViewById(R.id.save);
        name = v.findViewById(R.id.nameField);
        state = v.findViewById(R.id.stateField);*/
        firebaseAuth = FirebaseAuth.getInstance();
        final Bundle bundle = this.getArguments();
        progressDialog = new ProgressDialog(getContext());

        name = v.findViewById(R.id.name);
        age = v.findViewById(R.id.age);
        mobile = v.findViewById(R.id.mobile);
        home_state = v.findViewById(R.id.homeState);
        choice_of_state = v.findViewById(R.id.choiceState);
        save = v.findViewById(R.id.save);
        back = v.findViewById(R.id.back);
        smartphone = 0;

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ProfileViewWorker()).commit();
            }
        });

        if(bundle.getString("reference").equals("new_login")) {
            back.setVisibility(View.GONE);
            mobile.setText(firebaseAuth.getCurrentUser().getPhoneNumber().substring(3).trim());
            mobile.setEnabled(false);
            name.setText(bundle.getString("name"));
            mobile.setText(bundle.getString("mobile"));
            password = bundle.getString("password");
            smartphone = 1;
        }else if(bundle.getString("reference").equals("edit")) {
            ProfileModelWorker profileModelWorker = ProfileList.getProfileWorkerById(ProfileList.id);
            name.setText(profileModelWorker.getName());
            mobile.setText(profileModelWorker.getNumber());
            mobile.setEnabled(false);
            age.setText(profileModelWorker.getAge());
            home_state.setText(profileModelWorker.getHome_state());
            choice_of_state.setText(profileModelWorker.getChoice_of_state());
        }else{
            name.setText(bundle.getString("name"));
            mobile.setText(bundle.getString("mobile"));
            password = bundle.getString("password");
            mobile.setEnabled(false);
        }

        try{
            int permission = getActivity().checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permission+= getActivity().checkSelfPermission("Manifest.permission.CALL_PHONE");
            if(permission!=0){
                requestPermissions(new String[]{"Manifest.permission.ACCESS_FINE_LOCATION", "Manifest.permission.CALL_PHONE"}, 1001);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        String[] states = {"Andhra Pradesh",
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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1, states);
        home_state.setThreshold(1);
        home_state.setAdapter(adapter);

        choice_of_state.setThreshold(1);
        choice_of_state.setAdapter(adapter);

        v.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bundle.getString("reference").equals("new_login")) {
                    progressDialog.setMessage("Creating User...");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setIndeterminate(true);
                    progressDialog.show();
                    final StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_CREATE_WORKER, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            try {
                                final JSONObject jsonObject = new JSONObject(response);
                                Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                if (jsonObject.get("message").equals("Worker already registered")) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("A user already exists with this number. Do you wish to import the data?");
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            StringRequest request = new StringRequest(Request.Method.POST, Constants.URL_FETCH_EXISTING_WORKER, new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response1) {
                                                    try {
                                                        JSONArray jsonArray = new JSONArray(response1);
                                                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                                        ProfileModelWorker profileModelWorker = new ProfileModelWorker(jsonObject1.getString("id"), jsonObject1.getString("name"), jsonObject1.getString("age"),
                                                                jsonObject1.getString("mobile"), jsonObject1.getString("home_state"), jsonObject1.getString("choice_of_state"),
                                                                jsonObject1.getString("smartphone"), jsonObject1.getString("added_workers"), jsonObject1.getString("latitude"), jsonObject1.getString("longitude"));
                                                        ProfileList.setContext(getContext());
                                                        ProfileList.setId(jsonObject1.getString("id"));
                                                        ProfileList.setOccupation("Worker");
                                                        ProfileList.addDataWorker(profileModelWorker);
                                                        ProfileList.save();
                                                    } catch (Exception e) {
                                                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
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
                                                    map.put("mobile", mobile.getText().toString());
                                                    return map;
                                                }
                                            };

                                            RequestHanler.getInstance(getContext()).addToRequestQueue(request);
                                        }
                                    });
                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Toast.makeText(getContext(), "Re-enter your details", Toast.LENGTH_SHORT).show();
                                            EditProfileWorker editProfileWorker = new EditProfileWorker();
                                            Bundle bundle = new Bundle();
                                            bundle.putString("reference", "new_login");
                                            editProfileWorker.setArguments(bundle);
                                            FirebaseAuth.getInstance().signOut();
                                            Intent intent = new Intent(getActivity(), StartActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            getActivity().startActivity(intent);
                                        }
                                    });
                                    Dialog dialog = builder.create();
                                    dialog.show();
                                } else if (jsonObject.getString("message").equals("User Created Successfully")) {
                                    final ProgressDialog progressDialog = new ProgressDialog(getContext());
                                    progressDialog.setMessage("Saving Data...");
                                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    progressDialog.setIndeterminate(true);
                                    progressDialog.show();
                                    StringRequest request = new StringRequest(Request.Method.POST, Constants.URL_FETCH_ID, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONArray jsonArray = new JSONArray(response);
                                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                                id = jsonObject1.getString("id");
                                                Toast.makeText(getContext(), id, Toast.LENGTH_SHORT).show();
                                                ProfileModelWorker profileModelWorker = new ProfileModelWorker(id, name.getText().toString(), age.getText().toString(), mobile.getText().toString(), home_state.getText().toString(), choice_of_state.getText().toString(), smartphone + "", "0", latitude, longitude);
                                                ProfileList.setContext(getContext());
                                                ProfileList.setOccupation("Worker");
                                                ProfileList.setId(id);
                                                ProfileList.addDataWorker(profileModelWorker);
                                                ProfileList.save();
                                                Toast.makeText(getContext(), ProfileList.ProfileListWorker.size()+"", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ProfileViewWorker()).commit();
                                            } catch (Exception e) {
                                                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
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
                                            map.put("mobile", mobile.getText().toString());
                                            return map;
                                        }
                                    };

                                    RequestHanler.getInstance(getContext()).addToRequestQueue(request);
                                }

                            } catch (Exception e) {

                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> hashMap = new HashMap<>();
                            hashMap.put("name", name.getText().toString());
                            hashMap.put("age", age.getText().toString());
                            hashMap.put("mobile", mobile.getText().toString());
                            hashMap.put("home_state", home_state.getText().toString());
                            hashMap.put("choice_of_state", choice_of_state.getText().toString());
                            hashMap.put("password", password);
                            hashMap.put("smartphone", smartphone + "");
                            hashMap.put("added_workers", "0");
                            //blXr#zirQqr.
                            hashMap.put("latitude", latitude);
                            hashMap.put("longitude", longitude);
                            return hashMap;
                        }
                    };

                    RequestHanler.getInstance(getContext()).addToRequestQueue(stringRequest);
                }else if(bundle.getString("reference").equals("login")){
                    if(mobile.getText().toString().length()!=10){
                        Toast.makeText(getContext(), "Invalid Number", Toast.LENGTH_SHORT).show();
                    }else{
                        sendVerificationCode(mobile.getText().toString());
                        dialog = new Dialog(getContext());
                        dialog.setContentView(R.layout.enter_otp);
                        dialog.getWindow().getAttributes().width = WindowManager.LayoutParams.FILL_PARENT;
                        dialog.getWindow().getAttributes().height = WindowManager.LayoutParams.FILL_PARENT;
                        Button completeReg = dialog.findViewById(R.id.completeReg);
                        enterOTP = dialog.findViewById(R.id.enterOTP);
                        completeReg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                verifyVerificationCode(enterOTP.getText().toString());
                            }
                        });
                        dialog.setCancelable(false);
                        dialog.show();
                    }
                }else{
                    final ProgressDialog progressDialog = new ProgressDialog(getContext());
                    progressDialog.setMessage("Updating...");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setIndeterminate(true);
                    progressDialog.show();
                    StringRequest request = new StringRequest(Request.Method.POST, Constants.URL_UPDATE_WORKER_DATA, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressDialog.dismiss();
                                JSONObject jsonObject1 = new JSONObject(response);
                                if(jsonObject1.getString("message").equals("update successful")){
                                    Toast.makeText(getContext(), "update successful", Toast.LENGTH_SHORT).show();
                                    int pos = ProfileList.getProfileWorkerNumberById(ProfileList.id);
                                    ProfileModelWorker profileModelWorker1 = ProfileList.ProfileListWorker.get(pos);
                                    if(pos == 0) {
                                        profileModelWorker = new ProfileModelWorker(id, name.getText().toString(), age.getText().toString(), profileModelWorker1.getNumber(), home_state.getText().toString(), choice_of_state.getText().toString()
                                                , "1", profileModelWorker1.getAdded_workers(), profileModelWorker1.getLatitude(), profileModelWorker1.getLongitude());
                                    }else{
                                        profileModelWorker = new ProfileModelWorker(id, name.getText().toString(), age.getText().toString(), profileModelWorker1.getNumber(), home_state.getText().toString(), choice_of_state.getText().toString()
                                               , "0", profileModelWorker1.getAdded_workers(), profileModelWorker1.getLatitude(), profileModelWorker1.getLongitude());
                                    }
                                    ProfileList.ProfileListWorker.set(pos, profileModelWorker);
                                    ProfileList.save();
                                }
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ProfileViewWorker()).commit();
                            }catch (Exception e){
                                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String , String> hashMap = new HashMap<>();
                            hashMap.put("id", ProfileList.id);
                            hashMap.put("name", name.getText().toString());
                            hashMap.put("age", age.getText().toString());
                            hashMap.put("home_state", home_state.getText().toString());
                            hashMap.put("choice_of_state", choice_of_state.getText().toString());
                            return hashMap;
                        }
                    };

                    RequestHanler.getInstance(getContext()).addToRequestQueue(request);
                }
            }
        });

        /*employer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                employer.setCardBackgroundColor(getActivity().getResources().getColor(R.color.colorHighlight));
                labour.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
                employer.setEnabled(false);
                labour.setEnabled(true);
                occp = "Employer";
            }
        });

        labour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                labour.setCardBackgroundColor(getActivity().getResources().getColor(R.color.colorHighlight));
                employer.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
                labour.setEnabled(false);
                employer.setEnabled(true);
                occp = "Labour";
            }
        });

        industry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                industry.setCardBackgroundColor(getActivity().getResources().getColor(R.color.colorHighlight));
                industry.setEnabled(false);
                fields.add("Industry");
            }
        });

        construction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                construction.setCardBackgroundColor(getActivity().getResources().getColor(R.color.colorHighlight));
                construction.setEnabled(false);
                fields.add("Construction");
            }
        });

        farm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                farm.setCardBackgroundColor(getActivity().getResources().getColor(R.color.colorHighlight));
                farm.setEnabled(false);
                fields.add("Farm");
            }
        });

        household.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                household.setCardBackgroundColor(getActivity().getResources().getColor(R.color.colorHighlight));
                household.setEnabled(false);
                fields.add("Household");
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileList.setContext(getActivity().getApplicationContext());
                ProfileList.load();
                ProfileModel profileModel = new ProfileModel(name.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(3).trim(), occp, state.getText().toString(), fields, true);
                ProfileList.addData(profileModel);
            }
        });*/

        return v;
    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }


    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                enterOTP.setText(code);
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Verifying Code...");
                progressDialog.show();
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
        }
    };


    private void verifyVerificationCode(String code) {
        //creating the credential
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            signInWithPhoneAuthCredential(credential);
        }catch (Exception e){
            Toast toast = Toast.makeText(getActivity(), "Verification Code is wrong", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final ProgressDialog progressDialog = new ProgressDialog(getContext());
                            progressDialog.setMessage("Creating User...");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.setIndeterminate(true);
                            progressDialog.show();
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_CREATE_WORKER, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        progressDialog.dismiss();
                                        JSONObject jsonObject = new JSONObject(response);
                                        if (jsonObject.get("message").equals("Worker already registered")) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                            builder.setTitle("A user already exists with this number. Do you wish to import the data?");
                                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    final ProgressDialog progressDialog1 = new ProgressDialog(getContext());
                                                    progressDialog1.setMessage("Please wait while we are loading the resources...");
                                                    progressDialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                                    progressDialog1.setIndeterminate(true);
                                                    progressDialog1.show();
                                                    StringRequest request = new StringRequest(Request.Method.POST, Constants.URL_FETCH_EXISTING_WORKER, new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response1) {
                                                            progressDialog1.dismiss();
                                                            try {
                                                                JSONArray jsonArray = new JSONArray(response1);
                                                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                                                ProfileModelWorker profileModelWorker = new ProfileModelWorker(jsonObject1.getString("id"), jsonObject1.getString("name"), jsonObject1.getString("age"),
                                                                        jsonObject1.getString("mobile"), jsonObject1.getString("home_state"), jsonObject1.getString("choice_of_state"),
                                                                        jsonObject1.getString("smartphone"), jsonObject1.getString("added_workers"), jsonObject1.getString("latitude"), jsonObject1.getString("longitude"));
                                                                ProfileList.setContext(getContext());
                                                                ProfileList.setId(jsonObject1.getString("id"));
                                                                ProfileList.setOccupation("Worker");
                                                                ProfileList.addDataWorker(profileModelWorker);
                                                                ProfileList.save();
                                                            } catch (Exception e) {
                                                                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
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
                                                            map.put("mobile", mobile.getText().toString());
                                                            map.put("table", "workers");
                                                            return map;
                                                        }
                                                    };

                                                    RequestHanler.getInstance(getContext()).addToRequestQueue(request);
                                                }
                                            });
                                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Toast.makeText(getContext(), "Re-enter your details", Toast.LENGTH_SHORT).show();
                                                    EditProfileWorker editProfileWorker = new EditProfileWorker();
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("reference", "login");
                                                    editProfileWorker.setArguments(bundle);
                                                    FirebaseAuth.getInstance().signOut();
                                                    Intent intent = new Intent(getActivity(), StartActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    getActivity().startActivity(intent);
                                                }
                                            });
                                            Dialog dialog = builder.create();
                                            dialog.show();
                                        }else if(jsonObject.getString("message").equals("User Created Successfully")){
                                            StringRequest request = new StringRequest(Request.Method.POST, Constants.URL_FETCH_ID, new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONArray jsonArray = new JSONArray(response);
                                                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                                        id = jsonObject1.getString("id");
                                                        ProfileModelWorker profileModelWorker = new ProfileModelWorker(id, name.getText().toString(), age.getText().toString(), mobile.getText().toString(), home_state.getText().toString(), choice_of_state.getText().toString(), smartphone + "", "0", latitude, longitude);
                                                        ProfileList.setContext(getContext());
                                                        ProfileList.setOccupation("Worker");
                                                        ProfileList.addDataWorker(profileModelWorker);
                                                        ProfileList.save();
                                                        progressDialog.dismiss();
                                                        dialog.dismiss();
                                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ProfileViewWorker()).commit();
                                                    } catch (Exception e) {
                                                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
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
                                                    map.put("mobile", mobile.getText().toString());
                                                    return map;
                                                }
                                            };

                                            RequestHanler.getInstance(getContext()).addToRequestQueue(request);
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
                                    Map<String, String> hashMap = new HashMap<>();
                                    hashMap.put("name", name.getText().toString());
                                    hashMap.put("age", age.getText().toString());
                                    hashMap.put("mobile", mobile.getText().toString());
                                    hashMap.put("home_state", home_state.getText().toString());
                                    hashMap.put("choice_of_state", choice_of_state.getText().toString());
                                    hashMap.put("password", password);
                                    hashMap.put("smartphone", smartphone + "");
                                    hashMap.put("added_workers", "0");
                                    //blXr#zirQqr.
                                    hashMap.put("latitude", latitude);
                                    hashMap.put("longitude", longitude);
                                    return hashMap;
                                }
                            };

                            RequestHanler.getInstance(getContext()).addToRequestQueue(stringRequest);
                        } else {
                            progressDialog.dismiss();

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

}