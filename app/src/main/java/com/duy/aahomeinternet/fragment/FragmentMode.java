package com.duy.aahomeinternet.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.duy.aahomeinternet.FirebaseHandler;
import com.duy.aahomeinternet.R;
import com.duy.aahomeinternet.data.Preferences;
import com.duy.aahomeinternet.utils.Protocol;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Duy on 19/7/2016
 */
public class FragmentMode extends Fragment {
    View v;
    SwipeRefreshLayout refreshLayout;
    private CheckBox ckbPir;
    private SeekBar seekLight;
    private CardView cardOnOffLight;
    private ImageView imgSecutiry;
    private Preferences preferences;
    private TextView txtValueAnalog;
    private FirebaseHandler mFirebase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFirebase = new FirebaseHandler(getActivity().getApplicationContext());
        preferences = new Preferences(getActivity().getApplicationContext());
        v = inflater.inflate(R.layout.layout_mode, container, false);
        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.srMode);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload();
            }
        });
        reload();
        return v;
    }

    void reload() {
        setupAutoLight(v);
        setupFireAlarm(v);
        setupSecurity(v);
        setUpAutoRoof(v);
        setUpAutoDoor(v);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void setUpAutoRoof(View v) {
        final SwitchCompat swRoof = (SwitchCompat) v.findViewById(R.id.ckbAutoRoof);
        swRoof.setChecked(preferences.getBoolean(Preferences.IS_ROOF_ON));
        String url = "users/" + mFirebase.getUid() + "/" + "mode/" + Protocol.AUTO_ROOF;
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference(url);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Boolean.class) != null) {
                    boolean b = dataSnapshot.getValue(Boolean.class);
                    preferences.putBoolean(Preferences.IS_ROOF_ON, b);
                    swRoof.setChecked(b);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        swRoof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (swRoof.isChecked()) {
                    db.setValue(true);
                } else {
                    db.setValue(false);
                }
            }
        });
    }

    private void setUpAutoDoor(View v) {
        final SwitchCompat swDoor = (SwitchCompat) v.findViewById(R.id.ckbAutoDoor);
        swDoor.setChecked(preferences.getBoolean(Preferences.IS_AUTO_DOOR));
        String url = "users/" + mFirebase.getUid() + "/" + "mode/" + Protocol.AUTO_DOOR;
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference(url);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Boolean.class) != null) {
                    boolean b = dataSnapshot.getValue(Boolean.class);
                    preferences.putBoolean(Preferences.IS_AUTO_DOOR, b);
                    swDoor.setChecked(b);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        swDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (swDoor.isChecked()) {
                    db.setValue(true);
                } else {
                    db.setValue(false);
                }
            }
        });
    }

    private void setupFireAlarm(View v) {
        final SwitchCompat swFire = (SwitchCompat) v.findViewById(R.id.ckbFire);
        swFire.setChecked(preferences.getBoolean(Preferences.IS_FIRE_ON));

        String url = "users/" + mFirebase.getUid() + "/" + "mode/" + Protocol.FIRE_ALRAM_SYSTEM;
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference(url);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Boolean.class) != null) {
                    boolean b = dataSnapshot.getValue(Boolean.class);
                    preferences.putBoolean(Preferences.IS_FIRE_ON, b);
                    swFire.setChecked(b);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        swFire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.setValue(swFire.isChecked());
            }
        });
    }

    private void setupAutoLight(View v) {
        final SwitchCompat swLightDigital = (SwitchCompat) v.findViewById(R.id.auto_light_digital);
        String url2 = "users/" + mFirebase.getUid() + "/" + "mode/" + Protocol.AUTO_LIGHT_DIGITAL;
        final DatabaseReference db2 = FirebaseDatabase.getInstance().getReference(url2);
        db2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Boolean.class) != null) {
                    boolean b = dataSnapshot.getValue(Boolean.class);
                    preferences.putBoolean(Preferences.IS_AUTO_LIGHT_DIGITAL, b);
                    swLightDigital.setChecked(b);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final SwitchCompat swLightAnalog = (SwitchCompat) v.findViewById(R.id.auto_light_analog);
        swLightAnalog.setChecked(preferences.getBoolean(Preferences.IS_AUTO_LIGHT_ANALOG));
        String url = "users/" + mFirebase.getUid() + "/" + "mode/" + Protocol.AUTO_LIGHT_ANALOG;
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference(url);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Boolean.class) != null) {
                    boolean b = dataSnapshot.getValue(Boolean.class);
                    preferences.putBoolean(Preferences.IS_AUTO_LIGHT_ANALOG, b);
                    swLightAnalog.setChecked(b);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        swLightAnalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (swLightAnalog.isChecked()) {
                    if (swLightDigital.isChecked()) {
                        db2.setValue(false);
                    }
                    db.setValue(true);
                } else {
                    db.setValue(false);
                }
            }
        });


        swLightDigital.setChecked(preferences.getBoolean(Preferences.IS_AUTO_LIGHT_DIGITAL));
        swLightDigital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (swLightDigital.isChecked()) {
                    if (swLightAnalog.isChecked()) {
                        db.setValue(false);
                    }
                    db2.setValue(true);
                } else {
                    db2.setValue(false);
                }
            }
        });
    }

    private void setupSecurity(View v) {
        imgSecutiry = (ImageView) v.findViewById(R.id.imgSecurity);
        imgSecutiry.setImageResource((preferences.getBoolean(Preferences.IS_SECURITY_ON) ?
                R.drawable.ic_security_blue : R.drawable.ic_security_red));
        final SwitchCompat swSecurity = (SwitchCompat) v.findViewById(R.id.ckbSecurity);
        swSecurity.setChecked(preferences.getBoolean(Preferences.IS_SECURITY_ON));
        swSecurity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    imgSecutiry.setImageResource(R.drawable.ic_security_blue);
                } else {
                    imgSecutiry.setImageResource(R.drawable.ic_security_red);
                }
            }
        });
        swSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = swSecurity.isChecked();
                String url = "users/" + mFirebase.getUid() + "/" + "mode/" + Protocol.SECURITY;
                DatabaseReference db = FirebaseDatabase.getInstance().getReference(url);
                db.setValue(isChecked);
                preferences.putBoolean(Preferences.IS_SECURITY_ON, isChecked);

            }
        });
        String url = "users/" + mFirebase.getUid() + "/" + "mode/" + Protocol.SECURITY;
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(url);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    boolean b = dataSnapshot.getValue(Boolean.class);
                    swSecurity.setChecked(b);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
