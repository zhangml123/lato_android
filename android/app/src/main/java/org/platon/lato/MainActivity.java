package org.platon.lato;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugins.GeneratedPluginRegistrant;
import io.flutter.plugin.common.MethodChannel;
import io.reactivex.Flowable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platon.rlp.datatypes.Uint8;
import com.platon.sdk.contracts.ppos.dto.enums.StakingAmountType;
import com.platon.sdk.utlis.Bech32;
import com.platon.sdk.utlis.NetworkParameters;
import com.platon.wallet.FunctionType;
import com.platon.wallet.app.LoadingTransformer;
import com.platon.wallet.db.entity.AssetEntity;
import com.platon.wallet.db.entity.MessageEntity;
import com.platon.wallet.db.entity.NodeEntity;
import com.platon.wallet.db.entity.TransactionEntity;
import com.platon.wallet.db.entity.TransactionRecordEntity;
import com.platon.wallet.db.entity.WalletEntity;
import com.platon.wallet.db.sqlite.AssetDao;
import com.platon.wallet.db.sqlite.MessageDao;
import com.platon.wallet.db.sqlite.NodeDao;
import com.platon.wallet.db.sqlite.TransactionDao;
import com.platon.wallet.db.sqlite.WalletDao;
import com.platon.wallet.engine.DelegateManager;
import com.platon.wallet.engine.NodeManager;
import com.platon.wallet.engine.ServerUtils;
import com.platon.wallet.engine.TransactionManager;
import com.platon.wallet.engine.WalletManager;
import com.platon.wallet.entity.Asset;
import com.platon.wallet.entity.DelegateInfo;
import com.platon.wallet.entity.DelegateItemInfo;
import com.platon.wallet.entity.DelegateNodeDetail;
import com.platon.wallet.entity.DelegationValue;
import com.platon.wallet.entity.Message;
import com.platon.wallet.entity.Node;
import com.platon.wallet.entity.Transaction;
import com.platon.wallet.entity.TransactionStatus;
import com.platon.wallet.entity.TransactionType;
import com.platon.wallet.entity.VerifyNode;
import com.platon.wallet.entity.VerifyNodeDetail;
import com.platon.wallet.entity.Wallet;
import com.platon.wallet.network.ApiRequestBody;
import com.platon.wallet.network.ApiResponse;
import com.platon.wallet.network.ApiSingleObserver;
import com.platon.wallet.utils.BigIntegerUtil;
import com.platon.wallet.utils.JZWalletUtil;
import com.platon.wallet.utils.NumberParserUtils;
import com.platon.wallet.utils.RxUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.platon.lato.permissions.Permissions;
import org.platon.lato.service.websocket.WebSocketManager;
import org.platon.lato.util.Hex;
import org.platon.lato.util.RxSchedulerUtils;
import org.reactivestreams.Publisher;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.PlatOnTypeDecoder;
import org.web3j.abi.TypeDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.Utils;
import org.web3j.abi.WasmEventDecoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.StaticArray;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.PlatonCall;
import org.web3j.protocol.core.methods.response.PlatonGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.PlatonLog;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import com.platon.wallet.entity.GasProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;

import static com.platon.wallet.BuildConfig.URL_ALAYA_RPC;
import static com.platon.wallet.BuildConfig.URL_PLATON_TESTNET_RPC;
import static com.platon.wallet.entity.Asset.ASSET_CONTRACT;
import static com.platon.wallet.entity.Asset.ASSET_LAT;
import static com.platon.wallet.utils.RxUtils.getSingleSchedulerTransformer;
import static java.math.BigDecimal.ROUND_DOWN;
import static java.math.BigDecimal.ROUND_HALF_UP;

