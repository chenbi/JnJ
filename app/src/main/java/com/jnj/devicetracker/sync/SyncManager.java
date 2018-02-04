package com.jnj.devicetracker.sync;

import android.content.Context;
import android.support.annotation.NonNull;
import com.jnj.devicetracker.sync.event.SyncType;

import java.util.HashMap;

class SyncManager {

    private HashMap<SyncType, AbstractSync> syncMap;

    SyncManager(@NonNull Context context) {
        syncMap = new HashMap<>();
        syncMap.put(SyncType.TRANSACTION, new transactionSync (context));

        syncMap.put(SyncType.DEVICE, new DeviceSync(context));
        syncMap.put(SyncType.MEDICINE, new MedicineSync(context));
        syncMap.put(SyncType.PATIENT, new MedicineSync(context));
    }

    void doSync(@NonNull SyncType syncType) {
        syncMap.get(syncType).sync();
    }
}
