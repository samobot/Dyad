package com.samobot.smarthomecontrol;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class NewServiceFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_device_page, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) getActivity();
        activity.getSupportActionBar().setTitle(getString(R.string.addNewServiceSupportBarText));
        view.findViewById(R.id.newSericeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewDevice(v);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity) getActivity();
        activity.getSupportActionBar().setTitle(getString(R.string.addNewServiceSupportBarText));
    }

    public void addNewDevice(View view) {
        Intent intent = new Intent(getActivity(), NewServiceSelectorActivity.class);
        startActivity(intent);
    }

}
