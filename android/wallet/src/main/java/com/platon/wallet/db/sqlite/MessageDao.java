package com.platon.wallet.db.sqlite;

import com.platon.wallet.db.entity.AssetEntity;
import com.platon.wallet.db.entity.MessageEntity;
import com.platon.wallet.engine.NodeManager;
import com.platon.wallet.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MessageDao {
    private MessageDao() {
    }
    public static List<MessageEntity> getMessageList() {
        List<MessageEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<MessageEntity> results = realm.where(MessageEntity.class)
                    .sort("updateTime", Sort.ASCENDING)
                    .findAll();
            if (results != null) {
                list = realm.copyFromRealm(results);
            }
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }
    /*public static List<MessageEntity> getMessageListGroup() {
        List<MessageEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<MessageEntity> results = realm.where(MessageEntity.class)
                    .beginGroup()
                    .equalTo("uuid", uuid)
                    .endGroup()
                    .sort("updateTime", Sort.ASCENDING)
                    .findAll();
            if (results != null) {
                list = realm.copyFromRealm(results);
            }
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }*/
    public static MessageEntity getMessageById(String id) {
        MessageEntity messageEntity = null;
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            MessageEntity result = realm.where(MessageEntity.class)
                    .equalTo("id", id)
                    .findFirst();
            if (result != null) {
                messageEntity = realm.copyFromRealm(result);
            }
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return messageEntity;
    }
    public static List<MessageEntity> getSendTokenMessageList() {
        List<MessageEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<MessageEntity> results = realm.where(MessageEntity.class)
                    .equalTo("type","send_token")
                    .sort("updateTime", Sort.ASCENDING)
                    .findAll();
            if (results != null) {
                list = realm.copyFromRealm(results);
            }
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }
    public static List<MessageEntity> getNewMessageList() {
        List<MessageEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<MessageEntity> results = realm.where(MessageEntity.class)
                    .equalTo("read",false)
                    .sort("updateTime", Sort.ASCENDING)
                    .findAll();
            if (results != null) {
                list = realm.copyFromRealm(results);
            }
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }
    public static boolean insertMessage(MessageEntity entity) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.copyToRealm(entity);
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


    public static boolean setReadMessage(String messageId) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(MessageEntity.class)
                    .equalTo("id", messageId)
                    .findFirst()
                    .setRead(true);
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
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
}
