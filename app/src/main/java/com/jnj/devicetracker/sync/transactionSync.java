package com.jnj.devicetracker.sync;

import android.content.Context;
import android.support.annotation.NonNull;

import com.jnj.devicetracker.db.DeviceDataPersistence;
import com.jnj.devicetracker.db.TransactionDataPersistence;
import com.jnj.devicetracker.model.transaction;
import com.jnj.devicetracker.sync.event.SyncType;
import com.jnj.devicetracker.utils.LogUtils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

import static com.jnj.devicetracker.model.transaction.AMOUNT;

import static com.jnj.devicetracker.model.transaction.CURRENCY;
import static com.jnj.devicetracker.model.transaction.ITEM;
import static com.jnj.devicetracker.model.transaction.METHOD;
import static com.jnj.devicetracker.model.transaction.OBJECT_ID;
import static com.jnj.devicetracker.model.transaction.RELATIONSHIP;
import static com.jnj.devicetracker.model.transaction.TAX;
import static com.jnj.devicetracker.model.transaction.TIME;
import static com.jnj.devicetracker.model.transaction.TYPE;

class transactionSync extends AbstractSync {

    final static String BASE_URL = "https://api.backendless.com/14B07F45-B077-F936-FF75-176789BBE700/5F3401ED-7083-9C1E-FFB6-AE4075B43900/data";
    final static String TRANSACTION_URL= BASE_URL +"/transaction";

    transactionSync (@NonNull Context context) {
        super(context);
    }

    @Override
    protected SyncType getSyncType() {
        return SyncType.TRANSACTION;
    }



    @Override
    protected void get() {
        LogUtils.d("Devices GET request start");
        List<transaction> deviceList = downloadDevices();
        LogUtils.v("%d new items available", deviceList.size());
        //printDevices(deviceList);
        TransactionDataPersistence.removeAll();
        TransactionDataPersistence.save(deviceList);

        LogUtils.d("Devices GET request end");
    }

    @NonNull
    private List<transaction> downloadDevices() {
        List<transaction> transactionList = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(TRANSACTION_URL).build();
            Response responses = null;

            responses = client.newCall(request).execute();
            String jsonData = responses.body().string();
            JSONArray devices = new JSONArray(jsonData);
            transactionList = new ArrayList<>(devices.length());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZZ");
            for (int i = 0; i < devices.length(); i++) {
                JSONObject object = devices.getJSONObject(i);
                transaction transaction = new transaction();
                transaction.setUid (object.getString(OBJECT_ID));
                transaction.setObjectId (object.getString(OBJECT_ID));
                transaction.setAmount (object.getDouble (AMOUNT));
                transaction.setCurrency (object.getString(CURRENCY));
                transaction.setItem (object.getString(ITEM));
                transaction.setMethod (object.getString(METHOD));
                transaction.setTax (object.getDouble(TAX));
                transaction.setRelationship (object.getString(RELATIONSHIP));
                //transaction.setTime (new Date(object.getString (TIME)));
                transaction.setTime (new Date ());

                transaction.setType (object.getString(TYPE));

                transactionList.add(transaction);
            }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        return transactionList;
    }



    @Override
    protected void post() {
        Realm realm = Realm.getDefaultInstance();
       // List<transaction> deviceList = TransactionDataPersistence.getAllToBeUpdated(realm);
//        if (!deviceList.isEmpty()) {
//            LogUtils.d("Devices POST request start");
//            LogUtils.v("%d modified devices need to be uploaded to server", deviceList.size());
//            updateDevices(deviceList);
//            printDevices(deviceList);
//            DeviceDataPersistence.markAsUpdated(getIDs(deviceList));
//            LogUtils.d("Devices POST request end");
//        }
//        realm.close();
    }

    private void updateDevices(List<transaction> deviceList) {

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

//    private void printDevices(List<transaction> deviceList) {
//        for (transaction device : deviceList) {
//            LogUtils.v("transaction id: %s transaction: %s Last Checked Out date: ", device.getId(), device.getDevice(), device.getLastCheckedOutDate());
//        }
//    }



//    @NonNull
//    private List<Integer> getIDs(List<transaction> deviceList) {
//        List<Integer> IDs = new ArrayList<>();
//        for (transaction device : deviceList) {
//            IDs.add(device.getId());
//        }
//
//        return IDs;
//    }


}
