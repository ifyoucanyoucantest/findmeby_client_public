package com.findmeby.client.intro.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.findmeby.client.R;
import com.findmeby.client.util.FindMeHttpUtil;
import com.findmeby.client.util.SharedPreferencesHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String userName = SharedPreferencesHelper.getUserName(getActivity());
        if(userName != null && !userName.isEmpty()) {
            ((TextView) getActivity().findViewById(R.id.editTextTextMultiLine)).setText(userName);
        }
        view.findViewById(R.id.button_first3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ProfileFragment.this)
                        .navigate(R.id.action_profileFragment2_to_SecondFragment);
            }
        });

        view.findViewById(R.id.button_first2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View mainView = getView();

                String userName = ((TextView) mainView.findViewById(R.id.editTextTextMultiLine)).getText().toString();
                if(userName == null || userName.isEmpty())
                {
                    ((TextView) mainView.findViewById(R.id.editTextTextMultiLine)).setError("Введите как вас представить");
                } else {
                    SharedPreferencesHelper.setUserName(getActivity(), userName);
                    Set<String> contactsSet = SharedPreferencesHelper.getContacts(getActivity());

                    //String [] arrayOfContacts = SharedPreferencesHelper.getContacts(getActivity());
                    String userId = SharedPreferencesHelper.generateUserId(getActivity());
                    userName = SharedPreferencesHelper.getUserName(getActivity());
                    Log.d("Test" , userId);
                    JSONObject object = new JSONObject();
                    JSONArray contactsArray = new JSONArray();
                    for (String s : contactsSet){
                        if (!s.isEmpty()){
                            contactsArray.put(s);
                        }
                    }
                    try {
                        object
                                .put("accountToken", userId)
                                .put("contacts", contactsArray)
                                .put("messageText", "...")
                                .put("userName", userName)
                                .put("sendPeriodicGeoData", true)
                                .put("letContactsSeeGeoHistory", true)
                                .put("currentTimestamp", new Date().getTime());
                    } catch (JSONException e) {
                        Log.d("JSONFailed","JSONFailed");
                    }

                    Log.d("JSON", object.toString());
                    Log.d("HTTP", "TryingToexecuteHTTP");
                    new RegisterRequest().execute(object.toString());

                    NavHostFragment.findNavController(ProfileFragment.this)
                            .navigate(R.id.action_profileFragment2_to_geolocationFragment);
                }
            }
        });
    }

    class RegisterRequest extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... arg) {
            Log.d("DOInBG","TEST");
            return FindMeHttpUtil.sendRequestAlarm("POST", "/api/v1/registerContacts", arg[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("s","onPostExecute");
            super.onPostExecute(s);
        }
    }
}