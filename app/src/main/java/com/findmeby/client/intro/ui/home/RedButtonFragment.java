package com.findmeby.client.intro.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.findmeby.client.R;
import com.findmeby.client.intro.IntroMainActivity;
import com.findmeby.client.service.RebuttonService;
import com.findmeby.client.util.FindMeHttpUtil;
import com.findmeby.client.util.SharedPreferencesHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RedButtonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RedButtonFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public RedButtonFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static RedButtonFragment newInstance(String param1, String param2) {
        RedButtonFragment fragment = new RedButtonFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_red_button, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //if(!SharedPreferencesHelper.getBackGroundRun(getActivity())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getActivity().startForegroundService(new Intent(getActivity(), RebuttonService.class));
            } else {
                getActivity().startService(new Intent(getActivity(), RebuttonService.class));
            }
            SharedPreferencesHelper.setBackGroundRun(getActivity(), true);
        //}
        ((SwitchCompat) view.findViewById(R.id.switch1)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                //Location currentLocation = ((IntroMainActivity) getActivity()).getCurrentLocation();
                JSONObject object = new JSONObject();
                SharedPreferencesHelper.setDangerMode(getActivity(),true);
                String userId = SharedPreferencesHelper.generateUserId(getActivity());

                try {
                    object
                            .put("accountToken", userId)
                            .put("location", new JSONObject()
                                    .put("lat", null)
                                    .put("lng", null)
                                    .put("timestamp", new Date().getTime()))
                            .put("newStatus", "risky")
                            .put("currentTimestamp", new Date().getTime());
                } catch (JSONException e) {
                    Log.d("RedButtonOnClick", "JSONFailed");
                }
                Log.d("JSON", object.toString());

                new AsyncSetStatusRequest().execute(object.toString());

                buttonView.setText("Опасно");
                buttonView.setTextColor(Color.parseColor("#FF9800"));
            } else {
                //Location currentLocation = ((IntroMainActivity) getActivity()).getCurrentLocation();
                JSONObject object = new JSONObject();
                String userId = SharedPreferencesHelper.generateUserId(getActivity());
                SharedPreferencesHelper.setDangerMode(getActivity(),false);

                //Status switch handler
                try {
                    object
                            .put("accountToken", userId)
                            .put("location", new JSONObject()
                                    .put("lat", null)
                                    .put("lng", null)
                                    .put("timestamp", new Date().getTime()))
                            .put("newStatus", "inactive")
                            .put("currentTimestamp", new Date().getTime());
                } catch (JSONException e) {
                    Log.d("RedButtonOnClick", "JSONFailed");
                }
                Log.d("JSON", object.toString());

                new AsyncSetStatusRequest().execute(object.toString());

                buttonView.setText("Безопасно");
                buttonView.setTextColor(Color.parseColor("#4CAF50"));
            }
        });

        view.findViewById(R.id.imageView3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Location currentLocation = ((IntroMainActivity) getActivity()).getCurrentLocation();
                JSONObject object = new JSONObject();
                String userId = SharedPreferencesHelper.generateUserId(getActivity());

                try {
                    object
                            .put("accountToken", userId)
                            .put("location", new JSONObject()
                                    .put("lat", null)
                                    .put("lng", null)
                                    .put("timestamp", new Date().getTime()))
                            .put("originalTimestamp", new Date().getTime())
                            .put("currentTimestamp", new Date().getTime())
                            .put("reason", "тестирование");
                } catch (JSONException e) {
                    Log.d("RedButtonOnClick", "JSONFailed");
                }
                Log.d("JSON", object.toString());

                new AsyncCancelAlarmRequest().execute(object.toString());

                View mainView = getView();
                mainView.findViewById(R.id.imageView3).setVisibility(View.GONE);
                mainView.findViewById(R.id.textView15).setVisibility(View.GONE);
                mainView.findViewById(R.id.imageView2).setVisibility(View.VISIBLE);
            }
        });
        view.findViewById(R.id.imageView2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((IntroMainActivity) getActivity()).sendAlarmFromRedButton();

                View mainView = getView();
                mainView.findViewById(R.id.imageView3).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.textView15).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.imageView2).setVisibility(View.GONE);

                final Handler handler = new Handler();
                handler.postDelayed(new ChangeVisibilityAfterRunnable(mainView.findViewById(R.id.imageView3), View.GONE), 5000);
                handler.postDelayed(new ChangeVisibilityAfterRunnable(mainView.findViewById(R.id.textView15), View.GONE), 5000);
                handler.postDelayed(new ChangeVisibilityAfterRunnable(mainView.findViewById(R.id.imageView2), View.VISIBLE), 5000);
            }
        });

        view.findViewById(R.id.imageView8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(RedButtonFragment.this)
                        .navigate(R.id.action_redButtonFragment_to_contactsFragment);
            }
        });
    }





    class AsyncSetStatusRequest extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... arg) {
            Log.d("RedButtonAsyncRequest","BackGroundStarted");
            return FindMeHttpUtil.sendRequest("POST", "/api/v1/setStatus", arg[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("RedButtonAsyncRequest","onPostExecute");
            super.onPostExecute(s);
        }
    }

    class AsyncCancelAlarmRequest extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... arg) {
            Log.d("RedButtonAsyncRequest","BackGroundStarted");
            return FindMeHttpUtil.sendRequest("POST", "/api/v1/cancelAlarm", arg[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("RedButtonAsyncRequest","onPostExecute");
            super.onPostExecute(s);
        }
    }

    class ChangeVisibilityAfterRunnable implements Runnable {
        private View view;
        private int visible;

        public ChangeVisibilityAfterRunnable(View view, int visible)
        {
            this.view = view;
            this.visible = visible;
        }

        @Override
        public void run() {
            view.setVisibility(visible);
        }
    }
}