package com.platon.wallet.db.sqlite;

import com.platon.wallet.db.entity.AssetEntity;
import com.platon.wallet.engine.NodeManager;
import com.platon.wallet.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class AssetDao {
    private AssetDao() {
    }
    public static List<AssetEntity> getAssetInfoList() {
        List<AssetEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<AssetEntity> results = realm.where(AssetEntity.class)
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
    public static List<AssetEntity> getHomeAssetList() {
        List<AssetEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<AssetEntity> results = realm.where(AssetEntity.class)
                    .equalTo("isHome", true)
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
    public static List<AssetEntity> getAssetInfoListByWalletId(String walletId) {
        List<AssetEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<AssetEntity> results = realm.where(AssetEntity.class)
                    .equalTo("walletId", walletId)
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
    public static List<AssetEntity> getHomeAssetInfoListByWalletId(String walletId) {
        List<AssetEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<AssetEntity> results = realm.where(AssetEntity.class)
                    .equalTo("walletId", walletId)
                    .equalTo("isHome", true)
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
    public static boolean insertAssetInfo(AssetEntity entity) {
        Realm realm = null;
        System.out.println("entity111111 = "+ entity.getContractAddress());
        if(entity.getContractAddress() == null ) return false;
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
    public static AssetEntity getAssetFromContractAddress(String contractAddress) {
        Realm realm = null;
        AssetEntity assetEntity = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            AssetEntity results = realm.where(AssetEntity.class)
                    .equalTo("contractAddress", contractAddress)
                    .findFirst();
            if (results != null) {
                assetEntity = realm.copyFromRealm(results);
            }
            realm.commitTransaction();
        } catch (Exception exp) {
            if (realm != null) {
                realm.cancelTransaction();
            }
        }finally {
            if (realm != null) {
                realm.close();
            }
        }
        return assetEntity;
    }
    public static AssetEntity getAssetById(String id) {
        Realm realm = null;
        AssetEntity assetEntity = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            AssetEntity result = realm.where(AssetEntity.class)
                    .equalTo("id", id)
                    .findFirst();
            if (result != null) {
                assetEntity = realm.copyFromRealm(result);
            }
            realm.commitTransaction();
        } catch (Exception exp) {
            if (realm != null) {
                realm.cancelTransaction();
            }
        }finally {
            if (realm != null) {
                realm.close();
            }
        }
        return assetEntity;
    }
    public static boolean updateAssetBalance(String id,String balance){
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(AssetEntity.class)
                    .equalTo("id", id)
                    .findFirst()
                    .setBalance(balance);

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
    public static boolean deleteAssetInfo(String id) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(AssetEntity.class)
                    .equalTo("id", id)
                    .findAll()
                    .deleteFirstFromRealm();
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

    public static AssetEntity getAssetFromContractAddressAndWalletId(String walletId, String contractAddress) {
        Realm realm = null;
        AssetEntity assetEntity = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            AssetEntity result = realm.where(AssetEntity.class)
                    .equalTo("walletId", walletId)
                    .equalTo("contractAddress", contractAddress)
                    .findFirst();
            if (result != null) {
                assetEntity = realm.copyFromRealm(result);
            }
            realm.commitTransaction();
        } catch (Exception exp) {
            if (realm != null) {
                realm.cancelTransaction();
            }
        }finally {
            if (realm != null) {
                realm.close();
            }
        }
        return assetEntity;
    }
}
