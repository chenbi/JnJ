package com.jnj.devicetracker.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devicetracker.R;
import com.jnj.devicetracker.model.Device;
import com.jnj.devicetracker.ui.DeviceDetailActivity;
import com.jnj.devicetracker.ui.DeviceDetailFragment;
import com.jnj.devicetracker.ui.MainActivity;

import java.util.List;

import static com.jnj.devicetracker.model.Device.LASTCHECKEDOUTBY;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {

    private List<Device> deviceList;
    private MainActivity activity;

    public DeviceListAdapter(List<Device> deviceList, MainActivity activity) {
        this.deviceList = deviceList;
        this.activity=activity;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {


        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.device_list_row, viewGroup, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.id = deviceList.get(i).getId();
        String AVAILABLE = "Available";
        Device device = deviceList.get(i);
          SpannableStringBuilder spannable = new SpannableStringBuilder();
        spannable.append(device.getDevice() + " - " + device.getOS());
        spannable.append("\n");
        if (device.isCheckedOut())
            spannable.append(LASTCHECKEDOUTBY+": "+device.getLastCheckedOutBy());
        else
            spannable.append("Available");

        String originalString = spannable.toString();
        int start = originalString.indexOf(AVAILABLE);
        int end = originalString.indexOf(AVAILABLE) + AVAILABLE.length();

        if (start >= 0 && end > 0) {
            spannable.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        viewHolder.textView.setText(spannable);



    }

    @Override
    public int getItemCount() {
        return (null != deviceList ? deviceList.size() : 0);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        private  int id;
        ViewHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(android.R.id.text1);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activity.mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putInt(Device.DEVICE_ID, id);

                        DeviceDetailFragment fragment = new DeviceDetailFragment();
                        fragment.setArguments(arguments);
                        activity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.device_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, DeviceDetailActivity.class);
                        intent.putExtra(Device.DEVICE_ID, id);

                        context.startActivity(intent);

                    }
                }
            });
        }
    }

}


