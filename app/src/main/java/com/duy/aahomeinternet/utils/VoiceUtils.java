package com.duy.aahomeinternet.utils;

import android.util.Log;

import com.duy.aahomeinternet.FirebaseHandler;
import com.duy.aahomeinternet.data.Database;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Duy on 15-Oct-16.
 */

public class VoiceUtils {
    public static final String TAG = VoiceUtils.class.getSimpleName();

    public static final int OFF_ALL_LIGHT = 1;
    public static final int ON_ALL_LIGHT = 2;
    public static final int OFF_LIGHT = 3;
    public static final int ON_LIGHT = 4;
    public static final int ON_AUTO_LIGHT = 5;
    public static final int OFF_AUTO_LIGHT = 6;
    public static final int ON_FAN = 7;
    public static final int OFF_FAN = 8;
    public static final int NOT_FOUND = -1;
    private ArrayList<Translate> arrTranslate = new ArrayList<>();
    private Database database;
    private FirebaseHandler firebaseHandler;

    public VoiceUtils(FirebaseHandler firebaseHandler) {
        this.firebaseHandler = firebaseHandler;
        this.database = new Database(firebaseHandler.getContext());
        generalData();
    }

    private void generalData() {
        arrTranslate.add(new Translate("mở hết đèn", ON_ALL_LIGHT));
        arrTranslate.add(new Translate("bật hết đèn", ON_ALL_LIGHT));

        arrTranslate.add(new Translate("tắt hết đèn", OFF_ALL_LIGHT));
        arrTranslate.add(new Translate("đóng hết đèn", OFF_ALL_LIGHT));

        arrTranslate.add(new Translate("tắt đèn", OFF_LIGHT));
        arrTranslate.add(new Translate("đóng đèn", OFF_LIGHT));

        arrTranslate.add(new Translate("mở đèn", ON_LIGHT));
        arrTranslate.add(new Translate("bật đèn", ON_LIGHT));

        arrTranslate.add(new Translate("tắt quạt", OFF_FAN));
        arrTranslate.add(new Translate("đóng quạt", OFF_FAN));

        arrTranslate.add(new Translate("mở quạt", ON_FAN));
        arrTranslate.add(new Translate("bật quạt", ON_FAN));
    }

    public int getCommand(String cmd) {
        cmd = cmd.toLowerCase(); //convert to lower case
        Log.d(TAG, "getCommand: cmd " + cmd);
        for (Translate s : arrTranslate) {
            Log.d(TAG, "getCommand: " + s.getLocal() + " " + (cmd.contains(s.getLocal())));
            if (cmd.contains(s.getLocal())) {
                int id = s.getCommand();
                switch (id) {
                    case ON_LIGHT:
                        onLight(cmd);
                        break;
                    case OFF_LIGHT:
                        offLight(cmd);
                        break;
                    case OFF_ALL_LIGHT:
                        process(OFF_ALL_LIGHT, NOT_FOUND, database.getListPinLight());
                        break;
                    case ON_ALL_LIGHT:
                        process(ON_ALL_LIGHT, NOT_FOUND, database.getListPinLight());
                        break;
                }
                return s.getCommand();
            }
        }
        return NOT_FOUND;
    }

    private void offLight(String cmd) {
        String pin = "";
        for (int i = 0; i < cmd.length(); i++) {
            if ("0123456789".indexOf(cmd.charAt(i)) != -1) {
                for (char a : cmd.toCharArray()) {
                    if ("0123456789".indexOf(a) != -1)
                        pin += a;
                }
                break;
            }
        }
        try {
            Log.d(TAG, "offLight pin = " + pin);
            if (!pin.isEmpty()) {
                int p2 = Integer.parseInt(pin);
                process(OFF_LIGHT, p2, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onLight(String input) {
        String pin = "";
        for (int i = 0; i < input.length(); i++) {
            if ("0123456789".indexOf(input.charAt(i)) != -1) {
                for (char a : input.toCharArray()) {
                    if ("0123456789".indexOf(a) != -1)
                        pin += a;
                }
                break;
            }
        }
        try {
            Log.d(TAG, "onLight: pin = " + pin);
            if (!pin.isEmpty()) {
                int p2 = Integer.parseInt(pin);
                process(ON_LIGHT, p2, null);
            } else {
                process(ON_ALL_LIGHT, NOT_FOUND, database.getListPinLight());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean process(int cmd, int pin, ArrayList<String> arrayList) {
        String url;
        DatabaseReference db;
        Log.d(TAG, "process: id = " + cmd);
        switch (cmd) {
            case ON_ALL_LIGHT: //on light
                firebaseHandler.setAutoLight(Protocol.ANALOG, false);
                firebaseHandler.setAutoLight(Protocol.DIGITAL, false);
                Log.d(TAG, "process: arr " + arrayList.toString());
                for (String p : arrayList) {
                    url = "users/" + firebaseHandler.getUid() + "/" + "pin/" + p;
                    db = FirebaseDatabase.getInstance().getReference(url);
                    db.setValue(true);
                }
                break;
            case OFF_ALL_LIGHT: //off all light
                firebaseHandler.setAutoLight(Protocol.ANALOG, false);
                firebaseHandler.setAutoLight(Protocol.DIGITAL, false);
                Log.d(TAG, "process: arr " + arrayList.toString());
                for (String p : arrayList) {
                    url = "users/" + firebaseHandler.getUid() + "/" + "pin/" + p;
                    db = FirebaseDatabase.getInstance().getReference(url);
                    db.setValue(false);
                }
                break;
            case ON_LIGHT: //on light with pin
                url = "users/" + firebaseHandler.getUid() + "/" + "pin/" + pin;
                db = FirebaseDatabase.getInstance().getReference(url);
                db.setValue(true);
                break;
            case OFF_LIGHT: //off light with pin
                url = "users/" + firebaseHandler.getUid() + "/" + "pin/" + pin;
                db = FirebaseDatabase.getInstance().getReference(url);
                db.setValue(false);
                break;
            case ON_AUTO_LIGHT: //on auto light
                firebaseHandler.setAutoLight(Protocol.DIGITAL, true);
                break;
            case OFF_AUTO_LIGHT://off auto light
                firebaseHandler.setAutoLight(Protocol.DIGITAL, true);
                break;
            case ON_FAN:
                break;
            case OFF_FAN:
                break;
            default:
                break;
        }
        return true;
    }

    public class Translate {
        String local = "";
        int command = NOT_FOUND;

        public Translate(String local, int command) {
            this.local = local;
            this.command = command;
        }

        public String getLocal() {
            return local;
        }

        public void setLocal(String local) {
            this.local = local;
        }

        public int getCommand() {
            return command;
        }

        public void setCommand(int command) {
            this.command = command;
        }
    }
}
