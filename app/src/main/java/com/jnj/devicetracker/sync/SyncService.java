package com.jnj.devicetracker.sync;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jnj.devicetracker.model.Device;
import com.jnj.devicetracker.sync.event.EventBusManager;
import com.jnj.devicetracker.sync.event.SyncRequestEvent;
import com.jnj.devicetracker.sync.event.SyncType;
import com.jnj.devicetracker.utils.LogUtils;
import com.jnj.devicetracker.utils.NetworkUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SyncService extends Service {

    private ExecutorService executor = null;
    private SyncManager syncManager = null;
    private Context cxt;
    @Override
    public void onCreate() {
        LogUtils.i("SyncService onCreate");
        executor = Executors.newSingleThreadExecutor();
        syncManager = new SyncManager(getApplicationContext());
        EventBusManager.register(this);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (NetworkUtils.isNetworkConnected(context))
                    request(SyncType.TRANSACTION);

            }
        };

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        LogUtils.i("SyncService onDestroy");
        EventBusManager.unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(@NonNull final SyncRequestEvent event) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                syncManager.doSync(event.getSyncType());
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void start(@NonNull Context context) {

        Intent intent = new Intent(context, SyncService.class);
        context.startService(intent);
    }

    public static void request(@NonNull SyncType type) {
        EventBusManager.send(new SyncRequestEvent(type));
    }





}
