package com.duy.aahomeinternet;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Duy on 15-Oct-16.
 */

public class EnvironmentTask {
    private Context mContext;
    private EnvironmentListener mListener;
    private FirebaseHandler mFirebase;

    public EnvironmentTask(Context mContext, FirebaseHandler firebase) {
        this.mContext = mContext;
        this.mFirebase = firebase;
    }

    public EnvironmentTask(Context context, FirebaseHandler firebase, EnvironmentListener listener) {
        this.mContext = context;
        this.mFirebase = firebase;
        this.mListener = listener;
    }

    public void doExecute() {
        initTemp();
        initHumi();
        initLight();
        initGas();
    }

    private void initGas() {

    }

    private void initLight() {

    }

    private void initHumi() {
        String url2 = "users/" + mFirebase.getUid() + "/" + "temp/" + "current";
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(url2);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    int h = dataSnapshot.getValue(Integer.class);
                    if (mListener != null) mListener.onHumidityChange(h);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void initTemp() {
        String url2 = "users/" + mFirebase.getUid() + "/" + "temp/" + "current";
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(url2);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    int temp = dataSnapshot.getValue(Integer.class);
                    if (mListener != null) mListener.onTemperatureChange(temp);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public EnvironmentListener getListener() {
        return mListener;
    }

    public void setListener(EnvironmentListener mListener) {
        this.mListener = mListener;
    }

    /**
     * Created by Duy on 15-Oct-16.
     */
    public interface EnvironmentListener {

        void onTemperatureChange(int temp);

        void onHumidityChange(int humi);

        void onLightChange(int valueLight);

        void onGasChange(int valueGas);
    }
}
