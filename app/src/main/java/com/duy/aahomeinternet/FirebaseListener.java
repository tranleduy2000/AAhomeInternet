package com.duy.aahomeinternet;

import android.content.Context;
import android.util.Log;

import com.duy.aahomeinternet.utils.Protocol;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Duy on 19/7/2016
 */
public class FirebaseListener {
    private static final String PIN = "pin";
    private static final String USERS = "users";
    private static final String MODE = "mode";
    private static final String URL_FIREBASE = "https://smarthome-f6176.firebaseio.com";
    private static final String TAG = FirebaseListener.class.getName();
    private Context mContext;
    private FirebaseUser mUser;
    private Firebase mFirebase;

    public FirebaseListener(Context context) {
        this.mContext = context;
        Firebase.setAndroidContext(context);
        mFirebase = new Firebase(URL_FIREBASE);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        // Log.d(TAG, mUser.getUid());
    }

    public void setPin(int pin, boolean value) {
        mFirebase.child(USERS).child(mUser.getUid()).
                child(PIN).child(String.valueOf(pin)).setValue(value);
    }

    public void setMode(String mode, boolean value) {
        mFirebase.child(USERS).child(mUser.getUid())
                .child(MODE).child(String.valueOf(mode)).setValue(value);
    }

    public String getUid() {
        return mUser.getUid();
    }

    /**
     * return status off pin on arduino board, 1 if high, ortherwise 0
     *
     * @param pin - pin
     * @return - boolean
     */
    public boolean getStatusPin(int pin) {
        return true;
    }


    /**
     * @param id     - 0 if analog, 1 as digital
     * @param status
     */
    public void setAutoLight(int id, boolean status) {
        Log.d(TAG, "setAutoLight: " + id + " " + status);
        String url2 = "users/" + getUid() + "/" + "mode/" + Protocol.AUTO_LIGHT_DIGITAL;
        final DatabaseReference digital = FirebaseDatabase.getInstance().getReference(url2);
        String url = "users/" + getUid() + "/" + "mode/" + Protocol.AUTO_LIGHT_ANALOG;
        final DatabaseReference analog = FirebaseDatabase.getInstance().getReference(url);

        if (id == Protocol.ANALOG) {
            if (status) {
                analog.setValue(true);
                digital.setValue(false);
            } else {
                analog.setValue(false);
            }
        } else { //digital
            if (status) {
                digital.setValue(true);
                analog.setValue(false);
            } else {
                digital.setValue(false);
            }
        }
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }
}
