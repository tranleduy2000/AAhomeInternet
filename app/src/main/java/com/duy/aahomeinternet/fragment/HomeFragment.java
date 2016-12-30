package com.duy.aahomeinternet.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.duy.aahomeinternet.EnvironmentTask;
import com.duy.aahomeinternet.FirebaseListener;
import com.duy.aahomeinternet.MapsActivity;
import com.duy.aahomeinternet.R;
import com.duy.aahomeinternet.RoofActivity;
import com.github.lzyzsd.circleprogress.DonutProgress;

import java.util.concurrent.atomic.AtomicBoolean;

public class HomeFragment extends Fragment implements EnvironmentTask.EnvironmentListener {
    private Context mContext;
    private View mContainer;
    private TextView txtTempC, txtTempF;
    private TextView txtLocation;
    private DonutProgress proHumidity;
    private Activity mActivity;
    private ProgressBar progressBar1, progressBar2;
    private EnvironmentTask environmentTask;
    private Handler handler = new Handler();
    private FirebaseListener mFirebase;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        this.mActivity = getActivity();
        this.mFirebase = new FirebaseListener(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContainer = inflater.inflate(R.layout.fragment_main, container, false);
        return mContainer;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CardView mMap = (CardView) mContainer.findViewById(R.id.card_maps);
        mMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, MapsActivity.class));
            }
        });
        txtLocation = (TextView) mContainer.findViewById(R.id.txt_local);
        txtTempC = (TextView) mContainer.findViewById(R.id.txtTempC);
        txtTempF = (TextView) mContainer.findViewById(R.id.txtTempF);
        proHumidity = (DonutProgress) mContainer.findViewById(R.id.cpHumidity);
        LinearLayout mRoof = (LinearLayout) mContainer.findViewById(R.id.ll_roof);
        mRoof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, RoofActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout mDoor = (LinearLayout) mContainer.findViewById(R.id.llDoor_main);
        mDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, R.string.not_support_open_door_via_wifi, Toast.LENGTH_SHORT).show();
            }
        });

        progressBar1 = (ProgressBar) mContainer.findViewById(R.id.progressBar4);
        progressBar2 = (ProgressBar) mContainer.findViewById(R.id.progressBar5);

        /**
         * init eviroment listener for home
         */
        environmentTask = new EnvironmentTask(mContext, mFirebase);
        environmentTask.setListener(HomeFragment.this);
        environmentTask.doExecute();
    }

    @Override
    public void onTemperatureChange(int temp) {
        progressBar1.setVisibility(View.GONE);
        txtTempC.setVisibility(View.VISIBLE);
        txtTempC.setText(String.valueOf(temp) + " C");

        progressBar2.setVisibility(View.GONE);
        txtTempF.setVisibility(View.VISIBLE);
        int f = convertToF(temp);
        txtTempF.setText(String.valueOf(f) + " F");
    }

    public int convertToF(int C) {
        return (int) (C * 1.8 + 32);
    }

    @Override
    public void onHumidityChange(final int humi) {
        final AtomicBoolean ab = new AtomicBoolean(false);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= humi + 10 && ab.get(); i++) {
                    final int finalI = i;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            proHumidity.setProgress(finalI);
                        }
                    });
                    SystemClock.sleep(20);
                }

                for (int i = humi + 10; i >= humi && ab.get(); i--) {
                    final int finalI = i;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            proHumidity.setProgress(finalI);
                        }
                    });
                    SystemClock.sleep(20);
                }
            }
        });
        ab.set(true);
        thread.start();
    }

    @Override
    public void onLightChange(int valueLight) {

    }

    @Override
    public void onGasChange(int valueGas) {

    }
}
