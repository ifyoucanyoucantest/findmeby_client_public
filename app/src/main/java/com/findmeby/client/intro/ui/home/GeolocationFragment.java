package com.findmeby.client.intro.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.findmeby.client.R;
import com.findmeby.client.intro.IntroMainActivity;
import com.findmeby.client.util.SharedPreferencesHelper;

import static androidx.core.content.ContextCompat.getSystemService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GeolocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GeolocationFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback, LocationListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GeolocationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment geolocationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GeolocationFragment newInstance(String param1, String param2) {
        GeolocationFragment fragment = new GeolocationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_geolocation, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.button_first4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View mainView = getView();
                if(((RadioButton)mainView.findViewById(R.id.radioButton)).isChecked())
                {
                    showGeoAccess();
                } else {
                    SharedPreferencesHelper.setGeoProvided(getActivity(),false);
                }

                SharedPreferencesHelper.setNavigateTo(getActivity(), R.id.action_nav_home_to_redButtonFragment);
                //Log.d("tst",((IntroMainActivity)getActivity()).getCurrentLocation().getLatitude() + "");
                NavHostFragment.findNavController(GeolocationFragment.this)
                        .navigate(R.id.action_geolocationFragment_to_redButtonFragment);
            }
        });
    }

    public void showGeoAccess() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("Permissions", "AlreadyGranted");
        } else {
            requestGeoAccess();
        }
    }



    public void requestGeoAccess() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                5);
        Log.d("I", "GeoRequested successfully");
    }

    @Override
    public void onLocationChanged(Location location) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        Log.d("Long", longitude + "");
        Log.d("Long", latitude + "");
    }
    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }


}