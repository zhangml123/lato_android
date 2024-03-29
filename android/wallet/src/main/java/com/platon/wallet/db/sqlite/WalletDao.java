package com.platon.wallet.db.sqlite;

import com.platon.wallet.entity.Wallet;
import com.platon.wallet.utils.LogUtils;
import com.platon.wallet.db.entity.WalletEntity;
import com.platon.wallet.engine.NodeManager;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class WalletDao {

    private WalletDao() {
    }

    /**
     * 获取钱包列表，根据updateTime升序
     * updateTime是指钱更新信息的时间
     *
     * @return
     */
    public static List<WalletEntity> getWalletInfoList() {
        List<WalletEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<WalletEntity> results = realm.where(WalletEntity.class)
                    .equalTo("chainId", NodeManager.getInstance().getChainId())
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
    public static List<WalletEntity> getAllWalletInfoList() {
        List<WalletEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<WalletEntity> results = realm.where(WalletEntity.class)
                    //.equalTo("chainId", NodeManager.getInstance().getChainId())
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

    public static WalletEntity getWalletInfoFromUuid(String uuid){
        WalletEntity walletEntity = null;
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            WalletEntity result = realm.where(WalletEntity.class)
                    .equalTo("chainId", NodeManager.getInstance().getChainId())
                    .equalTo("uuid", uuid)
                    .findFirst();
            if (result != null) {
                walletEntity =  realm.copyFromRealm(result);
            }
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return walletEntity;
    }
    public static WalletEntity getWalletInfoFromName(String name) {
        WalletEntity walletEntity = null;
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            WalletEntity result = realm.where(WalletEntity.class)
                    .equalTo("chainId", NodeManager.getInstance().getChainId())
                    .equalTo("name", name)
                    .findFirst();
            if (result != null) {
                walletEntity =  realm.copyFromRealm(result);
            }
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return walletEntity;
    }
    public static List<WalletEntity> getWalletInfoListFromNodeId(long nodeId) {
        List<WalletEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<WalletEntity> results = realm.where(WalletEntity.class)
                    .equalTo("nodeId", nodeId)
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
    public static String getWalletNameByAddress(String prefixAddress) {
        String walletName = null;
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            WalletEntity walletEntity = realm.where(WalletEntity.class)
                    .equalTo("chainId", NodeManager.getInstance().getChainId())
                    .equalTo("address", prefixAddress, Case.INSENSITIVE)
                    .findFirst();
            if (walletEntity != null) {
                walletName = walletEntity.getName();
            }
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return walletName;
    }

    public static String getWalletAvatarByAddress(String prefixAddress) {
        String walletAvatar = null;
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            WalletEntity walletEntity = realm.where(WalletEntity.class)
                    .equalTo("chainId", NodeManager.getInstance().getChainId())
                    .equalTo("address", prefixAddress, Case.INSENSITIVE)
                    .findFirst();
            if (walletEntity != null) {
                walletAvatar = walletEntity.getAvatar();
            }
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return walletAvatar;
    }

    public static boolean insertWalletInfo(WalletEntity entity) {
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

    public static boolean updateNameWithUuid(String uuid, String name) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(WalletEntity.class)
                    .beginGroup()
                    .equalTo("uuid", uuid)
                    .and()
                    .equalTo("chainId", NodeManager.getInstance().getChainId())
                    .endGroup()
                    .findFirst()
                    .setName(name);
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

    public static boolean updateBackedUpWithUuid(String uuid, boolean backedUp) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(WalletEntity.class)
                    .beginGroup()
                    .equalTo("uuid", uuid)
                    .and()
                    .equalTo("chainId", NodeManager.getInstance().getChainId())
                    .endGroup()
                    .findFirst()
                    .setBackedUp(backedUp);
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

    public static boolean updateMnemonicWithUuid(String uuid, String mnemonic) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(WalletEntity.class)
                    .beginGroup()
                    .equalTo("uuid", uuid)
                    .and()
                    .equalTo("chainId", NodeManager.getInstance().getChainId())
                    .endGroup()
                    .findFirst()
                    .setMnemonic(mnemonic);
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

    public static boolean updateUpdateTimeWithUuid(String uuid, long updateTime) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(WalletEntity.class)
                    .beginGroup()
                    .equalTo("uuid", uuid)
                    .and()
                    .equalTo("chainId", NodeManager.getInstance().getChainId())
                    .endGroup()
                    .findFirst()
                    .setUpdateTime(updateTime);
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

    public static boolean updateSelectedWithUuid(String uuid, boolean selected) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            Objects.requireNonNull(realm.where(WalletEntity.class)
                    .beginGroup()
                    .equalTo("uuid", uuid)
                    .and()
                    //.equalTo("chainId", NodeManager.getInstance().getChainId())
                    .endGroup()
                    .findFirst())
                    .setSelected(selected);
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

    public static boolean deleteWalletInfo(String uuid) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(WalletEntity.class)
                    .beginGroup()
                    .equalTo("uuid", uuid)
                    .and()
                    .equalTo("chainId", NodeManager.getInstance().getChainId())
                    .endGroup()
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


    public static boolean updateWallet(Wallet newWallet) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(newWallet.buildWalletInfoEntity());
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


}
