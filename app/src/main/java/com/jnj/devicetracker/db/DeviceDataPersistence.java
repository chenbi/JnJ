package com.jnj.devicetracker.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jnj.devicetracker.model.Device;
import com.jnj.devicetracker.utils.LogUtils;
import io.realm.Realm;

import java.util.Date;
import java.util.List;

public class DeviceDataPersistence {




    public static void removeAll() {
        executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.allObjects(Device.class).clear();
            }
        });
    }

    public static void save(@NonNull final Device data) {
        executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(data);
            }
        });
    }

    public static void save(@NonNull final List<Device> dataList) {
        executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(dataList);
            }
        });
    }

    public static void toggleChecked(@NonNull final int id, final boolean status) {
        executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                    Device realmDevice = realm.where(Device.class).equalTo(Device.DEVICE_ID, id).findFirst();
                    if (realmDevice != null) {
                        realmDevice.setCheckedOut(status);
                        realmDevice.setToBeUpdated(true);
                    }

            }
        });
    }


    public static void setLastCheckedOutBy(@NonNull final int id, final String name) {
        executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Device realmDevice = realm.where(Device.class).equalTo(Device.DEVICE_ID, id).findFirst();
                if (realmDevice != null) {
                    realmDevice.setLastCheckedOutBy(name);
                }

            }
        });
    }


    public static void setLastCheckedOutDate(@NonNull final int id) {
        executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Device realmDevice = realm.where(Device.class).equalTo(Device.DEVICE_ID, id).findFirst();
                if (realmDevice != null) {
                    realmDevice.setLastCheckedOutDate(new Date());
                }

            }
        });
    }





    @NonNull
    public static Device getByID(@NonNull Realm realm, int id) {
        return realm.where(Device.class).equalTo(Device.DEVICE_ID, id).findFirst();
    }

    @NonNull
    public static List<Device> getAllToBeUpdated(@NonNull Realm realm) {
        return realm.where(Device.class).equalTo(Device.TOBEUPDATED, true).findAll();
    }




    public static void markAsUpdated(final List<Integer> iDs) {
        executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (int id: iDs) {
                    Device realmDevice = realm.where(Device.class).equalTo(Device.DEVICE_ID, id).findFirst();
                    if (realmDevice != null) {
                        realmDevice.setToBeUpdated(false);
                    }
                }

            }
        });
    }




    @NonNull
    public static List<Device> getAll(@NonNull Realm realm) {
        return realm.where(Device.class).findAll();
    }

    private static void executeTransaction(@NonNull Realm.Transaction transaction) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(transaction);
        } catch (Throwable e) {
            LogUtils.e("executeTransaction", e);
        } finally {
            close(realm);
        }
    }

    private static void close(@Nullable Realm realm) {
        if (realm != null) {
            realm.close();
        }
    }

    @NonNull
    public static long AUTO_INCREMENT(@NonNull Realm realm) {

        return (long) realm.where(Device.class).max(Device.UID) + 1;

    }


}
