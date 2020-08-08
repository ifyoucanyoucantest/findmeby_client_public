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
import java.util.HashSet;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ContactsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactsFragment newInstance(String param1, String param2) {
        ContactsFragment fragment = new ContactsFragment();
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
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Set<String> setOfContacts = SharedPreferencesHelper.getContacts(getActivity());

        if(setOfContacts != null) {
            int contactsSize = setOfContacts.size();
            if (contactsSize != 0) {
                String[] arrayOfContacts = new String[contactsSize];
                setOfContacts.toArray(arrayOfContacts);

                View mainView = getView();
                ((TextView) mainView.findViewById(R.id.editTextTextMultiLine2)).setText(arrayOfContacts[0]);

                if (contactsSize > 1) {
                    ((TextView) mainView.findViewById(R.id.editTextTextMultiLine3)).setText(arrayOfContacts[1]);

                    if (contactsSize > 2) {
                        ((TextView) mainView.findViewById(R.id.editTextTextMultiLine4)).setText(arrayOfContacts[2]);
                    }
                }
            }
        }


        view.findViewById(R.id.button_first7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ContactsFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });

        view.findViewById(R.id.button_first6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View mainView = getView();

                String firstContact = ((TextView) mainView.findViewById(R.id.editTextTextMultiLine2)).getText().toString();
                String secondContact = ((TextView) mainView.findViewById(R.id.editTextTextMultiLine3)).getText().toString();
                String thirdContact = ((TextView) mainView.findViewById(R.id.editTextTextMultiLine4)).getText().toString();

                HashSet<String> contactsSet = new HashSet<>();
                if(!firstContact.isEmpty()) {
                    contactsSet.add(firstContact);
                }
                if(!secondContact.isEmpty()) {
                    contactsSet.add(secondContact);
                }
                if(!thirdContact.isEmpty()) {
                    contactsSet.add(thirdContact);
                }
                if(firstContact.isEmpty() && secondContact.isEmpty() && thirdContact.isEmpty()) {
                    ((TextView) mainView.findViewById(R.id.editTextTextMultiLine2)).setError("Введите хотя бы один контакт");
                    return;
                } else {
                    SharedPreferencesHelper.setContacts(getActivity(), contactsSet);

                    NavHostFragment.findNavController(ContactsFragment.this)
                            .navigate(R.id.action_SecondFragment_to_profileFragment2);
                }
            }
        });
    }
}