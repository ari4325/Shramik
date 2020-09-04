package com.shramik.india;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Switch;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {
    Button signInBtn;
    TextView enterName;
    TextView signIn;
    TextView enterMobile;
    TextView enterPass;
    TextView confirmPass, enterOTP;
    Button button;
    Button button2, button3;
    Dialog dialog;
    RadioGroup radioGroup;
    String id, name, mobile, password;
    FirebaseAuth firebaseAuth;
    String mVerificationId;
    ProgressDialog progressDialog;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.sign_up);

        checkPermissions();

        enterMobile = findViewById(R.id.enterMobile);
        enterName = findViewById(R.id.enterName);
        enterPass = findViewById(R.id.enterPass);
        confirmPass = findViewById(R.id.confirmPass);
        button = findViewById(R.id.button);
        radioGroup = findViewById(R.id.radio);
        signIn = findViewById(R.id.signIn);

        id = "Employer";
        ProfileList.setContext(getApplicationContext());
        ProfileList.load();

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(StartActivity.this);
                dialog.setContentView(R.layout.sign_in);
                final TextView mobile = dialog.findViewById(R.id.enterNumber);
                final TextView password = dialog.findViewById(R.id.enterPassword);
                signInBtn = dialog.findViewById(R.id.signInBtn);
                dialog.getWindow().getAttributes().height = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
                signInBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mobile.getText().toString().length()!=0 && password.getText().toString().length()!=0){
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SIGN_IN_WORKER, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        if(jsonObject.getString("message").equals("signed in successfully")){
                                            StringRequest request = new StringRequest(Request.Method.POST, Constants.URL_FETCH_EXISTING_WORKER, new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONArray jsonArray = new JSONArray(response);
                                                        JSONObject jsonObject2 = jsonArray.getJSONObject(0);
                                                        if(id.equals("Worker")) {
                                                            ProfileModelWorker profileModelWorker = new ProfileModelWorker(jsonObject2.getString("id"), jsonObject2.getString("name"), jsonObject2.getString("age"),
                                                                    jsonObject2.getString("mobile"), jsonObject2.getString("home_state"), jsonObject2.getString("choice_of_state"),
                                                                    jsonObject2.getString("smartphone"), jsonObject2.getString("added_workers"), jsonObject2.getString("latitude"), jsonObject2.getString("longitude"));
                                                            ProfileList.setContext(getApplicationContext());
                                                            ProfileList.addDataWorker(profileModelWorker);
                                                            ProfileList.id = jsonObject2.getString("id");
                                                            ProfileList.occp = "Worker";
                                                            ProfileList.save();
                                                            Intent intent = new Intent(StartActivity.this, MainActivity.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            intent.putExtra("id", "");
                                                            startActivity(intent);
                                                        }else{
                                                            ProfileModelEmployer profileModelEmployer = new ProfileModelEmployer(jsonObject2.getString("id"), jsonObject2.getString("name"), jsonObject2.getString("mobile"),
                                                                    jsonObject2.getString("state"), jsonObject2.getString("industry"), "0.0", "0.0");
                                                            ProfileList.setContext(getApplicationContext());
                                                            ProfileList.addDataEmployer(profileModelEmployer);
                                                            ProfileList.id = jsonObject2.getString("id");
                                                            ProfileList.occp = "Employer";
                                                            ProfileList.save();
                                                            Intent intent = new Intent(StartActivity.this, MainActivity.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            intent.putExtra("id", "");
                                                            startActivity(intent);
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
                                                    map.put("mobile", mobile.getText().toString());
                                                    map.put("table", id.equals("Worker")?"workers":"employers");
                                                    return map;
                                                }
                                            };
                                            RequestHanler.getInstance(getApplicationContext()).addToRequestQueue(request);
                                        }else{
                                            Toast.makeText(getApplicationContext(), "User does not exist. Register Now", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
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
                                    map.put("mobile", mobile.getText().toString());
                                    map.put("password", password.getText().toString());
                                    map.put("table", id.equals("Worker")?"workers":"employers");
                                    return map;
                                }
                            };
                            RequestHanler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                        }else{
                            Toast.makeText(getApplicationContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.setCancelable(false);
                dialog.show();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.radioBtnEmployer:
                        id = "Employer";
                        break;
                    case R.id.radioBtnWorker:
                        id = "Worker";
                        break;
                }
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null){
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("id", "");
            startActivity(intent);
        }else if(!ProfileList.occp.equals("")){
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("id", "");
            startActivity(intent);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(enterName.getText().toString().length()!=0 && enterMobile.getText().toString().length()!=0 || enterPass.getText().toString().length()!=0 || confirmPass.getText().toString().length()!=0){
                    if(enterPass.getText().toString().equals(confirmPass.getText().toString())) {
                        StringRequest request = new StringRequest(Request.Method.POST, Constants.URL_ALREADY_REGISTER, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                ProgressDialog progressDialog1 = new ProgressDialog(StartActivity.this);
                                progressDialog1.setIndeterminate(true);
                                progressDialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog1.setMessage("Loading...");
                                progressDialog1.show();
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if(jsonObject.getString("message").equals("user not created")){
                                        progressDialog1.dismiss();
                                        name = enterName.getText().toString();
                                        mobile = enterMobile.getText().toString();
                                        password = enterPass.getText().toString();
                                        sendVerificationCode(mobile);
                                        dialog = new Dialog(StartActivity.this);
                                        dialog.setContentView(R.layout.enter_otp);
                                        dialog.getWindow().getAttributes().width = WindowManager.LayoutParams.FILL_PARENT;
                                        dialog.getWindow().getAttributes().height = WindowManager.LayoutParams.FILL_PARENT;
                                        enterOTP = dialog.findViewById(R.id.enterOTP);
                                        button2 = dialog.findViewById(R.id.completeReg);
                                        button3 = dialog.findViewById(R.id.button3);
                                        button2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                progressDialog = new ProgressDialog(StartActivity.this);
                                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                                progressDialog.setIndeterminate(true);
                                                progressDialog.setMessage("Verifying Code...");
                                                progressDialog.show();
                                                verifyVerificationCode(enterOTP.getText().toString());
                                            }
                                        });
                                        button3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();
                                            }
                                        });
                                        dialog.setCancelable(false);
                                        dialog.show();
                                    }else{
                                        progressDialog1.dismiss();
                                        Toast.makeText(getApplicationContext(), "User already created. Login now", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> map = new HashMap<>();
                                map.put("mobile", enterMobile.getText().toString());
                                map.put("table", (id.equals("Worker")?"workers":"employers"));
                                return map;
                            }
                        };

                        RequestHanler.getInstance(getApplicationContext()).addToRequestQueue(request);
                    }else{
                        Toast.makeText(getApplicationContext(), "Passwords dont match", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Fill all the fields before signing up", Toast.LENGTH_SHORT).show();
                }
            }
        });



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
                progressDialog = new ProgressDialog(StartActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Verifying Code...");
                progressDialog.show();
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(StartActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
            Toast toast = Toast.makeText(this, "Verification Code is wrong", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(StartActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Intent intent = new Intent(StartActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("id", id);
                            intent.putExtra("name", name);
                            intent.putExtra("mobile", mobile);
                            intent.putExtra("password", password);
                            startActivity(intent);
                        } else {
                            progressDialog.dismiss();

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Toast.makeText(StartActivity.this, message, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        progressDialog = new ProgressDialog(StartActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Verifying Code...");
        progressDialog.show();
        verifyVerificationCode(enterOTP.getText().toString());
    }

    void checkPermissions(){
        int permission = checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
        permission+=checkSelfPermission("Manifest.permission.CALL_PHONE");

        if(permission!=0){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE}, 1001);
        }
    }
}