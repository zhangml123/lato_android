package com.platon.wallet.db.sqlite;

import com.platon.wallet.utils.LogUtils;
import com.platon.wallet.app.Constants;
import com.platon.wallet.db.entity.TransactionRecordEntity;


import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class TransactionRecordDao {

    private TransactionRecordDao() {

    }

    public static List<TransactionRecordEntity> getTransactionRecordList() {

        List<TransactionRecordEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<TransactionRecordEntity> results = realm.where(TransactionRecordEntity.class)
                    .findAll();
            if (results != null) {
                list = realm.copyFromRealm(results);
            }
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }

    public static boolean insertTransactionRecord(TransactionRecordEntity transactionRecordEntity) {

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(transactionRecordEntity);
            realm.commitTransaction();
            return true;
        } catch (Exception exp) {
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return false;
    }

    public static boolean deleteTimeoutTransactionRecord() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.where(TransactionRecordEntity.class)
                            .lessThan("timeStamp", System.currentTimeMillis() - Constants.Common.TRANSACTION_TIMEOUT_WITH_MILLISECOND)
                            .findAll()
                            .deleteAllFromRealm();
                }
            });
            return true;
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
            return false;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public static boolean isResendTransaction(TransactionRecordEntity transactionRecordEntity) {
        boolean isResendTransaction = false;
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<TransactionRecordEntity> results = realm.where(TransactionRecordEntity.class)
                    .findAll();
            return realm.copyFromRealm(results).contains(transactionRecordEntity);
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return isResendTransaction;
    }
}
