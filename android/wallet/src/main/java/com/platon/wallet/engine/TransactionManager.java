package com.platon.wallet.engine;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.platon.wallet.ErrorCode;
import com.platon.wallet.FunctionType;
import com.platon.wallet.PlatOnContract;
import com.platon.wallet.PlatOnFunction;
import com.platon.wallet.app.Constants;
import com.platon.wallet.app.CustomThrowable;
import com.platon.wallet.db.entity.TransactionEntity;
import com.platon.wallet.db.entity.TransactionRecordEntity;
import com.platon.wallet.db.sqlite.TransactionDao;
import com.platon.wallet.entity.EstimateGasResult;
import com.platon.wallet.entity.GasProvider;
import com.platon.wallet.entity.RPCErrorCode;
import com.platon.wallet.entity.RPCNonceResult;
import com.platon.wallet.entity.RPCTransactionResult;
import com.platon.wallet.entity.SubmitTransactionData;
import com.platon.wallet.entity.Transaction;
import com.platon.wallet.entity.TransactionReceipt;
import com.platon.wallet.entity.TransactionStatus;
import com.platon.wallet.entity.TransactionType;
import com.platon.wallet.entity.Wallet;
import com.platon.wallet.event.EventPublisher;
import com.platon.wallet.network.ApiRequestBody;
import com.platon.wallet.network.ApiResponse;
import com.platon.wallet.network.ApiSingleObserver;
import com.platon.wallet.utils.BigDecimalUtil;
import com.platon.wallet.utils.BigIntegerUtil;
import com.platon.wallet.utils.JSONUtil;
import com.platon.wallet.utils.LogUtils;
import com.platon.wallet.utils.MapUtils;
import com.platon.wallet.utils.NumberParserUtils;
import com.platon.wallet.utils.SignCodeUtils;

import org.reactivestreams.Publisher;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.Sign;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.PlatonGetTransactionCount;
import org.web3j.protocol.core.methods.response.PlatonGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.PlatonSendTransaction;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.exceptions.ClientConnectionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;



import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

import static com.platon.wallet.utils.RxUtils.getSingleSchedulerTransformer;

/**
 * @author matrixelement
 */
public class TransactionManager {

    private static final String UTF_8 = "UTF-8";
    private volatile Map<String, Disposable> mDisposableMap = new HashMap<>();
    private volatile Map<String, Object> mPendingMap = new HashMap<>();
    private List<Transaction> transactionList = new ArrayList<>();
    private TransactionManager() {

    }

    private static class InstanceHolder {
        private static volatile TransactionManager INSTANCE = new TransactionManager();
    }

    public static TransactionManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public Wallet getBalanceByAddress(Wallet walletEntity) {
        return walletEntity;
    }

    public Disposable removeTaskByHash(String hash) {
        return mDisposableMap.remove(hash);
    }

    public void putPendingTransaction(String from, long timeStamp) {
        mPendingMap.put(buildPendingMapKey(from), timeStamp);
    }

    public long getPendingTransactionTimeStamp(String from) {
        return MapUtils.getLong(mPendingMap, buildPendingMapKey(from));
    }

    public void removePendingTransaction(String from) {
        mPendingMap.remove(buildPendingMapKey(from));
    }
    public List<Transaction> getTransactionList(){
        return this.transactionList;
    }
    public void insertTransaction(Transaction transaction){
        transactionList.add(transaction);
    }

    public void refreshTransactionList(int count, String assetId){
        transactionList.clear();
        List<TransactionEntity> transactionEntityListList = TransactionDao.getTransactionListFromAssetId(assetId);
        int _count = Math.min(transactionEntityListList.size(), count);
        for(int i = 0; i < _count; i++){
            transactionList.add(transactionEntityListList.get(i).buildTransaction());
        }
    }
    public void loadMoreTransactionList(int start,int perPage, String assetId){
        List<TransactionEntity> transactionEntityListList= TransactionDao.getTransactionListFromAssetId(assetId);
        int count = Math.min(transactionEntityListList.size(), start + perPage);
        for(int i = start; i < count; i++){
            transactionList.add(transactionEntityListList.get(i).buildTransaction());
        }
    }

