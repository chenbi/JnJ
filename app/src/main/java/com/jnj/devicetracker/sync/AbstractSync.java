package com.jnj.devicetracker.sync;

import android.content.Context;
import android.support.annotation.NonNull;
import com.jnj.devicetracker.sync.event.SyncEvent;
import com.jnj.devicetracker.sync.event.SyncStatus;
import com.jnj.devicetracker.sync.event.SyncType;
import com.jnj.devicetracker.utils.NetworkUtils;

abstract class AbstractSync {

    private Context context;

    public AbstractSync(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    void sync() {
        if (NetworkUtils.isNetworkConnected(context)) {
            SyncEvent.send(getSyncType(), SyncStatus.IN_PROGRESS);
            delete();
            post();
            get();
            SyncEvent.send(getSyncType(), SyncStatus.COMPLETED);
        }
    }

    protected abstract SyncType getSyncType();

    protected abstract void post();
    protected abstract void get();
    protected abstract void delete();


}