public class MainActivity extends FlutterActivity {
    private static String TAG = "MainActivity";
    private static String NOPLAY_UPDATE_URL = "http://199.247.27.165:1003";
    private static final String CHANNEL = "samples.flutter.dev";
    //与Flutter Client端写一致的方法
    private int t = 1;
    private static final int REQUEST_ENABLE_BT = 1;
    private BasicMessageChannel<Object> mMessageChannel;
    private Disposable mFetchWalletBalanceDisposable;
    private BigDecimal gasLimit;
    private BigDecimal gasPrice;
    private BigInteger nonce;
    private String fee;
    private Wallet currentWallet;
    private String direction;
    private String versionJson;
    private String uri;
    private String versionName;
    @SuppressLint("StaticFieldLeak")
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SplashScreen.show(this, true);
        messageChannelFunction();
        mFetchWalletBalanceDisposable = new CompositeDisposable();
        currentWallet = WalletManager.getInstance().getCurrentWallet();
        direction = "old";
        if(currentWallet != null){
            List<Asset> assetList1 = getAssetListFromDb();
            for(Asset asset :assetList1){
                System.out.println("MainActivity  onCreate assetList1。size = "+asset.isHome());
            }
            System.out.println("MainActivity  onCreate assetList1。size = "+assetList1.size());
            WalletManager.getInstance().setAssetList(assetList1);
            System.out.println("MainActivity  refreshBalance 111= ");
            refreshBalance();
            webSocketInit();

        }
    }

    private void refreshContractAssetBalance() {
         List<Asset> assetList = getAssetListFromDb();
         for(Asset asset :assetList){
             if(asset.getAssetType() == ASSET_CONTRACT){
                 Web3j mWeb3j = Web3j.build(new HttpService(URL_PLATON_TESTNET_RPC));
                 Wallet walletEntity = WalletManager.getInstance().getCurrentWallet();
                 final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function("balanceOf",
                         Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(currentWallet.getAddress())),
                         Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
                 String encodedFunction = FunctionEncoder.encode(function);

                 PlatonCall ethCall = null;
                 try {
                     ethCall = (PlatonCall) mWeb3j.platonCall(org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(walletEntity.getAddress(), asset.getContractAddress(), encodedFunction), DefaultBlockParameterName.LATEST).send();
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
         }

    }

    private void messageChannelFunction() {
        mMessageChannel = new BasicMessageChannel<Object>(getFlutterEngine().getDartExecutor().getBinaryMessenger(), "BasicMessageChannelPlugin", StandardMessageCodec.INSTANCE);
        mMessageChannel.setMessageHandler(new BasicMessageChannel.MessageHandler<Object>() {
            @SuppressLint({"StaticFieldLeak", "CheckResult"})
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onMessage(Object o, BasicMessageChannel.Reply<Object> reply) {
                Map<Object, Object> arguments = (Map<Object, Object>) o;
                String lMethod = (String) arguments.get("method");
                assert lMethod != null;

                if(lMethod.equals("get_version")) {
                    PackageManager packageManager = getPackageManager();
                    PackageInfo packageInfo    = null;
                    try {
                        packageInfo = packageManager.getPackageInfo(getPackageName(), 0);

                        reply.reply("{\"method\":\"get_version\",\"version\":\""+packageInfo.versionName+"\"}");

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }else if(lMethod.equals("check_update")) {
                    checkUpdate();
                    reply.reply("{\"method\":\"check_update\",\"status\":\"success\"}");

                }else if(lMethod.equals("update_app")) {
                    System.out.println("update1111");
                    update();
                    reply.reply("{\"method\":\"update_app\",\"status\":\"success\"}");

                }else if(lMethod.equals("cancel_update")) {
                    System.out.println("update1111");
                    SharedPreferences update = getSharedPreferences("UPDATE", MODE_PRIVATE);
                    SharedPreferences.Editor editor = update.edit();
                    editor.putBoolean("update", false);
                    editor.apply();
                    reply.reply("{\"method\":\"cancel_update\",\"status\":\"success\"}");

                }else if(lMethod.equals("init")) {
                    currentWallet = WalletManager.getInstance().getCurrentWallet();
                    //List<Asset> assetList1 = getAssetList();
                   // WalletManager.getInstance().setAssetList(assetList1);
                    reply.reply("{\"method\":\"init\",\"status\":\"success\"}");
                }else if(lMethod.equals("get_node_list")) {
                    List<Node>  nodeList =  NodeManager.getInstance().getNodeList().blockingGet();
                    String node_list = JSON.toJSONString(nodeList);
                    reply.reply("{\"method\":\"get_node_list\",\"node_list\":"+node_list+"}");
                }else if (lMethod.equals("get_wallet_list")) {
                    System.out.println("onMessage  get_wallet_list = "+arguments.get("nodeId"));
                    int nodeId = (int) arguments.get("nodeId");
                    System.out.println("onMessage  nodeId = "+nodeId);
                    List<NodeEntity> nodelist = NodeDao.getNodeList();
                    System.out.println("onMessage  nodelist = "+nodelist.size());
                    for(NodeEntity node :nodelist){

                        System.out.println("onMessage  node,.id = "+node.getId());
                    }
                    List<Wallet> walletList = WalletManager.getInstance().getWalletListFromNodeId(nodeId).blockingGet();
                    System.out.println("onMessage  walletList = "+walletList.size());
                    List<WalletEntity> walletList1 = WalletDao.getWalletInfoList();
                    System.out.println("onMessage  walletList1 = " + walletList1.size());
                    List<TransactionEntity> transactions = TransactionDao.getTransactionList();
                    for(TransactionEntity transaction :transactions){

                        System.out.println("onMessage  transaction,.id = "+transaction.getAssetId());
                    }

                    String wallet_ist = JSON.toJSONString(walletList);
                    reply.reply("{\"method\":\"get_wallet_list\",\"wallet_list\":"+wallet_ist+"}");
                }else if(lMethod.equals("check_wallet_name")){
                    String name = (String) arguments.get("name");
                    WalletEntity wallet = WalletDao.getWalletInfoFromName(name);
                    if(wallet != null){
                        reply.reply("{\"method\":\"check_wallet_name\",\"rs\":\"1\"}");
                    }else{
                        reply.reply("{\"method\":\"check_wallet_name\",\"rs\":\"0\"}");
                    }
                }
                else if (lMethod.equals("wallet_create")) {
                    System.out.println("wallet_create  wallet_create ");
                    String name = (String) arguments.get("name");
                    long nodeId = (int) arguments.get("nodeId");
                    long currentNodeId  = NodeManager.getInstance().getCheckedNode().blockingGet().getId();

                    System.out.println("wallet_create  nodeId = "+nodeId);

                    System.out.println("wallet_create  currentNodeId = "+currentNodeId);
                    if(nodeId == 0 ){
                        nodeId = NodeManager.getInstance().getCheckedNode().blockingGet().getId();
                    }
                    if(nodeId != currentNodeId){
                        System.out.println("wallet_create  switchNode ");
                        NodeManager.getInstance().switchNode(nodeId);
                        System.out.println("wallet_create  nodeId = "+nodeId);
                        System.out.println("wallet_create  currentNodeId11111 = "+ NodeManager.getInstance().getCheckedNode().blockingGet().getId());
                    }
                    String password = (String) arguments.get("password");
                    createWallet(name, password, nodeId);

                    reply.reply("android connect success");
                }else if(lMethod.equals("import_wallet")){
                    String type = (String) arguments.get("type");
                    Wallet wallet = null;
                    if(type.equals("privateKey")){
                        String privateKey = (String) arguments.get("privateKey");
                        String name = (String) arguments.get("name");
                        String password = (String) arguments.get("password");
                        long nodeId = (int) arguments.get("nodeId");
                        long currentNodeId  = NodeManager.getInstance().getCheckedNode().blockingGet().getId();
                        if(nodeId == 0 ){
                            nodeId = NodeManager.getInstance().getCheckedNode().blockingGet().getId();
                        }
                        if(nodeId != currentNodeId){
                            NodeManager.getInstance().switchNode(nodeId);
                        }
                       wallet =  WalletManager.getInstance().importPrivateKey(privateKey, name, password, nodeId).blockingGet();

                    }else if(type.equals("mnemonic")){
                        String mnemonic = (String) arguments.get("mnemonic");
                        String name = (String) arguments.get("name");
                        String password = (String) arguments.get("password");
                        long nodeId = (int) arguments.get("nodeId");
                        long currentNodeId  = NodeManager.getInstance().getCheckedNode().blockingGet().getId();
                        if(nodeId == 0 ){
                            nodeId = NodeManager.getInstance().getCheckedNode().blockingGet().getId();
                        }
                        if(nodeId != currentNodeId){
                            NodeManager.getInstance().switchNode(nodeId);
                        }
                        wallet  = WalletManager.getInstance().importMnemonic(mnemonic, name, password, nodeId).blockingGet();

                    }else if(type.equals("keystore")){
                        String keystore = (String) arguments.get("keystore");
                        String name = (String) arguments.get("name");
                        String password = (String) arguments.get("password");
                        long nodeId = (int) arguments.get("nodeId");
                        long currentNodeId  = NodeManager.getInstance().getCheckedNode().blockingGet().getId();
                        if(nodeId == 0 ){
                            nodeId = NodeManager.getInstance().getCheckedNode().blockingGet().getId();
                        }
                        if(nodeId != currentNodeId){
                            NodeManager.getInstance().switchNode(nodeId);
                        }
                        wallet  = WalletManager.getInstance().importKeystore(keystore, name, password, nodeId).blockingGet();

                    }
                    if(wallet != null){
                        long time = System.currentTimeMillis();
                        Asset assetEntity = new AssetEntity.Builder()
                                .id(UUID.randomUUID().toString())
                                .walletId(wallet.getUuid())
                                .assetType(ASSET_LAT)
                                .createTime(time)
                                .updateTime(time)
                                .contractAddress("")
                                .binary("")
                                .name("")
                                .symbol("LAT")
                                .setHome(true)
                                .build().buildAsset();
                        WalletManager.getInstance().addAsset(assetEntity);
                        AssetDao.insertAssetInfo(assetEntity.buildAssetInfoEntity());
                        currentWallet = WalletManager.getInstance().getCurrentWallet();
                        if(currentWallet != null){
                            List<Asset> assetList1 = getAssetListFromDb();
                            System.out.println("MainActivity  onCreate assetList1。size = "+assetList1.size());
                            WalletManager.getInstance().setAssetList(assetList1);
                            refreshBalance();
                            webSocketInit();
                        }
                        reply.reply("{\"method\":\"import_wallet\",\"status\":\"success\"}");

                    }
                }else if(lMethod.equals("get_wallet_address")){
                    System.out.println("onMessage  get_wallet_address111");
                    String address = "";
                    String name = "";
                    if(currentWallet != null){
                         address = currentWallet.getAddress();
                         name = currentWallet.getName();
                    }
                    String hex =  Bech32.addressDecodeHex("lat1rz52y79rkxlq2ewnxagqy68pwvu3m287tx04g9");

                    System.out.println("onMessage  lat1rz52y79rkxlq2ewnxagqy68pwvu3m287tx04g9 hex = "+hex);
                    System.out.println("onMessage  get_wallet_address111 name = "+name);
                    reply.reply("{\"method\":\"get_wallet_address\",\"address\":\""+address+"\",\"name\":\""+name+"\"}");
                }else if(lMethod.equals("get_wallet_hex_address")){
                    System.out.println("onMessage  get_wallet_address111");
                    String address = "";
                    String name = "";
                    if(currentWallet != null){
                        address = Bech32.addressDecodeHex(currentWallet.getAddress());;
                        name = currentWallet.getName();
                    }
                    System.out.println("onMessage  get_wallet_address111 name = "+name);
                    reply.reply("{\"method\":\"get_wallet_hex_address\",\"address\":\""+address+"\",\"name\":\""+name+"\"}");
                }else if(lMethod.equals("get_current_node")){
                    Node  currentNode = NodeManager.getInstance().getCheckedNode().blockingGet();
                    reply.reply("{\"method\":\"get_current_node\",\"nodeId\":"+currentNode.getId()+",\"nodeName\":\""+currentNode.getNodeName()+"\"}");

                }else if(lMethod.equals("get_asset_list")){
                    System.out.println("onMessage  get_asset_list");
                    JSONArray _assetList = getAssetList();
                    String assets = JSON.toJSONString(_assetList);

                    reply.reply("{\"method\":\"get_asset_list\",\"asset_list\":"+assets+"}");

                }else if(lMethod.equals("get_asset_balance")){
                    String assetId = (String) arguments.get("assetId");
                    System.out.println("onMessage assetId="+assetId);
                    AssetEntity assetEntity = AssetDao.getAssetById(assetId);
                    System.out.println("onMessage assetEntity="+assetEntity);
                    if(assetEntity !=null){
                        BigDecimal a = new BigDecimal("1000000000000000000");
                        System.out.println("onMessage assetEntity.getBalance() ="+assetEntity.getBalance());
                        String _balance = "0";
                        if(assetEntity.getBalance()!=null){
                            _balance = assetEntity.getBalance();
                         }
                        BigDecimal balance = new BigDecimal(_balance);
                        String balance1 = NumberParserUtils.getPrettyNumber(balance.divide(a,2,ROUND_DOWN ).toPlainString(), 8);
                        Asset asset = new Asset.Builder()
                                .assetType(assetEntity.getAssetType())
                                .id(assetEntity.getId())
                                .name(assetEntity.getName())
                                .symbol(assetEntity.getSymbol())
                                .balance(balance1)
                                .build();
                        String asset1 = JSON.toJSONString(asset);
                        reply.reply("{\"method\":\"get_asset_balance\",\"asset\":"+asset1+"}");
                    }
                }else if(lMethod.equals("get_transaction_list")){
                    System.out.println("onMessage  get_transaction_list");
                    int pageNum = (int) arguments.get("pageNum");
                    String assetId = (String) arguments.get("assetId");
                    getTransactionList(pageNum, assetId);
                    reply.reply("{\"method\":\"get_transaction_list\",\"status\":\"success\"}");

                }else if(lMethod.equals("get_estimate_gas")){
                    System.out.println("onMessage  get_estimate_gas");
                    new AsyncTask<Void, Void, String>() {
                        @SuppressLint("StaticFieldLeak")
                        @Override
                        protected String doInBackground(Void... voids) {
                            getEstimateGas(currentWallet);
                            return "";
                        }
                        @Override
                        protected void onPostExecute(@NonNull String count) {
                         mMessageChannel.send("{\"method\":\"get_estimate_gas\",\"gasLimit\":\""+gasLimit+"\",\"gasPrice\":\""+gasPrice+"\",\"nonce\":\""+nonce+"\",\"fee\":\""+fee+"\"}", new BasicMessageChannel.Reply<Object>() {
                            @Override
                            public void reply(Object o) {
                                Log.d("mMessageChannel", "mMessageChannel get_transaction_list 回调 " + o);
                            }
                        });
                        }
                    }.execute();

                    reply.reply("{\"method\":\"get_estimate_gas\",\"status\":\"success\"}");

                }else if(lMethod.equals("send_transfer_transaction")){
                    String from = (String) arguments.get("from");
                    String to = (String) arguments.get("to");
                    String password = (String) arguments.get("password");
                    String value = (String) arguments.get("value");
                    String fee = (String) arguments.get("fee");
                    String gasPrice = (String) arguments.get("gasPrice");
                    String gasLimit = (String) arguments.get("gasLimit");
                    String nonce = (String) arguments.get("nonce");
                    String remark = (String) arguments.get("remark");
                    String assetId = (String) arguments.get("assetId");
                    int assetType = (int) arguments.get("assetType");
                    if(assetType == ASSET_LAT){
                        sendTransferTransaction(from, to, value, fee, password, gasPrice, gasLimit, nonce, remark);

                    }else if(assetType == ASSET_CONTRACT){
                        sendContractTransaction(from, to, value, gasPrice, gasLimit, password, nonce, remark, assetId);
                    }

                }else if(lMethod.equals("send_contract_transaction")){
                    String gasPrice = (String) arguments.get("gasPrice");
                    String gasLimit = (String) arguments.get("gasLimit");
                    String encodeData = (String) arguments.get("encodeData");
                    String password = (String) arguments.get("password");
                    sendDeployContractTransaction(password, encodeData, gasPrice, gasLimit);
                }else if(lMethod.equals("check_password")){
                    String password = (String) arguments.get("password");
                    String walletId = (String) arguments.get("walletId");

                    try {
                        WalletEntity walletEntity;
                        if(walletId != null) {
                            walletEntity= WalletDao.getWalletInfoFromUuid(walletId);
                        }else{
                            walletEntity = currentWallet.buildWalletInfoEntity();
                        }

                        Credentials credentials = JZWalletUtil.getCredentials(password, walletEntity.getKeyJson());
                         System.out.println("wallet test  credentials  =" + credentials);
                        if(credentials!=null){
                            reply.reply("{\"method\":\"check_password\",\"status\":\"success\"}");
                        }else{
                            reply.reply("{\"method\":\"check_password\",\"status\":\"error\"}");
                        }

                    } catch (CipherException | IOException e) {
                        reply.reply("{\"method\":\"check_password\",\"status\":\"error\"}");
                        e.printStackTrace();
                    }
                }else if(lMethod.equals("get_private_key")){
                    String password = (String) arguments.get("password");

                    String walletId = (String) arguments.get("walletId");
                    try {
                        WalletEntity walletEntity = WalletDao.getWalletInfoFromUuid(walletId);
                        Credentials credentials = JZWalletUtil.getCredentials(password, walletEntity.getKeyJson());

                        System.out.println("wallet test  credentials  =" + credentials);
                        if(credentials!=null){
                            String privateKey = Numeric.toHexStringNoPrefixZeroPadded(credentials.getEcKeyPair().getPrivateKey(), Keys.PRIVATE_KEY_LENGTH_IN_HEX);
                            reply.reply("{\"method\":\"get_private_key\",\"status\":\"success\",\"private_key\":\""+privateKey+"\"}");
                        }else{
                            reply.reply("{\"method\":\"get_private_key\",\"status\":\"error\"}");
                        }

                    } catch (CipherException | IOException e) {
                        reply.reply("{\"method\":\"check_password\",\"status\":\"error\"}");
                        e.printStackTrace();
                    }
                }else if(lMethod.equals("get_mnemonic")){
                    String password = (String) arguments.get("password");
                    String walletId = (String) arguments.get("walletId");
                    String mnemonic_string;
                    if(!walletId.equals("")){
                        WalletEntity walletEntity = WalletDao.getWalletInfoFromUuid(walletId);
                        mnemonic_string =  JZWalletUtil.decryptMnenonic(walletEntity.getKeyJson(), walletEntity.getMnemonic(), password);

                    }else{
                        mnemonic_string =  JZWalletUtil.decryptMnenonic(currentWallet.getKey(), currentWallet.getMnemonic(), password);

                    }
                     System.out.println("mnemonic_string = "+mnemonic_string);
                    System.out.println("password = "+password);
                    if(mnemonic_string!=null){
                        reply.reply("{\"method\":\"get_mnemonic\",\"status\":\"success\",\"mnemonic\":\""+mnemonic_string+"\"}");
                    }else{
                        reply.reply("{\"method\":\"get_mnemonic\",\"status\":\"error\"}");
                    }
                }else if(lMethod.equals("get_file")){
                    String walletId = (String) arguments.get("walletId");
                    WalletEntity walletEntity = WalletDao.getWalletInfoFromUuid(walletId);
                    String keystore = walletEntity.getKeyJson();
                    if(keystore!=null){
                        reply.reply("{\"method\":\"get_file\",\"status\":\"success\",\"keystore\":"+keystore+"}");
                    }else{
                        reply.reply("{\"method\":\"get_file\",\"status\":\"error\"}");
                    }

                }else if(lMethod.equals("delete_wallet")){
                    String password = (String) arguments.get("password");
                    String walletId = (String) arguments.get("walletId");
                    WalletEntity walletEntity = WalletDao.getWalletInfoFromUuid(walletId);
                    try {
                        Credentials credentials = JZWalletUtil.getCredentials(password, walletEntity.getKeyJson());
                        System.out.println("wallet test  credentials  =" + credentials);
                        if(credentials!=null){
                            WalletManager.getInstance().deleteWallet(currentWallet);
                            ///////////////////
                            String assetId = getAssetListFromDb().get(0).getId();
                            AssetDao.deleteAssetInfo(assetId);
                            ////////////////////
                            reply.reply("{\"method\":\"delete_wallet\",\"status\":\"success\"}");
                        }else{
                            reply.reply("{\"method\":\"delete_wallet\",\"status\":\"error\"}");
                        }

                    } catch (CipherException | IOException e) {
                        reply.reply("{\"method\":\"delete_wallet\",\"status\":\"error\"}");
                        e.printStackTrace();
                    }



                }else if(lMethod.equals("switch_wallet")){
                    String uuid = (String) arguments.get("uuid");

                    long nodeId = (int) arguments.get("nodeId");
                    long currentNodeId  = NodeManager.getInstance().getCheckedNode().blockingGet().getId();
                    if(nodeId != currentNodeId && nodeId != 0){
                        NodeManager.getInstance().switchNode(nodeId);
                    }
                    WalletManager.getInstance().setSelectedWalletWithUuid(uuid);
                    WalletManager.getInstance().init();
                    currentWallet = WalletManager.getInstance().getCurrentWallet();
                    if(currentWallet != null){
                        List<Asset> assetList1 = getAssetListFromDb();
                        System.out.println("MainActivity  onCreate assetList1。size = "+assetList1.size());
                        WalletManager.getInstance().setAssetList(assetList1);
                        refreshBalance();
                        WebSocketManager.getInstance().reConnect(getContext());
                        webSocketInit();
                    }
                    reply.reply("{\"method\":\"switch_wallet\",\"status\":\"success\"}");
                }else if(lMethod.equals("change_password")){

                    String oldPassword = (String) arguments.get("password");
                    String newPassword = (String) arguments.get("newPassword");
                    String walletId = (String) arguments.get("walletId");
                    System.out.println("change_password  oldPassword = "+oldPassword);

                    System.out.println("change_password  newPassword = "+newPassword);
                    WalletEntity walletEntity = WalletDao.getWalletInfoFromUuid(walletId);
                    boolean rs = WalletManager.getInstance().changePassword(walletEntity.buildWallet() ,oldPassword ,newPassword);
                    reply.reply("{\"method\":\"change_password\",\"status\":"+rs+"}");
                }else if(lMethod.equals("app_create_erc20_token")){
                    Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                    startActivityForResult(intent,1);
                }else if(lMethod.equals("search_asset")){
                    String contractAddress = (String) arguments.get("contractAddress");
                    AssetEntity assetEntity = AssetDao.getAssetFromContractAddress(contractAddress);
                    if(assetEntity != null){
                        reply.reply("{\"method\":\"search_asset\",\"code\":\"2\",\"msg\":\"资产已经添加！\"}");
                    }else{

                        new AsyncTask<Void, Void, Asset>() {
                            @SuppressLint("StaticFieldLeak")
                            @Override
                            protected Asset doInBackground(Void... voids) {
                                return searchAssetFromNode(contractAddress);
                            }
                            @Override
                            protected void onPostExecute(@NonNull Asset asset) {
                                if(asset!=null){
                                    String asset1 = JSON.toJSONString(asset);
                                    reply.reply("{\"method\":\"search_asset\",\"code\":\"1\",\"asset\":"+asset1+"}");
                                }else{
                                    reply.reply("{\"method\":\"search_asset\",\"code\":\"0\",\"msg\":\"资产未找到！\"}");
                                }
                            }
                        }.execute();
                    }
                }else if(lMethod.equals("add_asset")){
                    String contractAddress = (String) arguments.get("contractAddress");
                    String assetId = (String) arguments.get("assetId");
                    String messageId = (String) arguments.get("messageId");
                    String walletId = currentWallet.getUuid();
                    AssetEntity assetEntity = AssetDao.getAssetFromContractAddressAndWalletId(walletId, contractAddress);

                    System.out.println("contractAddress = "+ contractAddress );
                    if(assetEntity != null){
                        reply.reply("{\"method\":\"add_asset\",\"code\":\"2\",\"msg\":\"资产已经添加！\"}");
                    }else{
                    new AsyncTask<Void, Void, Asset>() {
                        @SuppressLint("StaticFieldLeak")
                        @Override
                        protected Asset doInBackground(Void... voids) {
                            return searchAssetFromNode(contractAddress);
                        }
                        @Override
                        protected void onPostExecute(@NonNull Asset asset) {
                            System.out.println("contractAddress 1= "+ contractAddress );
                            if(asset!=null){
                                System.out.println("contractAddress 2= "+ contractAddress );
                                long time = System.currentTimeMillis();
                                Asset assetEntity = new AssetEntity.Builder()
                                        .id(!assetId.equals("") ? assetId : UUID.randomUUID().toString())
                                        .walletId(currentWallet.getUuid())
                                        .assetType(ASSET_CONTRACT)
                                        .createTime(time)
                                        .updateTime(time)
                                        .contractAddress(asset.getContractAddress())
                                        .binary("")
                                        .name(asset.getName())
                                        .symbol(asset.getSymbol())

                                        .setHome(true)
                                        .build().buildAsset();
                                WalletManager.getInstance().addAsset(assetEntity);
                                AssetDao.insertAssetInfo(assetEntity.buildAssetInfoEntity());
                                currentWallet = WalletManager.getInstance().getCurrentWallet();
                                System.out.println("contractAddress3 = "+ contractAddress );
                                if(messageId!=null){
                                   boolean rs =  MessageDao.setReadMessage(messageId);
                                   System.out.println("rs77 = "+rs);

                                    MessageEntity messageEntity = MessageDao.getMessageById(messageId);
                                    Transaction transaction = JSONObject.parseObject(messageEntity.getMsg(),Transaction.class);
                                    transaction.setAssetId(assetId);
                                    TransactionDao.insertTransaction(transaction.buildTransactionEntity());

                                    TransactionManager.getInstance().insertTransaction(transaction);
                                }
                                reply.reply("{\"method\":\"add_asset\",\"code\":\"1\"}");
                            }else{
                                reply.reply("{\"method\":\"add_asset\",\"code\":\"0\"}");
                            }
                            refreshBalance();
                        }
                    }.execute();
                    }

                }else if(lMethod.equals("delete_asset")){
                    String assetId = (String) arguments.get("assetId");
                    AssetDao.deleteAssetInfo(assetId);
                    reply.reply("{\"method\":\"delete_asset\",\"code\":\"1\"}");

                }else if(lMethod.equals("get_message_list")){
                    List<MessageEntity> list =  MessageDao.getMessageList();
                    System.out.println("get_message_list list` = "+list.size());
                    List<MessageEntity> newList1 = new ArrayList();
                    for(MessageEntity messageEntity : list){

                        MessageDao.setReadMessage(messageEntity.getId());
                        Transaction transaction = JSONObject.parseObject(messageEntity.getMsg(),Transaction.class);

                        //AssetEntity assetEntity = AssetDao.getAssetById(transaction.getAssetId());
                        String contractAddress = transaction.getContractAddress();
                        String walletId = currentWallet.getUuid();

                        System.out.println("get_message_list contractAddress = "+ contractAddress);
                        AssetEntity assetEntity = AssetDao.getAssetFromContractAddressAndWalletId(walletId, contractAddress);

                        System.out.println("get_message_list assetEntity = "+ assetEntity);
                        if(assetEntity ==null){
                           // list.remove(messageEntity);
                            newList1.add(messageEntity);
                        }


                        String rAddress  = transaction.getRAddress();
                        System.out.println("get_message_list rAddress = "+ rAddress);
                        System.out.println("get_message_list currentWallet = "+ currentWallet.getAddress());

                        System.out.println("get_message_list list1` = "+list.size());
                        if(!rAddress.equals(currentWallet.getAddress())){
                            newList1.remove(messageEntity);
                        }
                        System.out.println("get_message_list list2` = "+list.size());
                    }

                    Set set = new HashSet();
                    System.out.println("get_message_list list3 1` = "+list.size());
                    List<MessageEntity> newList = new ArrayList();
                    System.out.println("get_message_list list3 2` = "+list.size());
                    for(MessageEntity messageEntity : newList1){
                        Transaction transaction = JSONObject.parseObject(messageEntity.getMsg(),Transaction.class);
                        if (set.add(transaction.getContractAddress()))
                            newList.add(messageEntity);
                    }
                    System.out.println("get_message_list list3 3` = "+list.size());
                    newList1.clear();
                    System.out.println("get_message_list list3 4` = "+list.size());
                    newList1.addAll(newList);
                    System.out.println("get_message_list list3 5` = "+list.size());
                    String _list = JSON.toJSONString(newList1);
                    reply.reply("{\"method\":\"get_message_list\",\"message_list\":"+_list+"}");

                }else if(lMethod.equals("new_message")){
                    List<MessageEntity> list =  MessageDao.getNewMessageList();
                    if(list.size()>0){
                        reply.reply("{\"method\":\"new_message\",\"code\":\"1\"}");
                    }else{
                        reply.reply("{\"method\":\"new_message\",\"code\":\"0\"}");
                    }

                }else if(lMethod.equals("update_message")){
                    /*List<MessageEntity> list =  MessageDao.getNewMessageList();
                    for(MessageEntity entity : list){
                        System.out.println("MessageEntity = "+entity.getId());
                    }*/
                }else if(lMethod.equals("delete_message")){
                    /*List<MessageEntity> list =  MessageDao.getNewMessageList();
                    for(MessageEntity entity : list){
                        System.out.println("MessageEntity = "+entity.getId());
                    }*/
                }else if(lMethod.equals("get_verify_node_list")){
                    System.out.println("get_verify_node_list");
                    getVerifyNodeList();
                    reply.reply("{\"method\":\"get_verify_node_list\",\"code\":\"1\"}");

                }else if(lMethod.equals("get_verify_node_detail")){
                    String nodeId = (String) arguments.get("nodeId");
                    getNodeCandidateDetail(nodeId);
                    reply.reply("{\"method\":\"get_verify_node_detail\",\"code\":\"1\"}");
                }else if(lMethod.equals("get_my_delegate_list")){
                    getDelegateList();
                    reply.reply("{\"method\":\"get_my_delegate_list\",\"code\":\"1\"}");
                }else if(lMethod.equals("get_delegate_detail_list")){
                    getDelegateDetailList();

                    reply.reply("{\"method\":\"get_delegate_detail_list\",\"code\":\"1\"}");
                }else if(lMethod.equals("get_delegation_value")){
                    String nodeId = (String) arguments.get("nodeId");
                    //getDelegationValue("0x99e0e221e025041d9025205e12af0b0fcdca8cb70d98f4bd6ab82be65fe0f59cab996e16ee56304b48ffb9de8d81fa7759594d61d186c9ea7442917fa555a463");
                    reply.reply("{\"method\":\"get_delegation_value\",\"code\":\"1\"}");
                }else if(lMethod.equals("send_delegate")){
                    System.out.println("send_delegate11111133333");
                    String amount = (String) arguments.get("value");
                    String nodeId = (String) arguments.get("nodeId");
                    String password = (String) arguments.get("password");
                    try{
                        Web3j mWeb3j = Web3j.build(new HttpService(URL_PLATON_TESTNET_RPC));
                        System.out.println("MainActivity  mWeb3j = "+mWeb3j);
                        sendDelegate(amount, nodeId, password, mWeb3j);
                    }catch(Exception e){
                    }
                    reply.reply("{\"method\":\"send_delegate\",\"code\":\"1\"}");
                }else if(lMethod.equals("withdraw_delegate")){
                    String amount = (String) arguments.get("value");
                    String nodeId = (String) arguments.get("nodeId");
                    String password = (String) arguments.get("password");

                    getDelegationValue(nodeId, amount, password);


                    reply.reply("{\"method\":\"send_delegate\",\"code\":\"1\"}");
                }else if(lMethod.equals("withdraw_delegate_reward")){
                    String amount = (String) arguments.get("value");
                    String nodeId = (String) arguments.get("nodeId");
                    String password = (String) arguments.get("password");
                    String stakingBlockNum = (String) arguments.get("stakingBlockNum");
                    try{
                        Web3j mWeb3j = Web3j.build(new HttpService(URL_PLATON_TESTNET_RPC));
                        System.out.println("MainActivity  mWeb3j = "+mWeb3j);
                        withdrawDelegateReward(amount, nodeId, password, mWeb3j, stakingBlockNum);
                    }catch(Exception e){
                    }
                    reply.reply("{\"method\":\"send_delegate\",\"code\":\"1\"}");
                }else if(lMethod.equals("set_language")){
                    String lang = (String) arguments.get("lang");
                    SharedPreferences language = getSharedPreferences("LANGUAGE", MODE_PRIVATE);
                    SharedPreferences.Editor editor = language.edit();
                    editor.putString("lang", lang);
                    editor.apply();
                    reply.reply("{\"method\":\"set_language\",\"code\":\"1\"}");
                }else if(lMethod.equals("get_language")){
                    SharedPreferences language = getSharedPreferences("LANGUAGE", MODE_PRIVATE);
                    String lang = language.getString("lang", null);
                    System.out.println("MainActivity  lang = "+lang);
                    reply.reply("{\"method\":\"get_language\",\"lang\":\""+lang+"\"}");
                }


            }
        });
    }

    private JSONArray getAssetList() {
        List<Asset> assetList = WalletManager.getInstance().getAssetList();
        JSONArray _assetList = new JSONArray();
        for(Asset asset : assetList){
            JSONObject _asset = new JSONObject();
            BigDecimal a = new BigDecimal("1000000000000000000");
            String assetBalance =  "0";
            if(asset.getBalance()!=null){
                assetBalance = asset.getBalance();
            }
            BigDecimal balance = new BigDecimal(assetBalance);
            String balance1 = NumberParserUtils.getPrettyNumber(balance.divide(a,2,ROUND_DOWN ).toPlainString(), 8);
            _asset.put("assetType", asset.getAssetType());
            _asset.put("name", asset.getName());
            _asset.put("symbol", asset.getSymbol());
            _asset.put("balance", balance1);
            _asset.put("id", asset.getId());
            System.out.println("onMessage  get_asset_list balance = "+ asset.getBalance() );
            _assetList.add(_asset);
        }
        return _assetList;

    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
            .setMethodCallHandler(
                    (call, result) -> {
                        //与Flutter Client invokeMethod调用字符串标识符匹配
                        if(call.method.equals("call_native_method")){
                            //initBlueTooth为后面安卓端需要调取的方法
                            result.success("success");
                        }else if(call.method.equals("wallet_exists")){

                            String chainId = NodeManager.getInstance().getChainId();
                            System.out.println("WalletManager11111111 chainId  = "+chainId);
                            long currentNodeId  = NodeManager.getInstance().getCheckedNode().blockingGet().getId();
                            System.out.println("wallet_create  currentNodeId = "+currentNodeId);

                            String currentChainId  = NodeManager.getInstance().getCheckedNode().blockingGet().getChainId();
                            System.out.println("wallet_create  currentChainId = "+currentChainId);
                            List<WalletEntity> walletlist = WalletDao.getWalletInfoListFromNodeId(currentNodeId);
                            System.out.println("wallet_create  walletlist.size = "+walletlist.size());


                            List<Wallet> walletList = WalletManager.getInstance().getWalletList();
                            if(walletList.size() > 0){
                                result.success("true");
                            }else{
                                result.success("false");
                            }
                        }
                        else if(call.method.equals("wallet_create")){
                            String name = (String) call.argument("name");
                            String password = (String) call.argument("password");

                            result.success("name = "+ name +" password = "+password);
                            //createWallet(name, password);
                        }

                        else {
                            result.notImplemented();
                        }
                    }
            );
    }

    @SuppressLint("CheckResult")
    private void createWallet(String wallet_name, String password, long nodeId){
        WalletManager.getInstance()
                .createWallet(wallet_name, password)
                .doOnSuccess(new Consumer<Wallet>() {
                    @Override
                    public void accept(Wallet walletEntity) throws Exception {
                        // Looper.prepare();
                        //Toast.makeText(getActivity(), "生成钱包成功。", Toast.LENGTH_LONG).show();
                        System.out.println("WalletManager11111111 createwallet111111111 生成钱包成功");
                        walletEntity.setBackedUpPrompt(true);
                        walletEntity.setNodeId(nodeId);
                        walletEntity.setSelected(true);
                        WalletManager.getInstance().addWallet(walletEntity);
                        WalletDao.insertWalletInfo(walletEntity.buildWalletInfoEntity());
                        System.out.println("WalletManager11111111 walletEntity ="+walletEntity.getUuid());
                        WalletManager.getInstance().setSelectedWalletWithUuid(walletEntity.getUuid());
                        WalletManager.getInstance().init();
                        currentWallet = WalletManager.getInstance().getCurrentWallet();
                        long time = System.currentTimeMillis();
                        Asset assetEntity = new AssetEntity.Builder()
                                .id(UUID.randomUUID().toString())
                                .walletId(currentWallet.getUuid())
                                .assetType(ASSET_LAT)
                                .createTime(time)
                                .updateTime(time)
                                .contractAddress("")
                                .binary("")
                                .name("")
                                .symbol("LAT")
                                .setHome(true)
                                .build().buildAsset();
                        WalletManager.getInstance().addAsset(assetEntity);
                        AssetDao.insertAssetInfo(assetEntity.buildAssetInfoEntity());
                        refreshBalance();
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("message", "success");
                        resultMap.put("code", 200);
                        mMessageChannel.send("{\"code\":200}", new BasicMessageChannel.Reply<Object>() {
                            @Override
                            public void reply(Object o) {
                                Log.d("mMessageChannel", "mMessageChannel send 回调 " + o);
                            }
                        });

                        webSocketInit();
                    }
                }).subscribe(new Consumer<Wallet>() {
                    @Override
                    public void accept(Wallet walletEntity) throws Exception {
                        System.out.println("WalletManager11111111 createwallet12222222");
                        System.out.println("WalletManager11111111 createwallet12222222");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        System.out.println("WalletManager11111111 createwallet1122222222");
                        throwable.printStackTrace();
                    }
                });;
    }
    @SuppressLint("CheckResult")
    private void getAccountBalance(){
        if (!mFetchWalletBalanceDisposable.isDisposed()) {
            mFetchWalletBalanceDisposable.dispose();
        }
        mFetchWalletBalanceDisposable = WalletManager.getInstance().getAccountBalance(t)
                .compose(RxUtils.getSchedulerTransformer())
                .subscribe(new Consumer<BigDecimal>() {
                    @Override
                    public void accept(BigDecimal balance) throws Exception {
                        System.out.println("WalletManager11111111 balance = "+balance);
                        BigDecimal a = new BigDecimal("1000000000000000000");
                        String balance1 = NumberParserUtils.getPrettyNumber(balance.divide(a,2,ROUND_DOWN ).toPlainString(), 8);
                        mMessageChannel.send("{\"method\":\"get_asset_balance\",\"balance\":\""+balance1+"\"}", new BasicMessageChannel.Reply<Object>() {
                            @Override
                            public void reply(Object o) {
                                Log.d("mMessageChannel", "mMessageChannel send 回调 " + o);
                            }
                        });
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        System.out.println("balance111111 throwable= "+throwable);

                    }
                });
        t++;
    }
    @SuppressLint("StaticFieldLeak")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getTransactionList(int pageNum, String assetId ){
        System.out.println("pageNum11111 = "+pageNum);
        int perPage = 6;
        //String assetId = getAssetListFromDb().get(0).getId();
        AssetEntity asset = AssetDao.getAssetById(assetId);
        System.out.println("assetId= "+assetId);
        System.out.println("assetId= "+assetId);
        List<Transaction> transactionList = TransactionManager.getInstance().getTransactionList();
        String address = currentWallet.getAddress();
        List addressList = new ArrayList<String>();
        addressList.add(address);
        long beginSequence;
        if (transactionList == null || transactionList.isEmpty() || direction.equals("new")) {
            beginSequence =  -1;
        }else{
            int size = transactionList.size();
            beginSequence = transactionList.get(size - 1).getSequence();
        }
        ServerUtils
                .getCommonApi()
                .getTransactionList(ApiRequestBody.newBuilder()
                        .put("walletAddrs", addressList)
                        .put("beginSequence", beginSequence)
                        .put("listSize", perPage * 2)
                        .put("direction", direction)
                        .build())
                .compose(getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<List<Transaction>>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onApiSuccess(List<Transaction> transactions) {
                        for(Transaction transaction : transactions ){
                            System.out.println("WalletActivity onApiSuccess transaction 33333333="+transaction);
                            if(asset.getAssetType() == ASSET_LAT
                            && (transaction.txType.equals(String.valueOf(TransactionType.TRANSFER.getTxTypeValue()))
                            || transaction.txType.equals(String.valueOf(TransactionType.CONTRACT_CREATION.getTxTypeValue()))
                            )){
                                transaction.setAssetId(assetId);
                                TransactionDao.insertTransaction(transaction.buildTransactionEntity());
                            }else if(
                                    asset.getAssetType()== ASSET_CONTRACT
                                    && transaction.txType.equals(String.valueOf(TransactionType.CONTRACT_EXECUTION.getTxTypeValue()))
                                    && asset.getContractAddress().equals(transaction.to)
                            ){
                                transaction.setAssetId(assetId);
                              // getContractAddress(transaction.getHash());
                                /*new AsyncTask<Void, Void, Void>() {
                                    @SuppressLint("StaticFieldLeak")
                                    @Override
                                    protected Void doInBackground(Void... voids) {
                                        getContractAddress(transaction.getHash());
                                        return null;
                                    }
                                }.execute();*/
                                //TransactionDao.insertTransaction(transaction.buildTransactionEntity());
                            }
                        }

                    }
                });
        int count = pageNum * perPage;
        TransactionManager.getInstance().refreshTransactionList(count, assetId);
        transactionList = TransactionManager.getInstance().getTransactionList();
        JSONArray _transactionList = new JSONArray();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        BigDecimal a = new BigDecimal("1000000000000000000");
        for(Transaction transaction : transactionList){
            JSONObject _transaction = new JSONObject();
            _transaction.put("hash",transaction.getHash());
            _transaction.put("timestamp",format.format(new Date(transaction.getTimestamp())));
            _transaction.put("blockNumber",transaction.getBlockNumber());
            _transaction.put("from",transaction.getFrom());
            _transaction.put("to",transaction.getTo());
            _transaction.put("assetId",transaction.getAssetId());
            _transaction.put("txReceiptStatus",transaction.getRedeemStatus());
            _transaction.put("txType",transaction.getTxType());
            _transaction.put("rAddress",transaction.getRAddress());
            _transaction.put("symbol",transaction.getSymbol() != null ? transaction.getSymbol() : "LAT");
            BigDecimal value = new BigDecimal(transaction.getValue());
            _transaction.put("value",NumberParserUtils.getPrettyNumber(value.divide(a,2,ROUND_DOWN ).toPlainString(), 8));
            _transaction.put("remark",transaction.getRemark());
            _transactionList.add(_transaction);
        }
        String transactions = JSON.toJSONString(_transactionList);
        System.out.println("getTransactionList transactions = "+transactions);
        mMessageChannel.send("{\"method\":\"get_transaction_list\",\"asset_id\":\""+assetId+"\",\"transactions\":"+transactions+"}", new BasicMessageChannel.Reply<Object>() {
            @Override
            public void reply(Object o) {
                Log.d("mMessageChannel", "mMessageChannel get_transaction_list 回调 " + o);
            }
        });

    }
    private List<Asset> getAssetListFromDb(){

        String walletId = currentWallet.getUuid();
        return Flowable
            .fromCallable(new Callable<List<AssetEntity>>() {
                @Override
                public List<AssetEntity> call() throws Exception {
                    return AssetDao.getAssetInfoListByWalletId(walletId);

                    //return AssetDao.getHomeAssetInfoListByWalletId(walletId);
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


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("CheckResult")
    private void getEstimateGas(Wallet wallet){
        System.out.println("getEstimateGas11111111111");
         TransactionManager.getInstance().getEstimateGas(wallet)
                .flatMap(new Function<GasProvider, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(GasProvider mGasProvider) throws Exception {
                        System.out.println("getEstimateGas1111112222222222222");
                        BigDecimal a = new BigDecimal("1000000000000000000");
                        gasLimit = new BigDecimal(mGasProvider.getGasLimit());
                        gasPrice = new BigDecimal(mGasProvider.getGasPrice());
                        int nonce1 = Integer.parseInt(mGasProvider.getNonce());
                        nonce = BigIntegerUtil.toBigInteger(String.valueOf(nonce1));
                        fee = NumberParserUtils.getPrettyNumber(gasLimit.multiply(gasPrice).divide(a,8,ROUND_HALF_UP ).toPlainString(), 8);
                        return Single
                                .fromCallable(new Callable<String>() {
                                    @Override
                                    public String call() throws Exception {
                                        return  NumberParserUtils.getPrettyNumber(gasLimit.multiply(gasPrice).divide(a,8,ROUND_HALF_UP ).toPlainString(), 8);

                                    }
                                }).toObservable();
                    }
                }).blockingFirst();
    }
    @SuppressLint("CheckResult")
    private void sendTransferTransaction(String from, String to, String value, String fee, String password, String gasPrice, String gasLimit, String nonce, String remark){
        TransactionRecordEntity transactionRecordEntity = new TransactionRecordEntity(System.currentTimeMillis(), currentWallet.getAddress(), to, value, NodeManager.getInstance().getChainId());
        try{
            TransactionManager
                    .getInstance()
                    .sendTransferTransaction( password, currentWallet, transactionRecordEntity, fee, BigIntegerUtil.toBigInteger(gasPrice) , BigIntegerUtil.toBigInteger(gasLimit), BigIntegerUtil.toBigInteger(nonce) , remark)
                    .compose(RxUtils.getSingleSchedulerTransformer())
                    .subscribe(new Consumer<Transaction>() {
                        @Override
                        public void accept(Transaction transaction) {
                            Toast.makeText(MainActivity.this, "发送成功。", Toast.LENGTH_LONG).show();
                            TransactionDao.insertTransaction(transaction.toTransactionEntity());
                            TransactionManager.getInstance().insertTransaction(transaction);
                            mMessageChannel.send("{\"method\":\"send_transfer_transaction\",\"status\":\"success\"}", new BasicMessageChannel.Reply<Object>() {
                                @Override
                                public void reply(Object o) {
                                    Log.d("mMessageChannel", "mMessageChannel get_transaction_list 回调 " + o);
                                }
                            });
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            System.out.println("sendTransaction111111111 throwable ="+throwable);
                        }
                    });

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @SuppressLint("CheckResult")
    protected  void sendDeployContractTransaction(String password,  String encodeData, String gasPrice, String gasLimit){
        try{
            System.out.println("sendDeployContractTransaction encodeData="+encodeData);
            Web3j mWeb3j = Web3j.build(new HttpService(URL_PLATON_TESTNET_RPC));
            System.out.println("sendDeployContractTransaction URL_PLATON_TESTNET_RPC="+URL_PLATON_TESTNET_RPC);



            System.out.println("sendDeployContractTransaction NodeManager.getInstance().getChainId()="+NodeManager.getInstance().getChainId());
            System.out.println("sendDeployContractTransaction Long.valueOf(NodeManager.getInstance().getChainId())=" + Long.valueOf(NodeManager.getInstance().getChainId()));

            Long chainId = Long.valueOf(NodeManager.getInstance().getChainId());
            System.out.println("sendDeployContractTransaction chainId="+chainId);
            TransactionManager
                    .getInstance()
                    .deployContractTransaction(chainId,  mWeb3j, password, currentWallet, BigIntegerUtil.toBigInteger(gasPrice) , BigIntegerUtil.toBigInteger(gasLimit), encodeData )
                    .compose(RxUtils.getSingleSchedulerTransformer())
                    .subscribe(new Consumer<String>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void accept(String hash) {
                            Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                            intent.putExtra("hash",  hash);
                            intent.putExtra("path",  "/result/success/msg/"+hash);
                            startActivityForResult(intent,2);
                            /*mMessageChannel.send("{\"method\":\"send_contract_transaction\",\"status\":\"success\"}", new BasicMessageChannel.Reply<Object>() {
                                @Override
                                public void reply(Object o) {
                                    Log.d("mMessageChannel", "mMessageChannel get_transaction_list 回调 " + o);
                                }
                            });
                           */

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            System.out.println("sendTransaction111111111 throwable ="+throwable);
                        }
                    });

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @SuppressLint("CheckResult")
    protected void sendContractTransaction(String from, String to, String _value, String gasPrice, String gasLimit, String password, String nonce, String remark, String assetId){

        Long chainId = Long.valueOf(NodeManager.getInstance().getChainId());
        NetworkParameters.MainNetParams.setChainId(chainId);
        AssetEntity asset =AssetDao.getAssetById(assetId);
        System.out.println("sendContractTransaction =");
        try {
            Web3j mWeb3j = Web3j.build(new HttpService(URL_PLATON_TESTNET_RPC));
            BigDecimal value = new BigDecimal(_value);
            BigDecimal a = new BigDecimal("1000000000000000000");
            BigInteger b = new BigInteger(String.valueOf(value.multiply(a).setScale(0, BigDecimal.ROUND_DOWN)));
            System.out.println("sendContractTransaction =b = "+b);
            final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                    "transfer",
                    Arrays.<Type>asList(new Address(to),
                            new Uint256(b)),
                    Collections.<TypeReference<?>>emptyList());
            String encodedFunction = FunctionEncoder.encode(function);
            System.out.println("sendContractTransaction =encodedFunction = "+encodedFunction);
            TransactionManager
                    .getInstance()
                    .sendContractTransaction(chainId,  mWeb3j,password, currentWallet, BigIntegerUtil.toBigInteger(gasPrice) , BigIntegerUtil.toBigInteger(gasLimit),asset.getContractAddress(), encodedFunction )
                    .compose(RxUtils.getSingleSchedulerTransformer())
                    //.compose(bindToLifecycle())
                    .subscribe(new Consumer<String>() {
                        @SuppressLint("StaticFieldLeak")
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void accept(String hash) {
                            System.out.println("transactionReceipt22222222222222222 = hash "+hash);
                            Toast.makeText(MainActivity.this, "发送成功。", Toast.LENGTH_LONG).show();
                            long timestamp = System.currentTimeMillis();
                            System.out.println("setAssetId11111111111111111111111 =  "+assetId);
                            Transaction transaction = new Transaction();
                            transaction.setHash(hash);
                            transaction.setFrom(from);
                            transaction.setTo(asset.getContractAddress());
                            transaction.setValue(String.valueOf(b));
                            transaction.setAssetId(assetId);
                            transaction.setTimestamp(timestamp);
                            transaction.setTxReceiptStatus(0);
                            transaction.setTxType( String.valueOf(com.platon.wallet.entity.TransactionType.CONTRACT_EXECUTION.getTxTypeValue()));
                            transaction.setSymbol(asset.getSymbol());
                            transaction.setContractAddress(asset.getContractAddress());
                            transaction.setRAddress(to);
                            boolean rs = TransactionDao.insertTransaction(transaction.buildTransactionEntity());
                            System.out.println("setAssetId11111111111111111111111 rs=  "+rs);

                            JSONObject _text = new JSONObject();
                            _text.put("type","send_token");
                            _text.put("address",from);
                            _text.put("to",to);
                            _text.put("msg",transaction);
                            String text = _text.toJSONString();

                            System.out.println("sendContractTransaction transaction.=  "+transaction.getTxReceiptStatus());
                            System.out.println("sendContractTransaction text=  "+text);

                            WebSocketManager.getInstance().asyncSend(text)
                                    .subscribe(new Consumer<Boolean>() {
                                @Override
                                public void accept(Boolean isSuccess) throws Exception {
                                    if(isSuccess) {
                                        //发送成功
                                    } else {
                                        //发送失败
                                    }
                                }
                            });;
                            System.out.println("sendContractTransaction text11111111111=  ");
                            mMessageChannel.send("{\"method\":\"send_transfer_transaction\",\"status\":\"success\"}", new BasicMessageChannel.Reply<Object>() {
                                @Override
                                public void reply(Object o) {
                                    Log.d("mMessageChannel", "mMessageChannel get_transaction_list 回调 " + o);
                                }
                            });



                            /*try{

                                Thread.sleep(2000);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }




                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... voids) {
                                    int status = 0; long timestamp = 0;
                                    try{
                                        com.platon.wallet.entity.TransactionReceipt transactionReceipt =  TransactionManager.getInstance().getTransactionReceipt(hash);
                                        status = transactionReceipt.getStatus();
                                        timestamp = Long.parseLong(transactionReceipt.getTimestamp());
                                    }catch(Exception ignored){
                                    }
                                    return null;
                                }
                            }.execute();*/
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();
                            System.out.println("sendTransaction111111111 throwable ="+throwable);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*@RequiresApi(api = Build.VERSION_CODES.N)
    private void getContractAddress(String hash){
        System.out.println("transactionReceipt.t222222222222222ransactionReceipt =hash "+hash);
        String contractAddress = "";
       try {
            PlatonGetTransactionReceipt receipt = getTransactionReceipt(hash);
            System.out.println("transactionReceipt.t222222222222222ransactionReceipt "+hash);
            if(receipt != null && !receipt.hasError() ){
                if(receipt.getTransactionReceipt().isPresent()){
                    TransactionReceipt transactionReceipt = receipt.getTransactionReceipt().get();
                    System.out.println("transactionReceipt.transactionReceipt = "+transactionReceipt);

                    System.out.println("transactionReceipt.transactionReceipt = "+transactionReceipt.getLogs().get(0).getTopics());

                    List<String> topics = transactionReceipt.getLogs().get(0).getTopics();
                    String input = "000000000000000000000000000000000000000000000001314fb37062980000";
                    final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function("symbol",
                            Arrays.<Type>asList(),
                            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));

                    String encodedFunction = FunctionEncoder.encode(function);
                    System.out.println("WebViewActivity 111111111 encodedFunction= "+encodedFunction);

                    Long chainId = Long.valueOf(NodeManager.getInstance().getChainId());
                    PlatonCall ethCall = null;
                    Web3j mWeb3j = Web3j.build(new HttpService(URL_PLATON_TESTNET_RPC));
                    try {
                        ethCall = (PlatonCall)mWeb3j.platonCall(org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(currentWallet.getAddress(), "lat1n99msu7m79stnwsw5qzmru2t6ghfhhznxfenj5" , encodedFunction), DefaultBlockParameterName.LATEST).send();
                    } catch (IOException e) {
                        System.out.println("WebViewActivity 111111111 e= "+e);
                        e.printStackTrace();
                    }
                    if(ethCall != null && ethCall.getValue() != null ) {

                        System.out.println("WebViewActivity 111111111 ethCall.getValue()= "+ethCall.getValue());

                        List<Type> values = FunctionReturnDecoder.decode("0x000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000024545000000000000000000000000000000000000000000000000000000000000", function.getOutputParameters(), 210309L);
                        System.out.println("WebViewActivity 111111111 values1.)= "+values.get(0).getValue().toString());
                       // PlatOnTypeDecoder.decode()
                        //String aa = WasmEventDecoder.decodeIndexParameter(topics.get(0), String.class );
                        //System.out.println("WebViewActivity 111111111 WasmEventDecoder aa = "+aa);

                        org.web3j.protocol.core.methods.request.PlatonFilter filter = new org.web3j.protocol.core.methods.request.PlatonFilter();
                        filter.addSingleTopic(topics.get(2));
                        List<PlatonLog.LogResult> aa =  mWeb3j.platonGetLogs(filter).send().getLogs();
                        System.out.println("WebViewActivity 111111111 aa= "+aa);

                    }

                    /*
                    List<TypeReference<?>> outputParameters1 = Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {});
                    List<TypeReference<Type>> outputParameters = Utils.convert(outputParameters1);


                    List<Type> results = new ArrayList(outputParameters.size());
                    int offset = 0;
                    Iterator var6 = outputParameters.iterator();

                    while(var6.hasNext()) {
                        TypeReference typeReference = (TypeReference)var6.next();

                        try {
                            Class<Type> type = typeReference.getClassType();

                            int hexStringDataOffset = !DynamicBytes.class.isAssignableFrom(type) && !Utf8String.class.isAssignableFrom(type) && !DynamicArray.class.isAssignableFrom(type) ? offset : TypeDecoder.decodeUintAsInt(input, offset, chainId) << 1;
                            Type result;

                                int length;

                                result = TypeDecoder.decode(input, hexStringDataOffset, type, chainId);
                                offset += 64;




                        } catch (ClassNotFoundException var12) {
                            throw new UnsupportedOperationException("Invalid class reference provided", var12);
                        }
                    }
                        }else{
                    System.out.println("transactionReceipt.getContractAddress2 = empty");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new AssertionError(e);
                    }*/
                    //contractAddress =  getContractAddress(hash);
      /*          }
            }
       / } catch (IOException e) {
            e.printStackTrace();
        }
       // return contractAddress;
    }*/
    private PlatonGetTransactionReceipt getTransactionReceipt(String hash) throws IOException {
        Web3j mWeb3j = Web3j.build(new HttpService(URL_PLATON_TESTNET_RPC));
        return mWeb3j.platonGetTransactionReceipt(hash).send();
    }
    @SuppressLint("CheckResult")
    private void refreshBalance(){

        System.out.println("MainActivity  refreshBalance 2222= ");

        List<Asset> assetList = WalletManager.getInstance().getAssetList();
        for(Asset asset:assetList){
            if(asset.getAssetType() == ASSET_LAT){
                if (!mFetchWalletBalanceDisposable.isDisposed()) {
                    mFetchWalletBalanceDisposable.dispose();
                }
                mFetchWalletBalanceDisposable = WalletManager.getInstance().getAccountBalance(1)
                        .compose(RxUtils.getSchedulerTransformer())
                        .subscribe(new Consumer<BigDecimal>() {
                            @SuppressLint("StaticFieldLeak")
                            @Override
                            public void accept(BigDecimal balance) throws Exception {

                                System.out.println("MainActivity  refreshBalance 33333= ");

                                 asset.setBalance(balance.toString());
                                AssetDao.updateAssetBalance(asset.getId(),balance.toString());
                                List<Asset> assetList1 = getAssetListFromDb();
                                WalletManager.getInstance().setAssetList(assetList1);
                                JSONArray  _assetList = getAssetList();
                                String assets = JSON.toJSONString(_assetList);
                                mMessageChannel.send("{\"method\":\"get_asset_list\",\"asset_list\":"+assets+"}", new BasicMessageChannel.Reply<Object>() {
                                    @Override
                                    public void reply(Object o) {
                                        Log.d("mMessageChannel", "mMessageChannel get_transaction_list 回调 " + o);
                                    }
                                });
                                new AsyncTask<Void, Void, Void>() {
                                    @SuppressLint("StaticFieldLeak")
                                    @Override
                                    protected Void doInBackground(Void... voids) {
                                        refreshContractAssetBalance();
                                        return null;
                                    }
                                }.execute();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                System.out.println("balance111111 throwable= "+throwable);
                            }
                        });
            }
        }

    }

    @SuppressLint("StaticFieldLeak")
    private void checkUpdate(){
        new AsyncTask<Void, Void, Boolean>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @SuppressLint({"CheckResult", "StaticFieldLeak"})
            @Override
            protected Boolean doInBackground(Void... voids) {
                try{
                    return _checkUpdate();
                }catch(Exception e){
                    e.printStackTrace();
                }
                return false;
            }
            @Override
            protected void onPostExecute(@NonNull Boolean rs) {
                if(rs){
                    System.out.println("MainActivity    json.getVersionCode() > getVersionCode()");
                    SharedPreferences update = getSharedPreferences("UPDATE", MODE_PRIVATE);
                    boolean updateApk = update.getBoolean("update", false);
                    if(updateApk){
                        mMessageChannel.send("{\"method\":\"check_update\",\"update\":\"true\",\"json\":"+versionJson+"}", new BasicMessageChannel.Reply<Object>() {
                            @Override
                            public void reply(Object o) {
                                Log.d("mMessageChannel", "mMessageChannel get_transaction_list 回调 " + o);
                            }
                        });

                    }
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean _checkUpdate() throws IOException, PackageManager.NameNotFoundException {

        Log.i(TAG, "Checking for APK update...");
        OkHttpClient client  = new OkHttpClient();
        Request request = new Request.Builder().url(String.format("%s/latest.json", NOPLAY_UPDATE_URL)).build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Bad response: " + response.message());
        }

        //System.out.println("response.body().string() = "+response.body().string());
        System.out.println("11111111111111111111");
        ObjectMapper objectMapper = new ObjectMapper();
        versionJson = response.body().string();
        UpdateApk json = objectMapper.readValue(versionJson, UpdateApk.class);
        //UpdateApk json = JsonUtils.fromJson(response.body().string(), UpdateApk.class);
        System.out.println("json = "+json);
        System.out.println("2222222222222222");
        uri = json.getUrl();
        byte[] digest           = Hex.fromStringCondensed(json.getDigest());
        versionName = json.getVersionName();
        System.out.println("2222222222222222 uri ="+uri);

        System.out.println("2222222222222222 versionName ="+versionName);

        System.out.println("2222222222222222 json.getVersionCode()  ="+json.getVersionCode() );

        System.out.println("2222222222222222 json.getVersionCode()  ="+getVersionCode() );
        //lato
        if (json.getVersionCode() > getVersionCode()) {
            return true;
        }
        return false;
    }
    private void update(){
        System.out.println("update  Permissions");
        Permissions.with(MainActivity.this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .ifNecessary()
                .withRationaleDialog("应用需要访问储存权限")
                .withPermanentDenialDialog("应用需要访问储存权限")
                .onAllGranted(()->{
                    System.out.println("Permissions11  onAllGranted");
                    UpdateApk updateApk = new UpdateApk(MainActivity.this , uri, versionName,true, new UpdateApk.UpdateApkViewListener(){

                        @Override
                        public void onChange(double i) {
                            System.out.println("onChange= "+i);
                            mMessageChannel.send("{\"method\":\"update_progress\",\"progress\":"+i+"}", new BasicMessageChannel.Reply<Object>() {
                                @Override
                                public void reply(Object o) {
                                    Log.d("mMessageChannel", "mMessageChannel get_transaction_list 回调 " + o);
                                }
                            });
                        }
                    });
                    updateApk.execute();
                })
                .onAnyDenied(() -> Toast.makeText(MainActivity.this, "应用需要访问储存权限", Toast.LENGTH_LONG).show())
                .execute();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Permissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
    private int getVersionCode() throws PackageManager.NameNotFoundException {
        PackageManager packageManager = getPackageManager();
        PackageInfo packageInfo    = packageManager.getPackageInfo(getPackageName(), 0);
        System.out.println("UpdateApk  getVersionCode packageInfo ="+packageInfo);
        return packageInfo.versionCode;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (intent != null) {
                    String encodeData = intent.getStringExtra("encodeData");
                    String gasLimit = intent.getStringExtra("gasLimit");
                    String gasPrice = intent.getStringExtra("gasPrice");
                    System.out.println("encodeData = "+ encodeData);
                    mMessageChannel.send("{\"method\":\"app_create_erc20_token\",\"encodeData\":\""+encodeData+"\",\"gasLimit\":\""+gasLimit+"\",\"gasPrice\":\""+gasPrice+"\"}", new BasicMessageChannel.Reply<Object>() {
                        @Override
                        public void reply(Object o) {
                            Log.d("mMessageChannel", "mMessageChannel get_transaction_list 回调 " + o);
                        }
                    });
                }
            }
        }
    }


    public Asset searchAssetFromNode(String contractAddress){
        Web3j mWeb3j = Web3j.build(new HttpService(URL_PLATON_TESTNET_RPC));
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function("name",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        String encodedFunction = FunctionEncoder.encode(function);

        final org.web3j.abi.datatypes.Function function1 = new org.web3j.abi.datatypes.Function("symbol",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        String encodedFunction1 = FunctionEncoder.encode(function1);
        PlatonCall ethCall = null;
        PlatonCall ethCall1 = null;
        try {
            ethCall = (PlatonCall)mWeb3j.platonCall(org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(currentWallet.getAddress(),contractAddress, encodedFunction), DefaultBlockParameterName.LATEST).send();
            ethCall1 = (PlatonCall)mWeb3j.platonCall(org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(currentWallet.getAddress(),contractAddress, encodedFunction1), DefaultBlockParameterName.LATEST).send();
            System.out.println("WebViewActivity 111111111 ethCall= "+ethCall);
            System.out.println("WebViewActivity 111111111 ethCall1= "+ethCall1);
        } catch (IOException e) {
            System.out.println("WebViewActivity 111111111 e= "+e);
            e.printStackTrace();
        }
        System.out.println("WebViewActivity 111111111 ethCall.getValue()= "+ethCall.getValue());
        System.out.println("WebViewActivity 111111111 ethCall1.getValue()= "+ethCall1.getValue());
        if(ethCall != null && ethCall.getValue() != null && ethCall1 != null && ethCall1.getValue() != null){
            List<Type> values = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters(), 210309L);
            List<Type> values1 = FunctionReturnDecoder.decode(ethCall1.getValue(), function1.getOutputParameters(), 210309L);
            System.out.println("WebViewActivity 111111111 values= "+values);
            System.out.println("WebViewActivity 111111111 values1.)= "+values1);

            if(!values.isEmpty() && !values1.isEmpty()){
                System.out.println("AddAssetActivity ethCall. result ="+values.get(0).toString());
                String symbol = values.get(0).toString();
                String name = values1.get(0).getValue().toString();
                Asset asset = new Asset.Builder()
                        .contractAddress(contractAddress)
                        .symbol(symbol)
                        .name(name)
                        .build();
                return asset;
            }
        }
        return null;
    }

    private void webSocketInit(){
        WebSocketManager.getInstance().setWebSocketListener(new WebSocketListener() {
            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                super.onOpen(webSocket, response);
                System.out.println("WebSocketManager onOpen11111111111111");
                WebSocketManager.getInstance().setAddress(currentWallet.getAddress());
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                System.out.println("WebSocketManager onMessage=  "+text);

                try{
                    JSONObject obj = JSONObject.parseObject(text);
                    if(obj != null){
                        String type = obj.getString("type");
                        if(type.equals("send_token")){
                            Transaction transaction = obj.getObject("msg",Transaction.class);
                            System.out.println("WebSocketManager transactiongetTxType=  "+transaction.getTxType());
                            if(transaction != null){
                               // boolean rs = TransactionDao.insertTransaction(transaction.buildTransactionEntity());
                                System.out.println("save Transaction  transaction=  "+transaction.getFrom());
                                String assetId;
                                AssetEntity assetEntity = AssetDao.getAssetFromContractAddress(transaction.getContractAddress());
                                if(assetEntity != null){
                                    assetId = assetEntity.getId();
                                    transaction.setAssetId(assetId);
                                    TransactionDao.insertTransaction(transaction.buildTransactionEntity());
                                }else{
                                    assetId = UUID.randomUUID().toString();
                                }
                                long time = System.currentTimeMillis();
                                String messageId = UUID.randomUUID().toString();
                                transaction.setAssetId(assetId);
                                MessageEntity messageEntity = new MessageEntity.Builder()
                                        .id(messageId)
                                        .createTime(time)
                                        .updateTime(time)
                                        .msg(JSON.toJSONString(transaction))
                                        .type("send_token")
                                        .read(false)
                                        .build();
                                MessageDao.insertMessage(messageEntity);

                                mMessageChannel.send("{\"method\":\"new_message\",\"code\":\"1\"}", new BasicMessageChannel.Reply<Object>() {
                                    @Override
                                    public void reply(Object o) {
                                        Log.d("mMessageChannel", "mMessageChannel new_message 回调 " + o);
                                    }
                                });

                            }

                        }
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
                System.out.println("WebSocketManager onMessage");
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                System.out.println("WebSocketManager onClosing");
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                System.out.println("WebSocketManager onClosed");
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                System.out.println("WebSocketManager onFailure");
            }
        });

    }




    private void getVerifyNodeList(){
        ServerUtils.getCommonApi().getVerifyNodeList()
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<List<VerifyNode>>() {
                    @Override
                    public void onApiSuccess(List<VerifyNode> nodeList) {
                        JSONArray _nodeList = new JSONArray();
                        for(VerifyNode node : nodeList) {
                            JSONObject _node = new JSONObject();
                            BigDecimal a = new BigDecimal("1000000000000000000");
                            String delegateSum = "0";
                            if (node.getDelegateSum() != null) {
                                delegateSum = node.getDelegateSum();
                            }
                            BigDecimal _delegateSum = new BigDecimal(delegateSum);
                            String _delegateSum1 = NumberParserUtils.getPrettyNumber(_delegateSum.divide(a, 8, ROUND_DOWN).toPlainString(), 8);
                            _node.put("delegateSum",_delegateSum1);
                            _node.put("name",node.getName());
                            _node.put("showDelegatedRatePA",node.getShowDelegatedRatePA());
                            _node.put("url",node.getUrl());
                            _node.put("nodeId",node.getNodeId());


                            _nodeList.add(_node);
                        }


                            String node_list = JSON.toJSONString(_nodeList);
                        mMessageChannel.send("{\"method\":\"get_verify_node_list\",\"node_list\":"+node_list+"}", new BasicMessageChannel.Reply<Object>() {
                            @Override
                            public void reply(Object o) {
                                Log.d("mMessageChannel", "mMessageChannel get_verify_node_list 回调 " + o);
                            }
                        });

                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {

                    }
                });
    }
    private void getNodeCandidateDetail(String nodeId){

        ServerUtils.getCommonApi()
                .getNodeCandidateDetail(ApiRequestBody.newBuilder()
                        .put("nodeId", nodeId)
                        .build())
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<VerifyNodeDetail>() {
                    @Override
                    public void onApiSuccess(VerifyNodeDetail verifyNodeDetail) {
                        BigDecimal a = new BigDecimal("1000000000000000000");


                        String cumulativeReward = "0";
                        if (verifyNodeDetail.getCumulativeReward() != null) {
                            cumulativeReward = verifyNodeDetail.getCumulativeReward();
                        }
                        BigDecimal _cumulativeReward= new BigDecimal(cumulativeReward);
                        String _cumulativeReward1 = NumberParserUtils.getPrettyNumber(_cumulativeReward.divide(a, 8, ROUND_DOWN).toPlainString(), 8);
                        verifyNodeDetail.setCumulativeReward(_cumulativeReward1);

                        String delegatedRatePA = "0";
                        if (verifyNodeDetail.getDelegatedRatePA() != null) {
                            delegatedRatePA = verifyNodeDetail.getDelegatedRatePA();
                        }
                        BigDecimal b = new BigDecimal("100");
                        BigDecimal _delegatedRatePA= new BigDecimal(delegatedRatePA);
                        String _delegatedRatePA1 = NumberParserUtils.getPrettyNumber(_delegatedRatePA.divide(b, 8, ROUND_DOWN).toPlainString(), 8);
                        verifyNodeDetail.setDelegatedRatePA(_delegatedRatePA1 + "%");

                        String delegatedRewardPer = "0";
                        if (verifyNodeDetail.getDelegatedRewardPer() != null) {
                            delegatedRewardPer = verifyNodeDetail.getDelegatedRewardPer();
                        }
                        BigDecimal _delegatedRewardPer= new BigDecimal(delegatedRewardPer);
                        String _delegatedRewardPer1 = NumberParserUtils.getPrettyNumber(_delegatedRewardPer.divide(b, 8, ROUND_DOWN).toPlainString(), 8);
                        verifyNodeDetail.setDelegatedRewardPer(_delegatedRewardPer1 + "%");


                        String deposit = "0";
                        if (verifyNodeDetail.getDeposit() != null) {
                            deposit = verifyNodeDetail.getDeposit();
                        }
                        BigDecimal _deposit = new BigDecimal(deposit);
                        String _deposit1 = NumberParserUtils.getPrettyNumber(_deposit.divide(a, 8, ROUND_DOWN).toPlainString(), 8);
                        verifyNodeDetail.setDeposit(_deposit1);


                        String delegateSum = "0";
                        if (verifyNodeDetail.getDelegateSum() != null) {
                            delegateSum = verifyNodeDetail.getDelegateSum();
                        }
                        BigDecimal _delegateSum = new BigDecimal(delegateSum);
                        String _delegateSum1 = NumberParserUtils.getPrettyNumber(_delegateSum.divide(a, 8, ROUND_DOWN).toPlainString(), 8);
                        verifyNodeDetail.setDelegateSum(_delegateSum1);



                        String node_detail = JSON.toJSONString(verifyNodeDetail);
                        mMessageChannel.send("{\"method\":\"get_verify_node_detail\",\"node_detail\":"+node_detail+"}", new BasicMessageChannel.Reply<Object>() {
                            @Override
                            public void reply(Object o) {
                                Log.d("mMessageChannel", "mMessageChannel get_verify_node_list 回调 " + o);
                            }
                        });
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {

                    }
                });
    }
    @SuppressLint("CheckResult")
    private void sendDelegate(String amount, String nodeId, String password, Web3j mWeb3j ){
        org.web3j.tx.gas.GasProvider  gasProvider= new org.web3j.tx.gas.GasProvider() {
            @Override
            public BigInteger getGasPrice() {
                return new BigInteger("60000000000");
            }
            @Override
            public BigInteger getGasLimit() {
                return new BigInteger("2100000");
            }
        };

        DelegateManager.getInstance()
            .delegate(mWeb3j, amount, nodeId, StakingAmountType.FREE_AMOUNT_TYPE, gasProvider, password, currentWallet)

            .subscribe(new Consumer<String>() {
                @Override
                public void accept(String hash) throws Exception {
                    System.out.println("sendDelegate eeeeeeee");
                    if(hash != null){
                        Toast.makeText(getActivity(), "委托成功。", Toast.LENGTH_LONG).show();
                        mMessageChannel.send("{\"method\":\"send_delegate\",\"hash\":\""+hash+"\"}", new BasicMessageChannel.Reply<Object>() {
                            @Override
                            public void reply(Object o) {
                                Log.d("mMessageChannel", "mMessageChannel send_delegate 回调 " + o);
                            }
                        });
                    }

                }
            });

    }

    @SuppressLint("CheckResult")
    private void withdrawDelegate(String amount, String nodeId, String password, Web3j mWeb3j, String stakingBlockNum ) throws Exception {
        org.web3j.tx.gas.GasProvider  gasProvider= new org.web3j.tx.gas.GasProvider() {
            @Override
            public BigInteger getGasPrice() {
                return new BigInteger("60000000000");
            }
            @Override
            public BigInteger getGasLimit() {
                return new BigInteger("2100000");
            }
        };
        DelegateManager.getInstance()
                .withdrawDelegate(mWeb3j, password, currentWallet, nodeId, stakingBlockNum, amount, gasProvider)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String hash) throws Exception {
                        System.out.println("sendDelegate eeeeeeee");
                        if(hash != null){
                            Toast.makeText(getActivity(), "委托成功。", Toast.LENGTH_LONG).show();
                            mMessageChannel.send("{\"method\":\"send_delegate\",\"hash\":\""+hash+"\"}", new BasicMessageChannel.Reply<Object>() {
                                @Override
                                public void reply(Object o) {
                                    Log.d("mMessageChannel", "mMessageChannel send_delegate 回调 " + o);
                                }
                            });
                        }

                    }
                });

    }
    @SuppressLint("CheckResult")
    private void withdrawDelegateReward(String amount, String nodeId, String password, Web3j mWeb3j, String stakingBlockNum ) throws Exception {
        org.web3j.tx.gas.GasProvider  gasProvider= new org.web3j.tx.gas.GasProvider() {
            @Override
            public BigInteger getGasPrice() {
                return new BigInteger("60000000000");
            }
            @Override
            public BigInteger getGasLimit() {
                return new BigInteger("2100000");
            }
        };
        DelegateManager.getInstance()
                .withdrawDelegateReward(mWeb3j, password, currentWallet,  gasProvider)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String hash) throws Exception {
                        System.out.println("sendDelegate eeeeeeee");
                        if(hash != null){
                            Toast.makeText(getActivity(), "委托成功。", Toast.LENGTH_LONG).show();
                            mMessageChannel.send("{\"method\":\"send_delegate\",\"hash\":\""+hash+"\"}", new BasicMessageChannel.Reply<Object>() {
                                @Override
                                public void reply(Object o) {
                                    Log.d("mMessageChannel", "mMessageChannel send_delegate 回调 " + o);
                                }
                            });
                        }

                    }
                });

    }


    private void getDelegateList(){
        List addressList = new ArrayList<String>();
        addressList.add(currentWallet.getAddress());
        ServerUtils.getCommonApi().getMyDelegateList(ApiRequestBody.newBuilder().
            put("walletAddrs",addressList )
            .build())
            .compose(RxUtils.getSingleSchedulerTransformer())
            .subscribe(new ApiSingleObserver<List<DelegateInfo>>() {
                @Override
                public void onApiSuccess(List<DelegateInfo> delegateList) {

                    JSONArray _delegateList = new JSONArray();
                    for(DelegateInfo delegate : delegateList) {
                        JSONObject _delegate = new JSONObject();
                        BigDecimal a = new BigDecimal("1000000000000000000");
                        String cumulativeReward = "0";
                        if (delegate.getCumulativeReward() != null) {
                            cumulativeReward = delegate.getCumulativeReward();
                        }
                        BigDecimal _cumulativeReward = new BigDecimal(cumulativeReward);
                        String _cumulativeReward1 = NumberParserUtils.getPrettyNumber(_cumulativeReward.divide(a, 8, ROUND_DOWN).toPlainString(), 8);

                        String delegated = "0";
                        if (delegate.getDelegated() != null) {
                            delegated = delegate.getDelegated();
                        }
                        BigDecimal _delegated = new BigDecimal(delegated);
                        String _delegated1 = NumberParserUtils.getPrettyNumber(_delegated.divide(a, 2, ROUND_DOWN).toPlainString(), 8);

                        String withdrawReward = "0";
                        if (delegate.getWithdrawReward() != null) {
                            withdrawReward = delegate.getWithdrawReward();
                        }
                        BigDecimal _withdrawReward = new BigDecimal(withdrawReward);
                        String _withdrawReward1 = NumberParserUtils.getPrettyNumber(_withdrawReward.divide(a, 8, ROUND_DOWN).toPlainString(), 8);
                        _delegate.put("cumulativeReward",_cumulativeReward1);
                        _delegate.put("delegated",_delegated1);
                        _delegate.put("withdrawReward",_withdrawReward1);
                        _delegateList.add(_delegate);
                    }

                        String delegate_list = JSON.toJSONString(_delegateList);

                    System.out.println("delegate_list1111 = "+delegate_list);
                    mMessageChannel.send("{\"method\":\"get_my_delegate_list\",\"delegate_list\":"+delegate_list+"}", new BasicMessageChannel.Reply<Object>() {
                        @Override
                        public void reply(Object o) {
                            Log.d("mMessageChannel", "mMessageChannel get_verify_node_list 回调 " + o);
                        }
                    });

                }

                @Override
                public void onApiFailure(ApiResponse response) {

                }
            });

    }
    private void getDelegateDetailList(){
        ServerUtils.getCommonApi()
                .getDelegateDetailList(ApiRequestBody.newBuilder()
                        .put("addr", currentWallet.getAddress())
                        .build())
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<DelegateNodeDetail>() {
                    @Override
                    public void onApiSuccess(DelegateNodeDetail delegateNodeDetail) {
                        System.out.println("delegate_list1111 delegateNodeDetail= "+delegateNodeDetail.getDelegated());
                        System.out.println("delegate_list1111 delegateNodeDetail= "+delegateNodeDetail.getDelegateItemInfoList());
                        List<DelegateItemInfo> list = delegateNodeDetail.getDelegateItemInfoList();



                        for(DelegateItemInfo delegateItemInfo :list){
                            String delegated = "0";

                            BigDecimal a = new BigDecimal("1000000000000000000");
                            if (delegateItemInfo.getDelegated() != null) {
                                delegated = delegateItemInfo.getDelegated();
                            }
                            BigDecimal _delegated = new BigDecimal(delegated);
                            String _delegated1 = NumberParserUtils.getPrettyNumber(_delegated.divide(a, 8, ROUND_DOWN).toPlainString(), 8);
                            delegateItemInfo.setDelegated(_delegated1);



                            String withdrawReward = "0";
                            if (delegateItemInfo.getWithdrawReward() != null) {
                                withdrawReward = delegateItemInfo.getWithdrawReward();
                            }
                            BigDecimal _withdrawReward = new BigDecimal(withdrawReward);
                            String _withdrawReward1 = NumberParserUtils.getPrettyNumber(_withdrawReward.divide(a, 8, ROUND_DOWN).toPlainString(), 8);
                            delegateItemInfo.setWithdrawReward(_withdrawReward1);

                        }
                        String delegate_list = JSON.toJSONString(list);

                        System.out.println("delegate_list1111 = "+delegate_list);
                        mMessageChannel.send("{\"method\":\"get_delegate_detail_list\",\"delegate_list\":"+delegate_list+"}", new BasicMessageChannel.Reply<Object>() {
                            @Override
                            public void reply(Object o) {
                                Log.d("mMessageChannel", "mMessageChannel get_verify_node_list 回调 " + o);
                            }
                        });

                    }
                });
    }
    public void getDelegationValue(String nodeId, String amount, String password) {
        ServerUtils.getCommonApi().getDelegationValue(ApiRequestBody.newBuilder()
                .put("addr", currentWallet.getAddress())
                .put("nodeId", nodeId)
                .build())
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<DelegationValue>() {
                    @Override
                    public void onApiSuccess(DelegationValue delegationValue) {
                        System.out.println("delegationValue1111 = "+delegationValue.getDefaultShowWithDrawBalance().getStakingBlockNum());
                        String stakingBlockNum = delegationValue.getDefaultShowWithDrawBalance().getStakingBlockNum();
                        try{
                            Web3j mWeb3j = Web3j.build(new HttpService(URL_PLATON_TESTNET_RPC));
                            System.out.println("MainActivity  mWeb3j = "+mWeb3j);
                            withdrawDelegate(amount, nodeId, password, mWeb3j, stakingBlockNum);
                        }catch(Exception e){
                        }

                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        super.onApiFailure(response);
                        /*if (isViewAttached()) {
                            showLongToast(R.string.msg_connect_timeout);
                        }*/
                    }
                });
    }

}
