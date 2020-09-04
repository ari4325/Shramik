package com.shramik.india;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AddWorker extends Fragment {
    TextView enterName, enterMobile, enterPass, confirmPass;
    Button add;
    ProgressDialog progressDialog;
    String mVerificationId;
    TextView enterOTP;

    public AddWorker() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_worker, container, false);
        enterName = v.findViewById(R.id.enterName);
        enterMobile = v.findViewById(R.id.enterMobile);
        enterPass = v.findViewById(R.id.enterPass);
        confirmPass = v.findViewById(R.id.confirmPass);
        add = v.findViewById(R.id.button);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(enterName.getText().toString().length()!=0 && enterMobile.getText().toString().length()!=0 || enterPass.getText().toString().length()!=0 || confirmPass.getText().toString().length()!=0){
                    if(enterPass.getText().toString().equals(confirmPass.getText().toString())) {
                        if(enterMobile.getText().toString().length()!=10){
                            Toast.makeText(getContext(), "Mobile Number Invalid. Please check an try again", Toast.LENGTH_SHORT).show();
                        }else {
                            final ProgressDialog progressDialog = new ProgressDialog(getContext());
                            progressDialog.setIndeterminate(true);
                            progressDialog.setMessage("Adding Worker...");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.show();
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_INCREASE_ADDED_WORKERS, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        if(jsonObject.getBoolean("error")){
                                            Toast.makeText(getContext(), "There was some error, try again", Toast.LENGTH_SHORT).show();
                                        }else{
                                            progressDialog.dismiss();
                                            EditProfileWorker editProfileWorker = new EditProfileWorker();
                                            Bundle bundle = new Bundle();
                                            bundle.putString("reference", "login");
                                            bundle.putString("name", enterName.getText().toString());
                                            bundle.putString("mobile", enterMobile.getText().toString());
                                            bundle.putString("password", enterPass.getText().toString());
                                            editProfileWorker.setArguments(bundle);
                                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, editProfileWorker).commit();
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
                                    map.put("id", ProfileList.ProfileListWorker.get(0).getId());
                                    return map;
                                }
                            };

                            RequestHanler.getInstance(getContext()).addToRequestQueue(stringRequest);
                        }
                    }
                }
            }
        });
        return v;
    }
}