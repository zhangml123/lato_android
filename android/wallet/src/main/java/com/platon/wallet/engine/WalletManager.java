package com.platon.wallet.engine;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;


import com.platon.wallet.db.entity.AssetEntity;
import com.platon.wallet.db.sqlite.AssetDao;
import com.platon.wallet.db.sqlite.TransactionDao;
import com.platon.wallet.entity.Asset;
import com.platon.wallet.entity.TransactionStatus;
import com.platon.wallet.entity.TransactionType;
import com.platon.wallet.network.ApiErrorCode;
import com.platon.wallet.network.ApiRequestBody;
import com.platon.wallet.network.ApiResponse;
import com.platon.wallet.utils.LogUtils;
import com.platon.wallet.app.CustomThrowable;
import com.platon.wallet.db.entity.WalletEntity;
import com.platon.wallet.db.sqlite.WalletDao;
import com.platon.wallet.entity.AccountBalance;
import com.platon.wallet.entity.Wallet;
import com.platon.wallet.event.Event;
import com.platon.wallet.event.EventPublisher;
import com.platon.wallet.utils.AmountUtil;
import com.platon.wallet.utils.BigDecimalUtil;
import com.platon.wallet.utils.JZWalletUtil;
import com.platon.wallet.app.Constants;

import org.greenrobot.eventbus.EventBus;

import org.reactivestreams.Publisher;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
//import org.web3j.crypto.Address;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.PlatonCall;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;


import com.platon.wallet.utils.PreferenceTool;
import com.platon.wallet.utils.RxUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.plugins.RxJavaPlugins;
import retrofit2.Response;

import static com.platon.wallet.BuildConfig.URL_ALAYA_RPC;
import static com.platon.wallet.BuildConfig.URL_PLATON_TESTNET_RPC;
import static com.platon.wallet.entity.Asset.ASSET_CONTRACT;
import static com.platon.wallet.entity.Asset.ASSET_LAT;


/**
 * @author ziv
 */
public class WalletManager {
    public static final int CODE_OK = 0;
    public static final int CODE_ERROR_NAME = -1;
    public static final int CODE_ERROR_PASSWORD = -2;
    public static final int CODE_ERROR_KEYSTORE = -3;
    public static final int CODE_ERROR_PRIVATEKEY = -4;
    public static final int CODE_ERROR_MNEMONIC = -5;
    public static final int CODE_ERROR_WALLET_EXISTS = -200;
    public static final int CODE_ERROR_UNKNOW = -999;

    private List<Wallet> mWalletList = new ArrayList<>();
    private List<Asset> mAssetList = new ArrayList<>();
    public static final int CODE_ERROR_INVALIA_ADDRESS = -5;
    private Wallet currentWallet ;
    private Disposable mFetchWalletBalanceDisposable = new CompositeDisposable();
    /**
     * 记录上次的余额
     */
    private BigDecimal mSumAccountBalance = BigDecimal.ZERO;

    private WalletManager() {
    }
    private static class InstanceHolder {
        private static volatile WalletManager INSTANCE = new WalletManager();
    }
    public static WalletManager getInstance() {
        return WalletManager.InstanceHolder.INSTANCE;
    }

    public BigDecimal getSumAccountBalance() {
        return mSumAccountBalance;
    }

    public void setSumAccountBalance(BigDecimal sumAccountBalance) {
        this.mSumAccountBalance = sumAccountBalance;
    }
    public Wallet getCurrentWallet(){
        return currentWallet;
    }
    @SuppressLint("CheckResult")
    public  List<Boolean>setSelectedWalletWithUuid(String uuid){
        System.out.println("setSelectedWalletWithUuid 11");
       return Flowable
                .fromCallable(new Callable<List<WalletEntity>>() {
                    @Override
                    public List<WalletEntity> call() throws Exception {
                        return WalletDao.getAllWalletInfoList();
                    }
                }).flatMap(new Function<List<WalletEntity>, Publisher<Boolean>>() {
                   @Override
                   public Publisher<Boolean> apply( List<WalletEntity> walletEntities) throws Exception {
                       WalletDao.updateSelectedWithUuid(uuid,true);
                       return Flowable.range(0, walletEntities.size()).map(new Function<Integer, Boolean>() {
                           @Override
                           public Boolean apply(Integer integer) throws Exception {
                               if(!walletEntities.get(integer).getUuid().equals(uuid)){
                                    WalletDao.updateSelectedWithUuid(walletEntities.get(integer).getUuid(),false);
                               }
                               return true;
                           }
                       });
                   }
               })
               .toList()
               .blockingGet();
    }
 
    public Wallet getSelectedWallet() {
        if (mWalletList.size() == 0) {
            return getWalletListFromDB()
                    .toFlowable()
                    .flatMap(new Function<List<Wallet>, Publisher<Wallet>>() {
                @Override
                public Publisher<Wallet> apply(List<Wallet> walletList) throws Exception {
                    return Flowable.fromIterable(walletList);
                }
            })
                    .filter(new Predicate<Wallet>() {
                        @Override
                        public boolean test(Wallet wallet) throws Exception {
                            return wallet.isSelected();
                        }
                    })
                    .firstElement()
                    .defaultIfEmpty(Wallet.getNullInstance())
                    .onErrorReturnItem(Wallet.getNullInstance())
                    .blockingGet();
        }
        return getSelectedWalletFromWalletList();
    }

