package com.duy.aahomeinternet.adapter;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.duy.aahomeinternet.FirebaseHandler;
import com.duy.aahomeinternet.R;
import com.duy.aahomeinternet.items.DeviceItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
    private final String TAG = DeviceAdapter.class.getSimpleName();
    private ArrayList<DeviceItem> deviceItems = new ArrayList<>();
    private Context context;
    private EventListener eventListener = null;
    private FirebaseHandler mFirebase;
    private Handler handler = new Handler();

    public DeviceAdapter(Context context, EventListener eventListener,
                         FirebaseHandler firebaseHandler, ArrayList<DeviceItem> items) {
        this.context = context;
        this.eventListener = eventListener;
        this.mFirebase = firebaseHandler;
        this.deviceItems = items;
    }

    public DeviceItem getDeviceItem(int position) {
        return deviceItems.get(position);
    }

    public void addDevice(DeviceItem deviceItem) {
        deviceItems.add(deviceItem);
        notifyItemInserted(deviceItems.size() - 1);
        String url = "users/" + mFirebase.getUid() + "/" + "devices/";
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(url);
    }

    public void removeDevice(DeviceItem deviceItem, int position) {
        deviceItems.remove(position);
        notifyItemRemoved(position);
        String url = "users/" + mFirebase.getUid() + "/" + "devices/";
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference(url);
        db.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                db.setValue(deviceItems);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
        viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final DeviceItem deviceItem = deviceItems.get(position);
        holder.txtId.setText(deviceItem.getId() + "");
        holder.txtName.setText(deviceItem.getName());

        final String url = "users/" + mFirebase.getUid() + "/" + "pin/" + deviceItem.getId();
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Boolean.class) != null) {
                    final boolean b = dataSnapshot.getValue(Boolean.class);
                    Log.d(TAG, "onDataChange: device in " + position + " change status " + String.valueOf(b));
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.aSwitch.setChecked(b);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference(url);
        db.addValueEventListener(listener);

        holder.aSwitch.setOnClickListener(null);
        holder.aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "users/" + mFirebase.getUid() + "/" + "pin/" + deviceItem.getId();
                DatabaseReference db = FirebaseDatabase.getInstance().getReference(url);
                db.setValue(holder.aSwitch.isChecked());
                Log.d(TAG, "onClick: device on click" + position);
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder aBuilder = new AlertDialog.Builder(context);
                aBuilder.setMessage(R.string.delete_devices);
                aBuilder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                aBuilder.setNegativeButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeDevice(deviceItem, position);
                    }
                });
                aBuilder.create().show();
                return false;
            }
        });

        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //  if (eventListener != null) eventListener.deviceDetail(position, deviceItem);
                        break;
                    case 1:
                        if (eventListener != null)
                            eventListener.deviceChangeInfo(position, deviceItem);
                        break;
                    case 2:
                        if (eventListener != null) eventListener.deviceDelete(position, deviceItem);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return deviceItems.size();
    }

    public void updateDevice(DeviceItem deviceItem) {
        for (int index = 0; index < getItemCount(); index++) {
            if (deviceItem.getId() == deviceItems.get(index).getId()) {
                deviceItems.set(index, deviceItem);
                notifyItemChanged(index);
                break;
            }
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtDes, txtId;
        SwitchCompat aSwitch;
        CardView cardView;
        ImageView imageView;
        Spinner spinner;

        public ViewHolder(View itemView) {
            super(itemView);
            txtId = (TextView) itemView.findViewById(R.id.txt_id);
            txtName = (TextView) itemView.findViewById(R.id.txt_name_item);
            cardView = (CardView) itemView.findViewById(R.id.cv_item);
            aSwitch = (SwitchCompat) itemView.findViewById(R.id.sw_item);
            imageView = (ImageView) itemView.findViewById(R.id.imageView2);
            spinner = (Spinner) itemView.findViewById(R.id.spinnerDetail);
            this.setIsRecyclable(false);
        }
    }
}
