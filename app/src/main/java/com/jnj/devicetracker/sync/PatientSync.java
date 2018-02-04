package com.jnj.devicetracker.sync;

import android.content.Context;
import android.support.annotation.NonNull;
import com.jnj.devicetracker.sync.event.SyncType;

class PatientSync extends AbstractSync {

    PatientSync(@NonNull Context context) {
        super(context);
    }

    @Override
    protected SyncType getSyncType() {
        return SyncType.PATIENT;
    }

    @Override
    protected void post() {
        // not implemented
    }

    @Override
    protected void get() {
        // not implemented
    }

    @Override
    protected void delete() {

    }
}