    private Wallet getSelectedWalletFromWalletList() {
        return Flowable.fromIterable(mWalletList)
                .filter(new Predicate<Wallet>() {
                    @Override
                    public boolean test(Wallet wallet) throws Exception {
                        return wallet.isSelected();
                    }
                })
                .firstElement()
                .defaultIfEmpty(mWalletList.get(0))
                .onErrorReturnItem(mWalletList.get(0))
                .blockingGet();
    }

    public void setWalletList(List<Wallet> walletList) {
        this.mWalletList = walletList;
    }
    public void setAssetList(List<Asset> assetList){
            this.mAssetList = assetList;
    }
    public List<Asset> getAssetList() {
        return mAssetList;
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @SuppressLint({"CheckResult", "StaticFieldLeak"})
    public Single<String> refreshAssetBalance(){
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> emitter) throws Exception {
                if (mAssetList.isEmpty()) {emitter.onError(new CustomThrowable(0));}
                for(int i = 0 ; i < mAssetList.size(); i++){
                    Asset asset = mAssetList.get(i);
                    System.out.println("symbol = "+ asset.getSymbol());
                    if(asset.getAssetType() == ASSET_LAT){
                        int positon = i;
                        if(currentWallet != null ){
                            if (!mFetchWalletBalanceDisposable.isDisposed()) {
                                mFetchWalletBalanceDisposable.dispose();
                            }
                            mFetchWalletBalanceDisposable = WalletManager.getInstance().getAccountBalance(1)
                                    .compose(RxUtils.getSchedulerTransformer())
                                .subscribe(new Consumer<BigDecimal>() {
                                    @Override
                                    public void accept(BigDecimal balance) throws Exception {
                                        asset.setBalance(balance.toString());
                                        System.out.println("AssetListAdapter asset。 ="+asset.getBalance().toString());
                                        System.out.println("AssetListAdapter asset。getId ="+asset.getId().toString());
                                        AssetDao.updateAssetBalance(asset.getId(),balance.toString());
                                        mAssetList = getAssetListFromDb();
                                        System.out.println("AssetListAdapter asset。getId11111");
                                        emitter.onSuccess("success");
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        System.out.println("balance111111 throwable= "+throwable);
                                    }
                                });
                        }
                    }else{
                        int positon = i;
                        new AsyncTask<Void, Void, String>() {
                            @SuppressLint("StaticFieldLeak")
                            @Override
                            protected String doInBackground(Void... voids) {
                                String count= "";
                                try {
                                    System.out.println("AddAssetActivity onClick44");
                                    Web3j mWeb3j = Web3j.build(new HttpService(URL_ALAYA_RPC));
                                    Wallet walletEntity = WalletManager.getInstance().getWalletList().get(0);
                                    final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function("balanceOf",
                                            Arrays.<Type>asList(new Address(walletEntity.getAddress())),
                                            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
                                    String encodedFunction = FunctionEncoder.encode(function);
                                    PlatonCall ethCall = (PlatonCall)mWeb3j.platonCall(org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(walletEntity.getAddress(), asset.getContractAddress() , encodedFunction), DefaultBlockParameterName.LATEST).send();
                                    if(ethCall != null && ethCall.getValue() != null){
                                        List<Type> values = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters(), 210309L);
                                        if(!values.isEmpty() && values.get(0) != null){
                                            System.out.println("AssetListAdapter ethCall. values.get(0).getValue(); ="+values.get(0).getValue());
                                            System.out.println("AssetListAdapter ethCall. result ="+values.get(0).toString());
                                            count = values.get(0).getValue().toString();
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return count;
                            }
                            @Override
                            protected void onPostExecute(@NonNull String count) {
                                System.out.println("AssetListAdapter count ="+count);
                                asset.setBalance(count);
                                mAssetList.set( positon, asset);
                                System.out.println("AssetListAdapter asset。 ="+asset.getBalance().toString());
                                AssetDao.updateAssetBalance(asset.getId(),count);
                            }
                        }.execute();
                    }

                    System.out.println("refreshAssetBalance= i = "+i);

                    System.out.println("refreshAssetBalance= mAssetList.size() = "+mAssetList.size());

                    if(i== mAssetList.size() -1){
                        System.out.println("refreshAssetBalance= emitter.onSuccess(\"success\")= ");

                        emitter.onSuccess("success");
                    }
                }
            }
        });

    }

    public Single<String> refreshAssetBalance1(){
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> emitter) throws Exception {
                if (mAssetList.isEmpty()) {emitter.onError(new CustomThrowable(0));}
                for(int i = 0 ; i < mAssetList.size(); i++){
                    Asset asset = mAssetList.get(i);
                    System.out.println("symbol = "+ asset.getSymbol());
                    if(asset.getAssetType() == ASSET_LAT){
                        int positon = i;
                        if(currentWallet != null ){
                            if (!mFetchWalletBalanceDisposable.isDisposed()) {
                                mFetchWalletBalanceDisposable.dispose();
                            }
                            mFetchWalletBalanceDisposable = WalletManager.getInstance().getAccountBalance(1)
                                .compose(RxUtils.getSchedulerTransformer())
                                .subscribe(new Consumer<BigDecimal>() {
                                    @Override
                                    public void accept(BigDecimal balance) throws Exception {
                                        asset.setBalance(balance.toString());
                                        System.out.println("AssetListAdapter asset。 ="+asset.getBalance().toString());
                                        System.out.println("AssetListAdapter asset。getId ="+asset.getId().toString());
                                        AssetDao.updateAssetBalance(asset.getId(),balance.toString());
                                        mAssetList = getAssetListFromDb();
                                        System.out.println("AssetListAdapter asset。getId11111");

                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        System.out.println("balance111111 throwable= "+throwable);
                                    }
                                });
                        }
                    }

                    System.out.println("refreshAssetBalance= i = "+i);

                    System.out.println("refreshAssetBalance= mAssetList.size() = "+mAssetList.size());

                    if(i== mAssetList.size() -1){
                        System.out.println("refreshAssetBalance= emitter.onSuccess(\"success\")= ");
                        emitter.onSuccess("success");
                    }
                }
            }
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public void init() {
        System.out.println("WalletManager init ");
        mWalletList.clear();
        mWalletList = getWalletListFromDB().blockingGet();

        System.out.println("WalletManager init mWalletList.size = "+mWalletList.size());
        if(mWalletList.size() > 0 ){
            if(currentWallet == null){
                currentWallet = mWalletList.get(0);
            }
            mAssetList.clear();
            mAssetList = getAssetListFromDb();
        }
    }
    private List<Asset> getAssetListFromDb(){
        String walletId = currentWallet.getUuid();
        return Flowable
                .fromCallable(new Callable<List<AssetEntity>>() {
                    @Override
                    public List<AssetEntity> call() throws Exception {
                        return AssetDao.getAssetInfoListByWalletId(walletId);
                    }
                })
                .flatMap(new Function<List<AssetEntity>, Publisher<Asset>>() {
                    @Override
                    public Publisher<Asset> apply(final List<AssetEntity> assetEntities) throws Exception {
                        return Flowable.range(0, assetEntities.size()).map(new Function<Integer, Asset>() {
                            @Override
                            public Asset apply(Integer integer) throws Exception {
                                return assetEntities.get(integer).buildAsset();
                            }
                        });
                    }
                })
                .toList().blockingGet();
    }

    public Single<List<Wallet>> getWalletListFromDB() {
        return Flowable
            .fromCallable(new Callable<List<WalletEntity>>() {
                @Override
                public List<WalletEntity> call() throws Exception {
                    return WalletDao.getWalletInfoList();
                }
            })
            .flatMap(new Function<List<WalletEntity>, Publisher<Wallet>>() {
                @Override
                public Publisher<Wallet> apply(final List<WalletEntity> walletEntities) throws Exception {
                    return Flowable.range(0, walletEntities.size()).map(new Function<Integer, Wallet>() {
                        @Override
                        public Wallet apply(Integer integer) throws Exception {
                            if(walletEntities.get(integer).isSelected()){
                                currentWallet = walletEntities.get(integer).buildWallet();
                            }
                            return walletEntities.get(integer).buildWallet();
                        }
                    });
                }
            })
            .toList();
    }
    public Single<List<Wallet>> getWalletListFromNodeId(long nodeId) {
        return Flowable
                .fromCallable(new Callable<List<WalletEntity>>() {
                    @Override
                    public List<WalletEntity> call() throws Exception {
                        return WalletDao.getWalletInfoListFromNodeId(nodeId);
                    }
                })
                .flatMap(new Function<List<WalletEntity>, Publisher<Wallet>>() {
                    @Override
                    public Publisher<Wallet> apply(final List<WalletEntity> walletEntities) throws Exception {
                        return Flowable.range(0, walletEntities.size()).map(new Function<Integer, Wallet>() {
                            @Override
                            public Wallet apply(Integer integer) throws Exception {
                                if (integer == 0) {
                                    return   walletEntities.get(0).buildWallet();
                                } else {
                                    return walletEntities.get(integer).buildWallet();
                                }
                            }
                        });
                    }
                })
                .toList();
    }
    public void addWallet(Wallet wallet) {
        if (!mWalletList.contains(wallet)) {
            Wallet cloneWallet = wallet.clone();
            if (!isExistSelectedWallet()) {
                cloneWallet.setSelected(true);
                EventPublisher.getInstance().sendWalletSelectedChangedEvent();
            }
            mWalletList.add(cloneWallet);
        }
    }

    public void addAsset(Asset asset) {
        if (!mAssetList.contains(asset)) {
            mAssetList.add(asset);
        }
    }

    public void updateAccountBalance(AccountBalance accountBalance) {
        if (mWalletList.isEmpty() || accountBalance == null) {
            return;
        }

        int position = getPositionByAddress(accountBalance.getPrefixAddress());

        if (position == -1) {
            return;
        }

        Wallet wallet = mWalletList.get(position);
        wallet.setAccountBalance(accountBalance);
    }

    public void update() {

    }



    public List<Wallet> getWalletList() {
        if (mWalletList.isEmpty()) {
            return mWalletList = getWalletListFromDB().blockingGet();
        }
        Collections.sort(mWalletList);
        return mWalletList;
    }

    public String generateMnemonic() {
        return WalletServiceImpl.getInstance().generateMnemonic();
    }

    public String exportPrivateKey(String mnemonic) {
        return WalletServiceImpl.getInstance().exportPrivateKey(mnemonic);
    }

    private boolean isExistSelectedWallet() {
        if (mWalletList.isEmpty()) {
            return false;
        }

        return Flowable.fromIterable(mWalletList)
                .map(new Function<Wallet, Boolean>() {
                    @Override
                    public Boolean apply(Wallet wallet) throws Exception {
                        return wallet.isSelected();
                    }
                })
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) throws Exception {
                        return aBoolean;
                    }
                })
                .firstElement()
                .defaultIfEmpty(false)
                .onErrorReturnItem(false)
                .blockingGet();
    }

    private Single<String> createMnemonic() {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> emitter) throws Exception {
                String mnemonic = generateMnemonic();
                System.out.println("createMnemonic111111111 mnemonic "+ mnemonic);
                if (JZWalletUtil.isValidMnemonic(mnemonic)) {
                    System.out.println("createMnemonic111111111 JZWalletUtil.isValidMnemonic(mnemonic) success ");
                    emitter.onSuccess(mnemonic);
                } else {
                    System.out.println("createMnemonic111111111 JZWalletUtil.isValidMnemonic(mnemonic) onError ");
                    System.out.println(CustomThrowable.CODE_ERROR_CREATE_WALLET_FAILED);
                    System.out.println(new CustomThrowable(CustomThrowable.CODE_ERROR_CREATE_WALLET_FAILED));

                    emitter.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_CREATE_WALLET_FAILED));
                }
            }
        });
    }

    public Single<Wallet> createWallet(final String name, final String password) {
        return createMnemonic()
                .flatMap(new Function<String, SingleSource<? extends Wallet>>() {
                    @Override
                    public SingleSource<? extends Wallet> apply(String mnemonic) throws Exception {
                        return importMnemonic(mnemonic, name, password);
                    }
                });
    }

    public Single<Wallet> importKeystore(String store, String name, String password, long nodeId) {
        return Single.create(new SingleOnSubscribe<Wallet>() {
            @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
            @Override
            public void subscribe(SingleEmitter<Wallet> emitter) throws Exception {
                if (!JZWalletUtil.isValidKeystore(store)) {
                    emitter.onError(new CustomThrowable(CODE_ERROR_KEYSTORE));

                }
                if (TextUtils.isEmpty(name)) {
                    emitter.onError(new CustomThrowable(CODE_ERROR_NAME));
                }
                if (TextUtils.isEmpty(password)) {
                    emitter.onError(new CustomThrowable(CODE_ERROR_PASSWORD));
                }
                try {
                    Wallet entity = WalletServiceImpl.getInstance().importKeystore(store, name, password);
                    if (entity == null) {
                        emitter.onError(new CustomThrowable(CODE_ERROR_PASSWORD));
                    }
                    for (Wallet param : mWalletList) {
                        if (param.getAddress().toLowerCase().equalsIgnoreCase(entity.getAddress().toLowerCase())) {
                            emitter.onError(new CustomThrowable(CODE_ERROR_WALLET_EXISTS));
                        }
                    }
                    entity.setBackedUp(true);
                    entity.setMnemonic("");
                    entity.setChainId(NodeManager.getInstance().getChainId());
                    entity.setNodeId(nodeId);
                    entity.setSelected(true);
                    addWallet(entity);
                    WalletDao.insertWalletInfo(entity.buildWalletInfoEntity());
                    setSelectedWalletWithUuid(entity.getUuid());
                    init();
                    //PreferenceTool.putBoolean(Constants.Preference.KEY_OPERATE_MENU_FLAG, false);
                    System.out.println("WalletManager1111 importPrivateKey33333 ");
                    emitter.onSuccess(entity);
                } catch (Exception exp) {
                    emitter.onError(new CustomThrowable(CODE_ERROR_UNKNOW));
                }
            }
        });
    }

    public int importWalletAddress(String walletAddress) {
        if (!JZWalletUtil.isValidAddress(walletAddress)) {
            return CODE_ERROR_INVALIA_ADDRESS;
        }
        Wallet mWallet = new Wallet();
        mWallet.setAddress(walletAddress);
        mWallet.setUuid(UUID.randomUUID().toString());
        mWallet.setAvatar(WalletServiceImpl.getInstance().getWalletAvatar());

        for (Wallet param : mWalletList) {
            if (param.getAddress().toLowerCase().equalsIgnoreCase(mWallet.getAddress().toLowerCase())) {
                return CODE_ERROR_WALLET_EXISTS;
            }
        }
        mWallet.setBackedUp(true);
        mWallet.setChainId(NodeManager.getInstance().getChainId());
        mWallet.setCreateTime(System.currentTimeMillis());
        mWallet.setName(String.format("%s%d", "Wallet", PreferenceTool.getInt(NodeManager.getInstance().getChainId(), 1)));
        addWallet(mWallet);
        WalletDao.insertWalletInfo(mWallet.buildWalletInfoEntity());

        PreferenceTool.putBoolean(Constants.Preference.KEY_OPERATE_MENU_FLAG, false);
        return CODE_OK;
    }


    public Single<Wallet> importPrivateKey(final String privateKey, final String name, final String password, final long nodeId) {
        System.out.println("WalletManager1111 importPrivateKey ");
        return Single.create(new SingleOnSubscribe<Wallet>() {
            @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
            @Override
            public void subscribe(SingleEmitter<Wallet> emitter) throws Exception {

                System.out.println("WalletManager1111 importPrivateKey111111 ");
                if (!JZWalletUtil.isValidPrivateKey(privateKey)) {
                    System.out.println("WalletManager1111 onError1111111 ");
                    emitter.onError(new CustomThrowable(CODE_ERROR_PRIVATEKEY));
                }
                if (TextUtils.isEmpty(name)) {
                    System.out.println("WalletManager1111 onError2222222 ");
                    emitter.onError(new CustomThrowable(CODE_ERROR_NAME));
                }
                if (TextUtils.isEmpty(password)) {
                    System.out.println("WalletManager1111 onError3333333 ");
                    emitter.onError(new CustomThrowable(CODE_ERROR_PASSWORD));
                }
                System.out.println("WalletManager1111 importPrivateKey2222222 ");
                try {
                    Wallet entity = WalletServiceImpl.getInstance().importPrivateKey(privateKey, name, password);

                    if (entity == null) {
                        System.out.println("WalletManager1111 onError4444444 ");
                        emitter.onError(new CustomThrowable(CODE_ERROR_PASSWORD));
                    }

                    System.out.println("WalletManager1111 entity =  "+entity.getAddress());
                    for (Wallet param : mWalletList) {
                        if (param.getAddress().toLowerCase().equalsIgnoreCase(entity.getAddress().toLowerCase())) {
                            emitter.onError(new CustomThrowable(CODE_ERROR_WALLET_EXISTS));
                        }
                    }
                    entity.setBackedUp(true);
                    entity.setMnemonic("");
                    entity.setChainId(NodeManager.getInstance().getChainId());
                    entity.setNodeId(nodeId);
                    entity.setSelected(true);
                    addWallet(entity);
                    WalletDao.insertWalletInfo(entity.buildWalletInfoEntity());
                    setSelectedWalletWithUuid(entity.getUuid());
                    init();
                    //PreferenceTool.putBoolean(Constants.Preference.KEY_OPERATE_MENU_FLAG, false);
                    System.out.println("WalletManager1111 importPrivateKey33333 ");
                    emitter.onSuccess(entity);
                } catch (Exception exp) {
                    System.out.println("WalletManager1111 exp =  "+exp.getMessage());
                    emitter.onError(new CustomThrowable(CODE_ERROR_UNKNOW));
                }
            }
        });
    }

    private Single<Wallet> importMnemonic(final String mnemonic, final String name, final String password) {
        return Single.create(new SingleOnSubscribe<Wallet>() {
            @Override
            public void subscribe(SingleEmitter<Wallet> emitter) throws Exception {
                Wallet walletEntity = WalletServiceImpl.getInstance().importMnemonic(mnemonic, name, password);
                System.out.println("importMnemonic1111111111");
                if (walletEntity == null || isWalletAddressExists(walletEntity.getAddress().toLowerCase())) {
                    emitter.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_CREATE_WALLET_FAILED));
                } else {
                    walletEntity.setMnemonic(JZWalletUtil.encryptMnemonic(walletEntity.getKey(), mnemonic, password));
                    walletEntity.setChainId(NodeManager.getInstance().getChainId());
                    emitter.onSuccess(walletEntity);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public Single<Wallet>  importMnemonic(String mnemonic, String name, String password, long nodeId) {
        return Single.create(new SingleOnSubscribe<Wallet>() {
            @Override
            public void subscribe(SingleEmitter<Wallet> emitter) throws Exception {
                if (!JZWalletUtil.isValidMnemonic(mnemonic)) {
                    emitter.onError(new CustomThrowable(CODE_ERROR_MNEMONIC));
                }
                if (TextUtils.isEmpty(name)) {
                    emitter.onError(new CustomThrowable(CODE_ERROR_NAME));
                }
                if (TextUtils.isEmpty(password)) {
                    emitter.onError(new CustomThrowable(CODE_ERROR_PASSWORD));
                }
                try {
                    Wallet entity = WalletServiceImpl.getInstance().importMnemonic(mnemonic, name, password);
                    if (entity == null) {
                        emitter.onError(new CustomThrowable(CODE_ERROR_PASSWORD));

                    }
                    for (Wallet param : mWalletList) {
                        if (param.getAddress().toLowerCase().equalsIgnoreCase(entity.getAddress().toLowerCase())) {
                            emitter.onError(new CustomThrowable(CODE_ERROR_WALLET_EXISTS));
                        }
                    }
                    entity.setBackedUp(true);
                    entity.setMnemonic(JZWalletUtil.encryptMnemonic(entity.getKey(), mnemonic, password));
                    entity.setChainId(NodeManager.getInstance().getChainId());

                    entity.setNodeId(nodeId);
                    entity.setSelected(true);
                    addWallet(entity);
                    WalletDao.insertWalletInfo(entity.buildWalletInfoEntity());
                    setSelectedWalletWithUuid(entity.getUuid());
                    init();

                    PreferenceTool.putBoolean(Constants.Preference.KEY_OPERATE_MENU_FLAG, false);
                    emitter.onSuccess(entity);
                } catch (Exception exp) {
                    emitter.onError(new CustomThrowable(CODE_ERROR_UNKNOW));
                }
            }
        });
    }


    public boolean updateWalletName(Wallet wallet, String newName) {
        for (Wallet walletEntity : mWalletList) {
            if (wallet.getUuid().equals(walletEntity.getUuid())) {
                walletEntity.setName(newName);
                return true;
            }
        }
        return false;
    }


    public boolean updateBackedUpWithUuid(String uuid, boolean backedUp) {
        if (TextUtils.isEmpty(uuid)) {
            return false;
        }
        for (Wallet walletEntity : mWalletList) {
            if (uuid.equals(walletEntity.getUuid())) {
                walletEntity.setBackedUp(backedUp);
                break;
            }
        }
        return WalletDao.updateBackedUpWithUuid(uuid, backedUp);
    }

    public void updateWalletBackedUpPromptWithUUID(final String uuid, final boolean backedUpPrompt) {

        if (TextUtils.isEmpty(uuid)) {
            return;
        }

        if (mWalletList.isEmpty()) {
            return;
        }


        Flowable.range(0, mWalletList.size())
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return TextUtils.equals(mWalletList.get(integer).getUuid(), uuid);
                    }
                })
                .firstElement()
                .doOnSuccess(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        mWalletList.get(integer).setBackedUpPrompt(backedUpPrompt);
                    }
                })
                .subscribe();
    }

    public Wallet getWalletByAddress(String address) {
        if (TextUtils.isEmpty(address)) {
            return null;
        }

        for (Wallet walletEntity : mWalletList) {
            if (walletEntity.getAddress().toLowerCase().contains(address.toLowerCase())) {
                return walletEntity;
            }
        }
        return Wallet.getNullInstance();
    }

    public boolean deleteWallet(Wallet wallet) {
        for (Wallet walletEntity : mWalletList) {
            if (wallet.getUuid().equals(walletEntity.getUuid())) {
                mWalletList.remove(walletEntity);
                break;
            }
        }
        if (!isExistSelectedWallet() && !mWalletList.isEmpty()) {
            mWalletList.get(0).setSelected(true);
            EventPublisher.getInstance().sendWalletSelectedChangedEvent();
        }
        return WalletDao.deleteWalletInfo(wallet.getUuid());
    }

    public boolean deleteAsset(Asset asset){
        for (Asset assetEntity : mAssetList) {
            if (asset.getId().equals(assetEntity.getId())) {
                mAssetList.remove(assetEntity);
                break;
            }
        }
        return AssetDao.deleteAssetInfo(asset.getId());
    }
    public boolean isValidWallet(Wallet walletEntity, String password) {
        try {
            return JZWalletUtil.decrypt(walletEntity.getKey(), password) != null;
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
            return false;
        }
    }

    public boolean isWalletNameExists(final String walletName) {
        if (mWalletList == null || mWalletList.isEmpty()) {
            return false;
        }
        return Flowable
                .fromIterable(mWalletList)
                .map(new Function<Wallet, Boolean>() {
                    @Override
                    public Boolean apply(Wallet walletEntity) throws Exception {
                        return walletEntity.getName().toLowerCase().equals(walletName);
                    }
                })
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) throws Exception {
                        return aBoolean;
                    }
                })
                .firstElement()
                .defaultIfEmpty(false)
                .blockingGet();
    }

    public boolean isWalletAddressExists(final String prefixAddress) {
        if (mWalletList == null || mWalletList.isEmpty()) {
            return false;
        }
        return Flowable
                .fromIterable(mWalletList)
                .map(new Function<Wallet, Boolean>() {
                    @Override
                    public Boolean apply(Wallet walletEntity) throws Exception {
                        return walletEntity.getAddress().toLowerCase().equalsIgnoreCase(prefixAddress.toLowerCase());
                    }
                })
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) throws Exception {
                        return aBoolean;
                    }
                })
                .firstElement()
                .defaultIfEmpty(false)
                .blockingGet();
    }

    public Observable<BigDecimal> getAccountBalance(int t) {
        return Observable
                .interval(0, 5, TimeUnit.SECONDS)
                .flatMap(new Function<Long, ObservableSource<BigDecimal>>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public ObservableSource<BigDecimal> apply(Long aLong) throws Exception {
                        List<String> walletAddress =new ArrayList<>();
                        walletAddress.add(currentWallet.getAddress());
                        return ServerUtils
                                .getCommonApi()
                                .getAccountBalance(ApiRequestBody.newBuilder()
                                        .put("addrs", walletAddress)
                                        .build())
                                .toFlowable()
                                .flatMap(new Function<Response<ApiResponse<List<AccountBalance>>>, Publisher<AccountBalance>>() {
                                    @Override
                                    public Publisher<AccountBalance> apply(Response<ApiResponse<List<AccountBalance>>> apiResponseResponse) throws Exception {
                                        System.out.println("WalletManager getAccountBalance1111 11111111111111111");
                                        if (apiResponseResponse != null && apiResponseResponse.isSuccessful() && apiResponseResponse.body().getResult() == ApiErrorCode.SUCCESS) {
                                            return Flowable.fromIterable(apiResponseResponse.body().getData());
                                        }
                                        return Flowable.error(new Throwable());
                                    }
                                })
                                .map(new Function<AccountBalance, AccountBalance>() {
                                    @Override
                                    public AccountBalance apply(AccountBalance accountBalance) throws Exception {
                                        System.out.println("WalletManager getAccountBalance1111 2222222222222222222");

                                        //保留小数点后8位，截断
                                        accountBalance.setFree(AmountUtil.getPrettyBalance(accountBalance.getFree(), 8));
                                        accountBalance.setLock(AmountUtil.getPrettyBalance(accountBalance.getLock(), 8));
                                        return accountBalance;
                                    }
                                })
                                .doOnNext(new Consumer<AccountBalance>() {
                                    @Override
                                    public void accept(AccountBalance accountBalance) throws Exception {
                                        System.out.println("WalletManager getAccountBalance1111 333333333333333");

                                        WalletManager.getInstance().updateAccountBalance(accountBalance);
                                    }
                                })
                                .map(new Function<AccountBalance, BigDecimal>() {
                                    @Override
                                    public BigDecimal apply(AccountBalance accountBalance) throws Exception {
                                        System.out.println("WalletManager getAccountBalance1111 444444444444");

                                        return new BigDecimal(accountBalance.getFree());
                                    }
                                })
                                .reduce(new BiFunction<BigDecimal, BigDecimal, BigDecimal>() {
                                    @Override
                                    public BigDecimal apply(BigDecimal balance1, BigDecimal banalce2) throws Exception {
                                        System.out.println("WalletManager getAccountBalance1111 55555555555");

                                        return balance1.add(banalce2);
                                    }
                                })
                                .defaultIfEmpty(BigDecimal.ZERO)
                                .onErrorReturnItem(BigDecimal.ZERO)
                                .doOnSuccess(new Consumer<BigDecimal>() {
                                    @Override
                                    public void accept(BigDecimal sumAccountBalance) throws Exception {
                                        System.out.println("WalletManager getAccountBalance1111 666666666666");

                                        if (!sumAccountBalance.equals(mSumAccountBalance)) {
                                            EventBus.getDefault().post(new Event.SumAccountBalanceChanged());
                                        }
                                        setSumAccountBalance(sumAccountBalance);
                                    }
                                })
                                .toObservable();
                    }
                });

    }
    @SuppressLint("CheckResult")
    public void refreshContractAssetBalance(){
         Flowable.range(0, mAssetList.size())
            .filter(new Predicate<Integer>() {
                @SuppressLint("CheckResult")
                @Override
                public boolean test(Integer integer) throws Exception {
                    return mAssetList.get(integer).getAssetType() == ASSET_CONTRACT;
                }
            })

            .subscribe(new Consumer<Integer>() {
                @SuppressLint("CheckResult")
                @Override
                public void accept(Integer integer) throws Exception {
                    Asset asset = mAssetList.get(integer);
                    System.out.println("integer222222 = " + integer);
                    Web3j mWeb3j = Web3j.build(new HttpService(URL_PLATON_TESTNET_RPC));
                    Wallet walletEntity = WalletManager.getInstance().getCurrentWallet();
                    final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function("balanceOf",
                            Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(currentWallet.getAddress())),
                            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
                    String encodedFunction = FunctionEncoder.encode(function);

                    PlatonCall ethCall = null;
                    try {
                        ethCall = (PlatonCall) mWeb3j.platonCall(Transaction.createEthCallTransaction(walletEntity.getAddress(), asset.getContractAddress(), encodedFunction), DefaultBlockParameterName.LATEST).send();
                        System.out.println("WalletManager 111111111 ethCall= " + ethCall);
                    } catch (IOException e) {
                        System.out.println("WalletManager 111111111 e= " + e);
                        e.printStackTrace();
                    }
                    if (ethCall != null && ethCall.getValue() != null) {
                        List<Type> values = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters(), Long.parseLong(NodeManager.getInstance().getChainId()));
                        System.out.println("WalletManager 111111111 values= " + values);
                        if (!values.isEmpty()) {
                            System.out.println("WalletManager ethCall. result =" + values.get(0).toString());
                            String balance = values.get(0).getValue().toString();
                            asset.setBalance(balance);
                            AssetDao.updateAssetBalance(asset.getId(),balance);
                        }
                    }
                }

            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    throwable.printStackTrace();
                }
            });
    }

    public int getPositionByAddress(final String address) {
        return Flowable
                .range(0, mWalletList.size())
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return mWalletList.get(integer).getAddress().equalsIgnoreCase(address);
                    }
                })
                .firstElement()
                .defaultIfEmpty(-1)
                .onErrorReturnItem(-1)
                .blockingGet();
    }

    /**
     * 获取所有钱包的总计
     *
     * @return
     */
    public Observable<BigDecimal> getTotal() {
        if (mWalletList == null || mWalletList.isEmpty()) {
            return Observable.just(BigDecimal.ZERO);
        }
        return Flowable.fromIterable(mWalletList)
                .map(new Function<Wallet, AccountBalance>() {
                    @Override
                    public AccountBalance apply(Wallet wallet) throws Exception {
                        return wallet.getAccountBalance();
                    }
                }).map(new Function<AccountBalance, BigDecimal>() {

                    @Override
                    public BigDecimal apply(AccountBalance accountBalance) throws Exception {
                        return new BigDecimal(accountBalance.getFree());
                    }
                }).reduce(new BiFunction<BigDecimal, BigDecimal, BigDecimal>() {
                    @Override
                    public BigDecimal apply(BigDecimal balance1, BigDecimal balance2) throws Exception {
                        return balance1.add(balance2);
                    }
                }).toObservable();
    }


    /**
     * 一级排序按照可用余额从大到小排序，二级排序按照钱包创建时间从旧到新排序
     * 默认选中的钱包：按照钱包列表排序规则第一位的钱包
     *
     * @return
     */
    public Wallet getFirstSortedWallet() {
        if (mWalletList.isEmpty()) {
            return Wallet.getNullInstance();
        }

        Collections.sort(mWalletList, new BalanceComparator());

        Wallet wallet = getWalletByBalanceBiggerThanZero();

        if (wallet.isNull()) {
            Collections.sort(mWalletList, new CreateTimeComparator());
            wallet = mWalletList.get(0);
        }
        return wallet;
    }

    private Wallet getWalletByBalanceBiggerThanZero() {
        return Flowable
                .fromIterable(mWalletList)
                .filter(new Predicate<Wallet>() {
                    @Override
                    public boolean test(Wallet wallet) throws Exception {
                        return BigDecimalUtil.isBiggerThanZero(wallet.getFreeBalance());
                    }
                })
                .defaultIfEmpty(Wallet.getNullInstance())
                .blockingFirst();
    }

    static class BalanceComparator implements Comparator<Wallet> {

        @Override
        public int compare(Wallet o1, Wallet o2) {
            return BigDecimalUtil.compareTo(o2.getFreeBalance(), o1.getFreeBalance());
        }
    }

    static class CreateTimeComparator implements Comparator<Wallet> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public int compare(Wallet o1, Wallet o2) {
            return Long.compare(o1.getCreateTime(), o2.getCreateTime());
        }
    }



    ////////lato
    public String exportPrivateKeyByPassword(String password, Wallet wallet) throws Exception {
        Credentials  credentials = JZWalletUtil.getCredentials(password, wallet.getKey());
        String privateKey = Numeric.toHexStringNoPrefixZeroPadded(credentials.getEcKeyPair().getPrivateKey(), Keys.PRIVATE_KEY_LENGTH_IN_HEX);
        return privateKey;
    }
    public ECKeyPair exportECKeyPairByPassword(String password, Wallet wallet) throws Exception {
        Credentials  credentials = JZWalletUtil.getCredentials(password, wallet.getKey());
       return credentials.getEcKeyPair();
    }
    public boolean checkPassword(String password, Wallet wallet){
        try{
            Credentials  credentials = JZWalletUtil.getCredentials(password, wallet.getKey());
            return credentials != null;
        } catch (CipherException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean changePassword(Wallet wallet, String oldPassword, String newPassword){
        String mnemonic_string = JZWalletUtil.decryptMnenonic(wallet.getKey(), wallet.getMnemonic(), oldPassword);
        try {
            ECKeyPair ecKeyPair = JZWalletUtil.decrypt(wallet.getKey(), oldPassword);
            Wallet newWallet = WalletServiceImpl.getInstance().changePassword(ecKeyPair, newPassword, wallet, mnemonic_string);
           boolean rs =  WalletDao.updateWallet(newWallet);
            System.out.println("WalletManager changePassword rs = "+rs);
           return rs;

        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        }
        return false;
    }
}
