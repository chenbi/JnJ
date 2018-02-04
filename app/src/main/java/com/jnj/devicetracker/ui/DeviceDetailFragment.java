package com.jnj.devicetracker.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devicetracker.R;
import com.jnj.devicetracker.db.DeviceDataPersistence;
import com.jnj.devicetracker.model.Device;
import com.jnj.devicetracker.sync.SyncService;
import com.jnj.devicetracker.sync.event.EventBusManager;
import com.jnj.devicetracker.sync.event.SyncEvent;
import com.jnj.devicetracker.sync.event.SyncStatus;
import com.jnj.devicetracker.sync.event.SyncType;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.realm.Realm;

import static com.jnj.devicetracker.model.Device.LASTCHECKEDOUTBY;
import static com.jnj.devicetracker.model.Device.LASTCHECKEDOUTDATE;
import static com.jnj.devicetracker.model.Device.MANUFACTURER;


/**
 * A fragment representing a single Device detail screen.
 * This fragment is either contained in a {@link MainActivity} on handsets.
 * or in two-pane mode (on tablets) or a {@link DeviceDetailActivity}
 */
public class DeviceDetailFragment extends Fragment {
    /**
     * The fragment argument representing the DEVICE_ID that this fragment
     * represents.
     */
    private Realm realm;
    private Device device;
    private Button button;
    private TextView textView;
    private String input;
    private ProgressBar spinning;
    String AVAILABLE = "Available";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DeviceDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBusManager.register(this);
        realm=Realm.getDefaultInstance();
        if (getArguments().containsKey(Device.DEVICE_ID)) {

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle("Device Detail");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_device_detail, container, false);
        button =  ((Button) rootView.findViewById(R.id.device_detail_button));
        textView = ((TextView) rootView.findViewById(R.id.device_detail));
        device = DeviceDataPersistence.getByID(realm,getArguments().getInt(Device.DEVICE_ID));
        spinning = (ProgressBar) rootView.findViewById(R.id.progressBar);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (device.isCheckedOut()) {
                    DeviceDataPersistence.toggleChecked(device.getId(), !device.isCheckedOut());
                    device.setToBeUpdated(true);
                    SyncEvent.send(SyncType.DEVICE, SyncStatus.COMPLETED); // tell all related screens to reload data from realm
                    SyncService.request(SyncType.DEVICE);


                }
                else
                    showInputDialog();
            }
        });


        refreshUI();


        return rootView;
    }

    private  void refreshUI(){

        SpannableStringBuilder spannable = new SpannableStringBuilder();
        spannable.append(device.getDevice() + " - " + device.getOS());
        spannable.append("\n");
        spannable.append(MANUFACTURER+": "+device.getManufacturer());
        spannable.append("\n");
        if (device.getLastCheckedOutBy()!=null) {
            spannable.append(LASTCHECKEDOUTBY + ": " + device.getLastCheckedOutBy());
            spannable.append("\n");
            spannable.append(LASTCHECKEDOUTDATE + ": " + device.getLastCheckedOutDate().toString());
            spannable.append("\n");
        }
        if (device.isCheckedOut()) {
            spannable.append("Status: Checeked Out");
            button.setText(R.string.check_in);
        }
        else {
            spannable.append("Status: Available");
            button.setText(R.string.check_out);
        }


        String originalString = spannable.toString();
        int start = originalString.indexOf(AVAILABLE);
        int end = originalString.indexOf(AVAILABLE) + AVAILABLE.length();

        if (start >= 0 && end > 0) {
            spannable.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        textView.setText(spannable);

    }

    protected void showInputDialog() {

        // get prompts.xml view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View promptView = inflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        input = editText.getText().toString();
                        DeviceDataPersistence.toggleChecked(device.getId(), !device.isCheckedOut());
                        DeviceDataPersistence.setLastCheckedOutBy(device.getId(),input);
                        DeviceDataPersistence.setLastCheckedOutDate(device.getId());

                        SyncEvent.send(SyncType.DEVICE, SyncStatus.COMPLETED); // tell all related screens to reload data from realm
                        SyncService.request(SyncType.DEVICE);



                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SyncEvent event) {
        if (event.getType() == SyncType.DEVICE && event.getStatus() == SyncStatus.IN_PROGRESS) {
            spinning.setVisibility(View.VISIBLE);
        } else if (event.getType() == SyncType.DEVICE && event.getStatus() == SyncStatus.COMPLETED) {

            refreshUI();
            spinning.setVisibility(View.INVISIBLE);

        }
    }



}
