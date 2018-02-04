package com.jnj.devicetracker.sync;

import android.content.Context;
import android.support.annotation.NonNull;

import com.jnj.devicetracker.model.Device;
import com.jnj.devicetracker.db.DeviceDataPersistence;
import com.jnj.devicetracker.sync.event.SyncType;
import com.jnj.devicetracker.utils.LogUtils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.jnj.devicetracker.model.Device.DEVICE;
import static com.jnj.devicetracker.model.Device.DEVICE_ID;
import static com.jnj.devicetracker.model.Device.ISCHECKEDOUT;
import static com.jnj.devicetracker.model.Device.LASTCHECKEDOUTBY;
import static com.jnj.devicetracker.model.Device.LASTCHECKEDOUTDATE;
import static com.jnj.devicetracker.model.Device.MANUFACTURER;
import static com.jnj.devicetracker.model.Device.OS;

class DeviceSync extends AbstractSync {

    final static String BASE_URL = "http://private-1cc0f-devicecheckout.apiary-mock.com";
    final static String DEVICES_URL = BASE_URL +"/devices";

    DeviceSync(@NonNull Context context) {
        super(context);
    }

    @Override
    protected SyncType getSyncType() {
        return SyncType.DEVICE;
    }



    @Override
    protected void get() {
        LogUtils.d("Devices GET request start");
        List<Device> deviceList = downloadDevices();
        LogUtils.v("%d new items available", deviceList.size());
        printDevices(deviceList);
        DeviceDataPersistence.removeAll();
        DeviceDataPersistence.save(deviceList);

        LogUtils.d("Devices GET request end");
    }

    @NonNull
    private List<Device> downloadDevices() {
        List<Device> deviceList = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(DEVICES_URL).build();
            Response responses = null;

            responses = client.newCall(request).execute();
            String jsonData = responses.body().string();
            JSONArray transactions = new JSONArray(jsonData);
            deviceList = new ArrayList<>(transactions.length());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZZ");
            for (int i = 0; i < transactions.length(); i++) {
                JSONObject object = transactions.getJSONObject(i);
                Device device = new Device();
                device.setUid(object.getInt(DEVICE_ID));
                device.setId(object.getInt(DEVICE_ID));
                device.setDevice(object.getString(DEVICE));
                device.setOS(object.getString(OS));
                device.setManufacturer(object.getString(MANUFACTURER));
                device.setCheckedOut(object.getBoolean(ISCHECKEDOUT));
                if (object.getBoolean(ISCHECKEDOUT)) {
                    device.setLastCheckedOutBy(object.getString(LASTCHECKEDOUTBY));
                    Date date = format.parse(object.getString(LASTCHECKEDOUTDATE));
                    device.setLastCheckedOutDate(date);
                }
                deviceList.add(device);
            }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
        }

        return deviceList;
    }



    @Override
    protected void post() {
        Realm realm = Realm.getDefaultInstance();
        List<Device> deviceList = DeviceDataPersistence.getAllToBeUpdated(realm);
        if (!deviceList.isEmpty()) {
            LogUtils.d("Devices POST request start");
            LogUtils.v("%d modified devices need to be uploaded to server", deviceList.size());
            updateDevices(deviceList);
            printDevices(deviceList);
            DeviceDataPersistence.markAsUpdated(getIDs(deviceList));
            LogUtils.d("Devices POST request end");
        }
        realm.close();
    }

    private void updateDevices(List<Device> deviceList) {

        //for devices that don't have uid, post them to TRANSACTION_URL, the
        // others that have uid, post them to TRANSACTION_URL/{device_id} one by one in a loop
//        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//
//        OkHttpClient client = new OkHttpClient();
//        RequestBody body = RequestBody.create(JSON, json);
//        Request request = new Request.Builder()
//                .url(TRANSACTION_URL)
//                .post(body)
//                .build();
//        Response response = null;
//        try {
//            response = client.newCall(request).execute();
//            LogUtils.v(response.body().string());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }


    @Override
    protected void delete() {

    }

    private void printDevices(List<Device> deviceList) {
        for (Device device : deviceList) {
            LogUtils.v("Device id: %s Device: %s Last Checked Out date: ", device.getId(), device.getDevice(), device.getLastCheckedOutDate());
        }
    }



    @NonNull
    private List<Integer> getIDs(List<Device> deviceList) {
        List<Integer> IDs = new ArrayList<>();
        for (Device device : deviceList) {
            IDs.add(device.getId());
        }

        return IDs;
    }


}