    /**b
     * 是否允许发送交易，与上笔pending中交易间隔超过五分钟
     *
     * @return
     */
    public long getSendTransactionTimeInterval(String from, long currentTime) {

        long timestamp = getPendingTransactionTimeStamp(from);

        return Constants.Common.TRANSACTION_SEND_INTERVAL - (currentTime - timestamp);

    }

    /**
     * 是否允许发送交易，与上笔pending中交易间隔超过五分钟
     *
     * @return
     */
    public boolean isAllowSendTransaction(String from, long currentTime) {

        long timestamp = getPendingTransactionTimeStamp(from);

        return currentTime - timestamp > Constants.Common.TRANSACTION_SEND_INTERVAL;
    }


    public void putTask(String hash, Disposable disposable) {
        if (!mDisposableMap.containsKey(hash)) {
            mDisposableMap.put(hash, disposable);
        }
    }

    public void cancelTaskByHash(String hash) {
        Disposable disposable = removeTaskByHash(hash);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
/*
    public Single<RPCTransactionResult> sendContractTransaction(PlatOnContract platOnContract, final Credentials credentials, PlatOnFunction platOnFunction, String nonce) throws IOException {

        return signedTransaction(platOnContract, credentials, platOnFunction.getGasProvider().getGasPrice(), platOnFunction.getGasProvider().getGasLimit(), platOnContract.getContractAddress(), platOnFunction.getEncodeData(), BigInteger.ZERO, nonce)
                .flatMap(new Function<String, SingleSource<RPCTransactionResult>>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public SingleSource<RPCTransactionResult> apply(String signedMessage) throws Exception {
                        return submitTransaction(createSigned(credentials.getEcKeyPair(), signedMessage, ""), signedMessage, "");
                    }
                });
    }*/

    /**
     * 提交交易
     *
     * @param sign
     * @param signedMessage
     * @param remark
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Single<RPCTransactionResult> submitTransaction(String sign, final String signedMessage, String remark) {

        return ServerUtils.getCommonApi().submitSignedTransaction(ApiRequestBody.newBuilder()
                .put("data", JSONUtil.toJSONString(new SubmitTransactionData(signedMessage, remark)))
                .put("sign", sign)
                .build())
                .flatMap(new Function<Response<ApiResponse<String>>, SingleSource<RPCTransactionResult>>() {
                    @Override
                    public SingleSource<RPCTransactionResult> apply(final Response<ApiResponse<String>> apiResponseResponse) {
                        System.out.println("TransactionManager apiResponseResponse = "+apiResponseResponse);
                        return Single.create(new SingleOnSubscribe<RPCTransactionResult>() {
                            @Override
                            public void subscribe(SingleEmitter<RPCTransactionResult> emitter) {
                                System.out.println("TransactionManager apiResponseResponse 111111111111111 ");
                                if (apiResponseResponse != null) {

                                    System.out.println("TransactionManager apiResponseResponse 222222222222222 ");
                                    System.out.println("TransactionManager apiResponseResponse apiResponseResponse.isSuccessful()  = "+apiResponseResponse.isSuccessful());
                                    System.out.println("TransactionManager apiResponseResponse apiResponseResponse.body() =  "+apiResponseResponse.body());
                                    //System.out.println("TransactionManager apiResponseResponse apiResponseResponse.body().getErrorCode()  =  "+apiResponseResponse.body().getErrorCode() );
                                    if (apiResponseResponse.isSuccessful() && apiResponseResponse.body() != null) {

                                        System.out.println("TransactionManager apiResponseResponse 33333333333333333333333 ");
                                        emitter.onSuccess(new RPCTransactionResult(RPCErrorCode.SUCCESS, apiResponseResponse.body().getData()));
                                    } else {

                                        System.out.println("TransactionManager apiResponseResponse 44444444444444444 =  "+apiResponseResponse.body().getErrorCode());
                                        emitter.onSuccess(new RPCTransactionResult(apiResponseResponse.body().getErrorCode()));
                                    }
                                }
                            }
                        });
                    }
                })
                .onErrorReturn(new Function<Throwable, RPCTransactionResult>() {
                    @Override
                    public RPCTransactionResult apply(Throwable throwable) {
                        if (throwable instanceof SocketTimeoutException) {
                            return new RPCTransactionResult(RPCErrorCode.SOCKET_TIMEOUT, Hash.sha3(signedMessage));
                        } else if (throwable instanceof ClientConnectionException) {
                            return new RPCTransactionResult(RPCErrorCode.CONNECT_TIMEOUT);
                        }
                        return null;
                    }
                });

    }


    public Single<BigInteger> getNonce(final String from) {

        return Single
                .fromCallable(new Callable<RPCNonceResult>() {
                    @Override
                    public RPCNonceResult call() throws Exception {
                        return Web3jManager.getInstance().getNonce(from);
                    }
                })
                .flatMap(new Function<RPCNonceResult, SingleSource<BigInteger>>() {
                    @Override
                    public SingleSource<BigInteger> apply(final RPCNonceResult rpcNonceResult) throws Exception {
                        return Single.create(new SingleOnSubscribe<BigInteger>() {
                            @Override
                            public void subscribe(SingleEmitter<BigInteger> emitter) throws Exception {
                                if (rpcNonceResult.isSuccessful()) {
                                    emitter.onSuccess(rpcNonceResult.getNonce());
                                } else {
                                    emitter.onError(new CustomThrowable(rpcNonceResult.getErrCode()));
                                }
                            }
                        });
                    }
                });

    }

    private String getSignedMessageSingle(ECKeyPair ecKeyPair, String from, String toAddress, BigDecimal amount, BigInteger gasPrice, BigInteger gasLimit, BigInteger nonce) throws RuntimeException {

        return  getSignedMessage(ecKeyPair, from, toAddress, amount, gasPrice, gasLimit, nonce);
    }

    private String getSignedMessage(ECKeyPair ecKeyPair, String from, String toAddress, BigDecimal amount, BigInteger gasPrice, BigInteger gasLimit, BigInteger nonce) throws RuntimeException {

        Credentials credentials = Credentials.create(ecKeyPair);

        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, toAddress, amount.toBigInteger(), "");

        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, NumberParserUtils.parseLong(NodeManager.getInstance().getChainId()), credentials);
        return Numeric.toHexString(signedMessage);
    }

    private String createSigned(ECKeyPair ecKeyPair, String signedData, String remark) {
        byte[] signedDataByte = Numeric.hexStringToByteArray(signedData);
        byte[] remarkByte = new byte[0];
        if (!TextUtils.isEmpty(remark)) {
            try {
                remarkByte = remark.getBytes(UTF_8);
            } catch (UnsupportedEncodingException e) {
                LogUtils.e(e.getMessage(),e.fillInStackTrace());
            }
        }
        byte[] message = new byte[signedDataByte.length + remarkByte.length];
        System.arraycopy(signedDataByte, 0, message, 0, signedDataByte.length);
        System.arraycopy(remarkByte, 0, message, signedDataByte.length, remarkByte.length);

        byte[] messageHash = Hash.sha3(message);

        //签名 Sign.signMessage(message, ecKeyPair, true) 和  Sign.signMessage(messageHash, ecKeyPair, false) 等效
        Sign.SignatureData signatureData = Sign.signMessage(messageHash, ecKeyPair, false);

        byte[] signByte = SignCodeUtils.encode(signatureData);

        //报文中sign数据， signHex等于下面打印的值
        return Numeric.toHexString(signByte);
    }

    private String getSignedData(ECKeyPair ecKeyPair, String data) {

        byte[] message = new byte[0];
        try {
            message = data.getBytes(UTF_8);
        } catch (UnsupportedEncodingException e) {
            LogUtils.e(e.getMessage(),e.fillInStackTrace());
            return null;
        }
        byte[] messageHash = Hash.sha3(message);

        //签名 Sign.signMessage(message, ecKeyPair, true) 和  Sign.signMessage(messageHash, ecKeyPair, false) 等效
        Sign.SignatureData signatureData = Sign.signMessage(messageHash, ecKeyPair, false);

        byte[] signByte = SignCodeUtils.encode(signatureData);

        //报文中sign数据， signHex等于下面打印的值
        return Numeric.toHexString(signByte);
    }
/*
    private Single<String> signedTransaction(final PlatOnContract platOnContract, Credentials credentials, final BigInteger gasPrice, final BigInteger gasLimit, final String to,
                                             final String data, final BigInteger value, final String nonce) throws IOException {

        return Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return ((RawTransactionManager) platOnContract.getTransactionManager()).signedTransaction(RawTransaction.createTransaction(
                        BigIntegerUtil.toBigInteger(nonce),
                        gasPrice,
                        gasLimit,
                        to,
                        value,
                        data));
            }
        });
    }

*/
private String signedDeployContractTransaction( Credentials credentials, final BigInteger gasPrice, final BigInteger gasLimit,
                                         final String data, final BigInteger value, final BigInteger nonce) throws IOException {
    System.out.println("TransactionManager signedDeployContractTransaction nonce="+nonce);
    System.out.println("TransactionManager signedDeployContractTransaction gasPrice="+gasPrice);
    System.out.println("TransactionManager signedDeployContractTransaction gasLimit="+gasLimit);
    System.out.println("TransactionManager signedDeployContractTransaction value="+value);
    System.out.println("TransactionManager signedDeployContractTransaction data="+data);
    RawTransaction rawTransaction = RawTransaction.createContractTransaction(
            nonce,
            gasPrice,
            gasLimit,
            value,
            data);
    System.out.println("TransactionManager signedDeployContractTransaction getChainId="+ NumberParserUtils.parseLong(NodeManager.getInstance().getChainId()));
    byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, NumberParserUtils.parseLong(NodeManager.getInstance().getChainId()), credentials);
    System.out.println("TransactionManager signedDeployContractTransaction Numeric.toHexString(signedMessage) ="+Numeric.toHexString(signedMessage));
    return Numeric.toHexString(signedMessage);

}
    /**
     * 获取交易hash，交易读超时的话就使用本地hash
     *
     * @param hexValue
     * @return
     */
    public RPCTransactionResult getTransactionResult(String hexValue) {
        RPCTransactionResult rpcTransactionResult = null;
        try {
            String hash = Web3jManager.getInstance().getWeb3j().platonSendRawTransaction(hexValue).send().getTransactionHash();
            rpcTransactionResult = new RPCTransactionResult(RPCErrorCode.SUCCESS, hash);
        } catch (SocketTimeoutException e) {
            LogUtils.e(e.getMessage(),e.fillInStackTrace());
            rpcTransactionResult = new RPCTransactionResult(RPCErrorCode.SOCKET_TIMEOUT, Hash.sha3(hexValue));
        } catch (ClientConnectionException e) {
            rpcTransactionResult = new RPCTransactionResult(RPCErrorCode.CONNECT_TIMEOUT);
        } catch (IOException e) {
            LogUtils.e(e.getMessage(),e.fillInStackTrace());
        }
        return rpcTransactionResult;
    }


