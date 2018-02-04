package com.jnj.devicetracker.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jnj.devicetracker.model.transaction;
import com.jnj.devicetracker.utils.LogUtils;

import java.util.Date;
import java.util.List;

import io.realm.Realm;

public class TransactionDataPersistence {




    public static void removeAll() {
        executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.allObjects(transaction.class).clear();
            }
        });
    }

    public static void save(@NonNull final transaction data) {
        executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(data);
            }
        });
    }

    public static void save(@NonNull final List<transaction> dataList) {
        executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(dataList);
            }
        });
    }

//    public static void toggleChecked(@NonNull final int id, final boolean status) {
//        executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                    transaction realmtransaction = realm.where(transaction.class).equalTo(transaction.transaction_ID, id).findFirst();
//                    if (realmtransaction != null) {
//                        realmtransaction.setCheckedOut(status);
//                        realmtransaction.setToBeUpdated(true);
//                    }
//
//            }
//        });
//    }
//
//
//    public static void setLastCheckedOutBy(@NonNull final int id, final String name) {
//        executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                transaction realmtransaction = realm.where(transaction.class).equalTo(transaction.transaction_ID, id).findFirst();
//                if (realmtransaction != null) {
//                    realmtransaction.setLastCheckedOutBy(name);
//                }
//
//            }
//        });
//    }
//
//
//    public static void setLastCheckedOutDate(@NonNull final int id) {
//        executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                transaction realmtransaction = realm.where(transaction.class).equalTo(transaction.transaction_ID, id).findFirst();
//                if (realmtransaction != null) {
//                    realmtransaction.setLastCheckedOutDate(new Date());
//                }
//
//            }
//        });
//    }





//    @NonNull
//    public static transaction getByID(@NonNull Realm realm, int id) {
//        return realm.where(transaction.class).equalTo(transaction.transaction_ID, id).findFirst();
//    }
//
//    @NonNull
//    public static List<transaction> getAllToBeUpdated(@NonNull Realm realm) {
//        return realm.where(transaction.class).equalTo(transaction.TOBEUPDATED, true).findAll();
//    }




//    public static void markAsUpdated(final List<Integer> iDs) {
//        executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                for (int id: iDs) {
//                    transaction realmtransaction = realm.where(transaction.class).equalTo(transaction.transaction_ID, id).findFirst();
//                    if (realmtransaction != null) {
//                        realmtransaction.setToBeUpdated(false);
//                    }
//                }
//
//            }
//        });
//    }




    @NonNull
    public static List<transaction> getAll(@NonNull Realm realm) {
        return realm.where(transaction.class).findAll();
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

        return (long) realm.where(transaction.class).max(transaction.UID) + 1;

    }


}
