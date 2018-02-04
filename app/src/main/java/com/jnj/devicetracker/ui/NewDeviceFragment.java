package com.jnj.devicetracker.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devicetracker.R;
import com.jnj.devicetracker.db.DeviceDataPersistence;
import com.jnj.devicetracker.model.Device;
import com.jnj.devicetracker.sync.SyncService;
import com.jnj.devicetracker.sync.event.SyncEvent;
import com.jnj.devicetracker.sync.event.SyncStatus;
import com.jnj.devicetracker.sync.event.SyncType;

import java.util.Date;

import io.realm.Realm;

public class NewDeviceFragment extends Fragment {

    private EditText deviceName;
    private EditText os;

    private EditText manufacturer;

    private Realm realm;
    private Device device;
    private Button button;
    private TextView textView;
    private String input;
    private ProgressBar spinning;
    String AVAILABLE = "Available";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm=Realm.getDefaultInstance();
        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle("Add Device");
        }
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.new_device_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            onCreateClicked();

        }

//        if (item.getItemId() == R.id.home) {
//
//        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_device, container, false);
        Activity activity = this.getActivity();
        ActionBar toolbar =  ((AppCompatActivity)activity).getSupportActionBar();
        toolbar.setTitle("Add Device");
        toolbar.setDisplayHomeAsUpEnabled(true);
        activity.findViewById(R.id.fab).setVisibility(View.GONE);

        deviceName = (EditText) rootView.findViewById(R.id.new_device);
        os= (EditText) rootView.findViewById(R.id.new_device_os);

        manufacturer= (EditText) rootView.findViewById(R.id.new_device_manufacturer);

        return rootView;
    }



    private void onCreateClicked() {
        String deviceName = this.deviceName.getText().toString();
        String os = this.os.getText().toString();

        String manufacturer = this.manufacturer.getText().toString();

        if (TextUtils.isEmpty(deviceName) || TextUtils.isEmpty(os) || TextUtils.isEmpty(manufacturer)) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
        } else {
            Device device = new Device();
            device.setUid(DeviceDataPersistence.AUTO_INCREMENT(realm));
            device.setDevice(deviceName);
            device.setOS(os);
            device.setManufacturer(manufacturer);
            device.setLastCheckedOutDate(new Date());
            device.setCheckedOut(false);
            device.setToBeUpdated(false); // upload to server indicator
            DeviceDataPersistence.save(device);
            SyncEvent.send(SyncType.DEVICE, SyncStatus.COMPLETED); // tell all related screens to reload data from realm
            SyncService.request(SyncType.DEVICE);
            getActivity().getSupportFragmentManager().popBackStack();


        }
    }


}
