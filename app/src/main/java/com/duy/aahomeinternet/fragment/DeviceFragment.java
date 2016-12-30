package com.duy.aahomeinternet.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.duy.aahomeinternet.DeviceActivity;
import com.duy.aahomeinternet.FirebaseListener;
import com.duy.aahomeinternet.MainActivity;
import com.duy.aahomeinternet.R;
import com.duy.aahomeinternet.adapter.DeviceAdapter;
import com.duy.aahomeinternet.adapter.EventListener;
import com.duy.aahomeinternet.data.Database;
import com.duy.aahomeinternet.items.DeviceItem;
import com.duy.aahomeinternet.utils.Variable;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * class for control devices.
 */
public class DeviceFragment extends Fragment implements EventListener {
    private static final String TAG = DeviceFragment.class.getName();
    private ArrayList<DeviceItem> list = new ArrayList<>();
    private Handler handler = new Handler();
    private View mContainer;
    private Context context;
    private MainActivity activity;

    /**
     * adapter for recycle view
     */
    private DeviceAdapter deviceAdapter;
    private SwipeRefreshLayout mReresh;
    private Database database;
    private FirebaseListener mFirebase;
    private boolean isRunning = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.activity = (MainActivity) context;
        this.database = new Database(context);
        this.mFirebase = new FirebaseListener(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContainer = inflater.inflate(R.layout.layout_device, container, false);
        return mContainer;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView rcAllDevice = (RecyclerView) mContainer.findViewById(R.id.rc_device);
        rcAllDevice.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rcAllDevice.setLayoutManager(linearLayoutManager);
        deviceAdapter = new DeviceAdapter(context, this, mFirebase, list);
        rcAllDevice.setAdapter(deviceAdapter);
        mReresh = (SwipeRefreshLayout) mContainer.findViewById(R.id.sr_device);
        mReresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initDatabase();
            }
        });
        initDatabase();
    }

    private void initDatabase() {

        Log.d(TAG, "initDatabase: started");
        isRunning = true;
        mReresh.post(new Runnable() {
            @Override
            public void run() {
                mReresh.setRefreshing(true);
            }
        });
        //create url
        String url = "users/" + mFirebase.getUid() + "/" + "devices/";
        //get database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(url);
        //add listener
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Log.d(TAG, "onDataChange: datachange");
                    list.clear();
                    ArrayList deviceItems = (ArrayList) dataSnapshot.getValue();
                    for (int i = 0; i < deviceItems.size(); i++) {
                        try {
                            HashMap map = (HashMap) deviceItems.get(i);
                            DeviceItem deviceItem = new DeviceItem();
                            deviceItem.setId(Integer.parseInt(String.valueOf(map.get("id"))));
                            deviceItem.setName(String.valueOf(map.get("name")));
                            Log.d(TAG, deviceItem.getId() + " " + deviceItem.getName());
                            list.add(deviceItem);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    database.removeAllDevice();
                    database.addDevice(list);

                    if (deviceAdapter != null) {
                        deviceAdapter.notifyDataSetChanged();
                        mReresh.post(new Runnable() {
                            @Override
                            public void run() {
                                mReresh.setRefreshing(false);
                            }
                        });
                    }
                    Log.d(TAG, "onDataChange: ok");
                } else {
                    Toast.makeText(context, "Data nullable, please add some devices ", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onDataChange: data nullable");
                }
                isRunning = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: ok");
                isRunning = false;
            }
        });
    }

    @Override
    public void deviceDetail(int pos, DeviceItem deviceItem) {
        String info = "Chân arduino: " + deviceItem.getId() + "\n" +
                "Tên: " + deviceItem.getName() + "\n" +
                "Thông tin: " + deviceItem.getDes() + "\n";
        showDialog(info);
    }

    @Override
    public void deviceChangeInfo(int pos, DeviceItem deviceItem) {
        Intent intent = new Intent(context, DeviceActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putSerializable("item", deviceItem);
        intent.putExtra("com/example/tranleduy/aahome/com.duy.aahomeinternet.data", bundle);
        startActivityForResult(intent, Variable.ACTIVITY_DEVICE);
    }

    @Override
    public void deviceDelete(final int pos, final DeviceItem deviceItem) {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(context);
        aBuilder.setMessage(R.string.do_you_want_delete_device);
        aBuilder.setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        aBuilder.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                long res = database.removeDevice(deviceItem);
                if (res > 0) {
                    deviceAdapter.removeDevice(deviceItem, pos);
//                    String url = "users/" + mFirebase.getUid() + "/" + "pin/" + deviceItem.getId();
//                    DatabaseReference db = FirebaseDatabase.getInstance().getReference(url);
//                    db.removeValue();
                } else {
                    showDialog(getString(R.string.delete_failed));
                }
            }
        });
        aBuilder.create().show();

    }

    /**
     * show dialog on fragment
     *
     * @param s
     */
    protected void showDialog(String s) {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(context);
        aBuilder.setMessage(s);
        aBuilder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        aBuilder.create().show();
    }

}

