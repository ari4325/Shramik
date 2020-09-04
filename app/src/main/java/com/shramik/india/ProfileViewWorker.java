package com.shramik.india;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileViewWorker extends Fragment {
    List<ProfileModelWorker> profileModelWorkers = new ArrayList<>();
    List<String> names = new ArrayList<>();
    TextView name, age, mobile;
    TextView home_state, choice_of_state;
    Button addWorker;
    String id;

    public ProfileViewWorker() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_view, container, false);
        ProfileList.setContext(getActivity().getApplicationContext());
        ProfileList.load();
        profileModelWorkers = ProfileList.ProfileListWorker;
        for(int i = 0;i <profileModelWorkers.size(); i++){
            ProfileModelWorker profileModelWorker = profileModelWorkers.get(i);
            names.add(profileModelWorker.getName());
        }

        addWorker = v.findViewById(R.id.addWorker);
        addWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new AddWorker()).commit();
            }
        });

        final ImageView edit = v.findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProfileWorker editProfileWorker = new EditProfileWorker();
                Bundle bundle = new Bundle();
                bundle.putString("reference", "edit");
                editProfileWorker.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, editProfileWorker).commit();
            }
        });

        final ProfileModelWorker profileModelWorker = ProfileList.getProfileWorkerById(ProfileList.id);
        name = v.findViewById(R.id.name);
        age = v.findViewById(R.id.age);
        mobile = v.findViewById(R.id.mobile);
        home_state = v.findViewById(R.id.homeState);
        choice_of_state = v.findViewById(R.id.choiceState);
        name.setText(profileModelWorker.getName());
        age.setText(profileModelWorker.getAge());
        mobile.setText(profileModelWorker.getNumber());
        home_state.setText(profileModelWorker.getHome_state());
        choice_of_state.setText(profileModelWorker.getChoice_of_state());

        Spinner spinner = v.findViewById(R.id.workers);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, names);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                id = profileModelWorkers.get(i).getId();
                ProfileList.setId(id);
                ProfileList.save();
                ProfileModelWorker profileModelWorker1 = profileModelWorkers.get(i);
                name.setText(profileModelWorker1.getName());
                age.setText(profileModelWorker1.getAge());
                mobile.setText(profileModelWorker1.getNumber());
                home_state.setText(profileModelWorker1.getHome_state());
                choice_of_state.setText(profileModelWorker1.getChoice_of_state());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        return v;
    }
}