    public String sendContractTransaction(String signedMessage) {

        try {
            PlatonSendTransaction transaction = Web3jManager.getInstance().getWeb3j().platonSendRawTransaction(signedMessage).send();
            return transaction.getTransactionHash();
        } catch (IOException e) {
            LogUtils.e(e.getMessage(),e.fillInStackTrace());
        }

        return null;
    }

    public PlatonSendTransaction sendTransactionReturnPlatonSendTransaction(String signedMessage) {
        try {
            return Web3jManager.getInstance().getWeb3j().platonSendRawTransaction(signedMessage).send();
        } catch (Exception e) {
            LogUtils.e(e.getMessage(),e.fillInStackTrace());
        }
        return null;
    }

    public String signTransaction(Credentials credentials, String data, String toAddress, BigDecimal amount, BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit) {

        try {
            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, toAddress, amount.toBigInteger(),
                    data);

            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, NumberParserUtils.parseLong(NodeManager.getInstance().getChainId()), credentials);

            return Numeric.toHexString(signedMessage);
        } catch (Exception e) {
            LogUtils.e(e.getMessage(),e.fillInStackTrace());
        }

        return null;
    }

    String  signedStr = "";

   // public Single<Transaction> sendTransferTransaction(final ECKeyPair ecKeyPair, final String fromAddress, final String toAddress, final String walletName, final BigDecimal transferAmount, final BigDecimal feeAmount, BigInteger gasPrice, BigInteger gasLimit, BigInteger nonce, final String remark) throws RuntimeException{


    public Single<Transaction> sendTransferTransaction(final String password, final Wallet walletEntity, final TransactionRecordEntity transactionRecordEntity, final String feeAmount, final BigInteger gasPrice, final BigInteger gasLimit, final BigInteger nonce, final String remark) throws Exception {
          final ECKeyPair ecKeyPair = WalletManager.getInstance().exportECKeyPairByPassword(password, walletEntity);
        final String fromAddress = transactionRecordEntity.getFrom();
        final String toAddress = transactionRecordEntity.getTo();
        final String walletName = walletEntity.getName();
        final BigDecimal transferAmount = Convert.toVon(transactionRecordEntity.getValue(), Convert.Unit.LAT);
        final BigDecimal fee = Convert.toVon(feeAmount, Convert.Unit.LAT);
        System.out.println("fromAddress = "+fromAddress);
        System.out.println("toAddress = "+toAddress);
        System.out.println("walletName = "+walletName);
        System.out.println("transferAmount = "+transferAmount);
        System.out.println("fee = "+fee);
        System.out.println("gasPrice = "+gasPrice);
        System.out.println("gasLimit = "+gasLimit);
        System.out.println("nonce = "+nonce);
        signedStr = getSignedMessageSingle(ecKeyPair, fromAddress, toAddress, transferAmount, gasPrice, gasLimit, nonce);
        Single<String> single = Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return signedStr;
            }
        });
        return single
                .flatMap(new Function<String, SingleSource<Transaction>>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public SingleSource<Transaction> apply(String signedMessage) throws Exception {
                        return submitTransaction(createSigned(ecKeyPair, signedMessage, remark), signedMessage, remark)
                                .flatMap(new Function<RPCTransactionResult, SingleSource<RPCTransactionResult>>() {
                                    @Override
                                    public SingleSource<RPCTransactionResult> apply(RPCTransactionResult transactionResult) throws Exception {
                                        return createRPCTransactionResult(transactionResult);
                                    }
                                })
                                .map(new Function<RPCTransactionResult, Transaction>() {
                                    @Override
                                    public Transaction apply(RPCTransactionResult rpcTransactionResult) throws Exception {
                                        System.out.println("TransactionManager  hash = "+rpcTransactionResult.getHash());
                                        return new Transaction.Builder()
                                                .hash(rpcTransactionResult.getHash())
                                                .from(fromAddress)
                                                .to(toAddress)
                                                .senderWalletName(walletName)
                                                .value(transferAmount.toPlainString())
                                                .chainId(NodeManager.getInstance().getChainId())
                                                .txType(String.valueOf(TransactionType.TRANSFER.getTxTypeValue()))
                                                .timestamp(System.currentTimeMillis())
                                                .txReceiptStatus(TransactionStatus.PENDING.ordinal())
                                                .actualTxCost(fee.toPlainString())
                                                .remark(remark)
                                                .build();
                                    }
                                }).filter(new Predicate<Transaction>() {
                                    @Override
                                    public boolean test(Transaction transaction) throws Exception {
                                        return TransactionDao.insertTransaction(transaction.toTransactionEntity());
                                    }
                                })
                                .toSingle()
                                .doOnSuccess(new Consumer<Transaction>() {
                                    @Override
                                    public void accept(Transaction transaction) throws Exception {
                                        EventPublisher.getInstance().sendUpdateTransactionEvent(transaction);
                                        putPendingTransaction(transaction.getFrom(), transaction.getTimestamp());
                                        // putTask(transaction.getHash(), getTransactionByLoop(transaction));
                                    }
                                });
                    }
                });
    }
    public Single<String> deployContractTransaction( Long chainId, Web3j mWeb3j, String password, Wallet walletEntity, BigInteger gasPrice, BigInteger gasLimit, String encodeData) throws Exception {
         return Single.create(new SingleOnSubscribe<String>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void subscribe(SingleEmitter<String> emitter) throws Exception {
                    Web3ClientVersion web3ClientVersion = mWeb3j.web3ClientVersion().send();
                    String clientVersion = web3ClientVersion.getWeb3ClientVersion();
                    System.out.println("sendDeployContractTransaction clientVersion="+clientVersion);

                    ECKeyPair ecKeyPair = WalletManager.getInstance().exportECKeyPairByPassword(password, walletEntity);
                    Credentials credentials = Credentials.create(ecKeyPair);
                    System.out.println("TransactionManager deployContractTransaction 111111111111111111");
                    RawTransactionManager transactionManager = new RawTransactionManager(mWeb3j, credentials, chainId);
                    System.out.println("TransactionManager deployContractTransaction 22222222222222222");
                    System.out.println("TransactionManager deployContractTransaction credentials.getAddress() = "+credentials.getAddress());
                    System.out.println("TransactionManager deployContractTransaction 333333333333333 chainId="+chainId);
                    System.out.println("TransactionManager deployContractTransaction  DefaultBlockParameterName.PENDING= "+ DefaultBlockParameterName.PENDING);
                     PlatonGetTransactionCount ethGetTransactionCount = (PlatonGetTransactionCount)mWeb3j.platonGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.PENDING).send();
                    System.out.println("TransactionManager deployContractTransaction 333333333333333");
                    System.out.println("TransactionManager deployContractTransaction ethGetTransactionCount="+ethGetTransactionCount);
                    System.out.println("TransactionManager deployContractTransaction ethGetTransactionCount11111="+ethGetTransactionCount.getTransactionCount());

                   /// System.out.println("TransactionManager deployContractTransaction ethGetTransactionCount.getTransactionCount().intValue() = "+ethGetTransactionCount.getTransactionCount().intValue());
                     if (ethGetTransactionCount.getTransactionCount().intValue() == 0) {
                         System.out.println("TransactionManager deployContractTransaction 4444444444444444444444444");
                        ethGetTransactionCount = (PlatonGetTransactionCount)mWeb3j.platonGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).send();
                    }
                    System.out.println("TransactionManager deployContractTransaction 55555555555555555555");
                    BigInteger nonce =  ethGetTransactionCount.getTransactionCount();
                    System.out.println("nonce="+nonce);
                    System.out.println("gasPrice="+gasPrice);
                    System.out.println("gasLimit="+gasLimit);
                    System.out.println("encodeData = " + encodeData);
                    String data = Numeric.prependHexPrefix(encodeData);
                    System.out.println("data = " + data);
                    RawTransaction rawTransaction = RawTransaction.createTransaction(nonce , gasPrice, gasLimit ,"",  BigInteger.ZERO, data);
                    PlatonSendTransaction platonSendTransaction = transactionManager.signAndSend(rawTransaction);
                    String hash = platonSendTransaction.getTransactionHash();
                    System.out.println("hash11122hash = "+hash);
                    emitter.onSuccess(hash);
                }
         });

    }

    public Single<String> sendContractTransaction( Long chainId, Web3j mWeb3j, String password, Wallet walletEntity, BigInteger gasPrice, BigInteger gasLimit, String contractAddress, String encodeData) throws Exception {
        return Single.create(new SingleOnSubscribe<String>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void subscribe(SingleEmitter<String> emitter) throws Exception {
                System.out.println("TransactionManager ContractTransaction 1");
                ECKeyPair ecKeyPair = WalletManager.getInstance().exportECKeyPairByPassword(password, walletEntity);

                System.out.println("TransactionManager ContractTransaction 2");
                Credentials credentials = Credentials.create(ecKeyPair);

                System.out.println("TransactionManager ContractTransaction 3");
                RawTransactionManager transactionManager = new RawTransactionManager(mWeb3j, credentials, chainId);

                System.out.println("TransactionManager ContractTransaction 4");
                System.out.println("TransactionManager ContractTransaction 4credentials.getAddress(chainId) = "+credentials.getAddress(chainId));
                PlatonGetTransactionCount ethGetTransactionCount = (PlatonGetTransactionCount)mWeb3j.platonGetTransactionCount(credentials.getAddress(chainId), DefaultBlockParameterName.PENDING).send();

                System.out.println("TransactionManager ContractTransaction 5");
                System.out.println("TransactionManager ContractTransaction ethGetTransactionCount = "+ethGetTransactionCount.getTransactionCount().intValue());
                if (ethGetTransactionCount.getTransactionCount().intValue() == 0) {
                    ethGetTransactionCount = (PlatonGetTransactionCount) mWeb3j.platonGetTransactionCount(credentials.getAddress(chainId), DefaultBlockParameterName.LATEST).send();
                }

                System.out.println("TransactionManager ContractTransaction 6");
                BigInteger nonce =  ethGetTransactionCount.getTransactionCount();

                System.out.println("TransactionManager ContractTransaction 7");
                RawTransaction rawTransaction2 = RawTransaction.createTransaction(nonce , new BigInteger("60000000000"), new BigInteger("2100000"), contractAddress, BigInteger.ZERO, encodeData);

                System.out.println("TransactionManager ContractTransaction 8");
                System.out.println("TransactionManager ContractTransaction 8 nonce = "+nonce);

                System.out.println("TransactionManager ContractTransaction 8 contractAddress = "+contractAddress);

                System.out.println("TransactionManager ContractTransaction 8 encodeData = "+encodeData);
                System.out.println("TransactionManager ContractTransaction 8 nonce = "+nonce);
                System.out.println("TransactionManager ContractTransaction 8 nonce = "+nonce);


                PlatonSendTransaction platonSendTransaction = transactionManager.signAndSend(rawTransaction2);

                System.out.println("TransactionManager ContractTransaction 9");
                String hash = platonSendTransaction.getTransactionHash();

                System.out.println("hash11122hash = "+hash);
                emitter.onSuccess(hash);

            }
        });

    }
    /*public Single<RPCTransactionResult> sendContractTransaction(PlatOnContract platOnContract, Credentials credentials, PlatOnFunction platOnFunction, String nonce) throws IOException {




        return signedTransaction(platOnContract, credentials, platOnFunction.getGasProvider().getGasPrice(), platOnFunction.getGasProvider().getGasLimit(), platOnContract.getContractAddress(), platOnFunction.getEncodeData(), BigInteger.ZERO, nonce)
                .flatMap(new Function<String, SingleSource<RPCTransactionResult>>() {
                    @Override
                    public SingleSource<RPCTransactionResult> apply(String signedMessage) throws Exception {
                        return submitTransaction(createSigned(credentials.getEcKeyPair(), signedMessage, ""), signedMessage, "");
                    }
                });
    }*/
    /**
     * 通过轮询获取普通钱包的交易
     */
    /*public Disposable getTransactionByLoop(final Transaction transaction) {

        Disposable disposable = mDisposableMap.get(transaction.getHash());

        return disposable != null ? disposable : Flowable
                .interval(Constants.Common.TRANSACTION_STATUS_LOOP_TIME, TimeUnit.MILLISECONDS)
                .map(new Function<Long, Transaction>() {
                    @Override
                    public Transaction apply(Long aLong) throws Exception {
                        Transaction tempTransaction = transaction.clone();
                        //如果pending时间超过4小时，则删除
                        if (System.currentTimeMillis() - transaction.getTimestamp() >= NumberParserUtils.parseLong(AppConfigManager.getInstance().getTimeout())) {
                            tempTransaction.setTxReceiptStatus(TransactionStatus.TIMEOUT.ordinal());
                        } else {
                            TransactionReceipt transactionReceipt = getTransactionReceipt(tempTransaction.getHash());
                            tempTransaction.setTxReceiptStatus(transactionReceipt.getStatus());
                            tempTransaction.setTotalReward(transactionReceipt.getTotalReward());
                            tempTransaction.setBlockNumber(transactionReceipt.getBlockNumber());
                            tempTransaction.setTimestamp(transactionReceipt.getTimestamp() == null ? 0 : transactionReceipt.getTimestamp());
                            tempTransaction.setActualTxCost(TextUtils.isEmpty(transactionReceipt.getActualTxCost()) ? "0" : transactionReceipt.getActualTxCost());
                            if (tempTransaction.getTxType() == TransactionType.UNDELEGATE) {
                                tempTransaction.setValue(BigDecimalUtil.add(transaction.getUnDelegation(), transactionReceipt.getTotalReward()).toPlainString());
                            }
                        }
                        return tempTransaction;
                    }
                })
                .takeUntil(new Predicate<Transaction>() {
                    @Override
                    public boolean test(Transaction transaction) throws Exception {
                        return transaction.getTxReceiptStatus() != TransactionStatus.PENDING;
                    }
                })
                .filter(new Predicate<Transaction>() {
                    @Override
                    public boolean test(Transaction transaction) throws Exception {
                        return transaction.getTxReceiptStatus() != TransactionStatus.PENDING;
                    }
                })
                .doOnNext(new Consumer<Transaction>() {
                    @Override
                    public void accept(Transaction transaction) throws Exception {
                        removeTaskByHash(transaction.getHash());
                        removePendingTransaction(transaction.getFrom());
                        //更新数据库中交易的状态
                        TransactionDao.insertTransaction(transaction.toTransactionEntity());
                        //如果是完成了的交易，则从数据库中删除
                        if (transaction.getTxReceiptStatus() == TransactionStatus.SUCCESSED || transaction.getTxReceiptStatus() == TransactionStatus.FAILED) {
                            TransactionDao.deleteTransaction(transaction.getHash());
                        }
                        EventPublisher.getInstance().sendUpdateTransactionEvent(transaction);
                    }
                })
                .toObservable()
                .subscribeOn(Schedulers.io())
                .subscribe();

    }
*/
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public TransactionReceipt getTransactionReceipt(String hash) {
        return ServerUtils
            .getCommonApi()
            .getTransactionsStatus(ApiRequestBody.newBuilder()
                    .put("hash", Arrays.asList(hash))
                    .build())
            .filter(new Predicate<Response<ApiResponse<List<TransactionReceipt>>>>() {
                @Override
                public boolean test(Response<ApiResponse<List<TransactionReceipt>>> apiResponseResponse) throws Exception {
                    return apiResponseResponse != null && apiResponseResponse.isSuccessful();
                }
            })
            .filter(new Predicate<Response<ApiResponse<List<TransactionReceipt>>>>() {
                @Override
                public boolean test(Response<ApiResponse<List<TransactionReceipt>>> apiResponseResponse) throws Exception {

                    System.out.println("getTransactionReceipt apiResponseResponse "+apiResponseResponse);
                    System.out.println("getTransactionReceipt apiResponseResponse.body().getData() "+apiResponseResponse.body());
                    List<TransactionReceipt> transactionReceiptList = apiResponseResponse.body().getData();
                    return transactionReceiptList != null && !transactionReceiptList.isEmpty();
                }
            })
            .map(new Function<Response<ApiResponse<List<TransactionReceipt>>>, TransactionReceipt>() {
                @Override
                public TransactionReceipt apply(Response<ApiResponse<List<TransactionReceipt>>> apiResponseResponse) throws Exception {
                    return apiResponseResponse.body().getData().get(0);
                }
            })
            .defaultIfEmpty(new TransactionReceipt(TransactionStatus.PENDING.ordinal(), hash))
            .onErrorReturnItem(new TransactionReceipt(TransactionStatus.PENDING.ordinal(), hash))
            .toSingle()
            .blockingGet();

    }


    private String buildPendingMapKey(String from) {
        return from.toLowerCase() + "-" + NodeManager.getInstance().getChainId();
    }

    private Single<RPCTransactionResult> createRPCTransactionResult(final RPCTransactionResult rpcTransactionResult) {
        return Single.create(new SingleOnSubscribe<RPCTransactionResult>() {
            @Override
            public void subscribe(SingleEmitter<RPCTransactionResult> emitter) throws Exception {
                if (TextUtils.isEmpty(rpcTransactionResult.getHash())) {
                    emitter.onError(new CustomThrowable(rpcTransactionResult.getErrCode()));
                } else {
                    emitter.onSuccess(rpcTransactionResult);
                }
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Observable<GasProvider> getEstimateGas(Wallet mWallet) {
        System.out.println("getEstimateGas1111112222223344444332");
        return ServerUtils
                .getCommonApi()
                .estimateGas(ApiRequestBody.newBuilder()
                        .put("from", mWallet.getAddress())
                        .put("txType", FunctionType.TRANSFER)
                        .build())
                //.compose(getSingleSchedulerTransformer())
                .toFlowable()
                .flatMap(new Function<Response<ApiResponse<EstimateGasResult>>, Publisher<GasProvider>>(){
                    @Override
                    public Publisher<GasProvider> apply(Response<ApiResponse<EstimateGasResult>> apiResponseResponse) throws Exception {
                        System.out.println("getEstimateGas11111122222233333332");
                        assert apiResponseResponse.body() != null;
                        final EstimateGasResult gasProvider = apiResponseResponse.body().getData();
                        final GasProvider mGasProvider = gasProvider.getGasProvider();
                        return Single.create(new SingleOnSubscribe<GasProvider>() {
                            @Override
                            public void subscribe(SingleEmitter<GasProvider> emitter) throws Exception {
                                emitter.onSuccess(mGasProvider);
                            }
                        }).toFlowable();
                    }
                })
                .toObservable();
    }


}



////lato
    /*
     */


