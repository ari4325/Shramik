package com.shramik.india;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class ProfileViewEmployer extends Fragment {
    public ProfileViewEmployer() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_view_employer, container, false);
        ProfileModelEmployer profileModelEmployer = ProfileList.ProfileListEmployer.get(0);
        TextView name, age, mobile, home_state;
        name = v.findViewById(R.id.name);
        age = v.findViewById(R.id.age);
        mobile = v.findViewById(R.id.mobile);
        home_state = v.findViewById(R.id.homeState);
        name.setText(profileModelEmployer.getName());
        age.setText(profileModelEmployer.getIndustry());
        mobile.setText(profileModelEmployer.getNumber());
        home_state.setText(profileModelEmployer.getState());
        ImageView edit = v.findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               EditProfileEmployer editProfileEmployer = new EditProfileEmployer();
               Bundle bundle = new Bundle();
               bundle.putString("reference", "existing");
               editProfileEmployer.setArguments(bundle);
               getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, editProfileEmployer).commit();
            }
        });
        return v;
    }
}