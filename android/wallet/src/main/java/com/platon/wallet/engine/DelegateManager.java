package com.platon.wallet.engine;

import android.annotation.SuppressLint;
import android.text.TextUtils;


import com.platon.sdk.contracts.ppos.DelegateContract;
import com.platon.sdk.contracts.ppos.dto.enums.StakingAmountType;
import com.platon.sdk.utlis.NetworkParameters;
import com.platon.wallet.FunctionType;
import com.platon.wallet.PlatOnFunction;
import com.platon.wallet.app.CustomThrowable;
import com.platon.wallet.db.sqlite.TransactionDao;
import com.platon.wallet.entity.RPCTransactionResult;
import com.platon.wallet.entity.Transaction;
import com.platon.wallet.entity.TransactionStatus;
import com.platon.wallet.entity.Wallet;
import com.platon.wallet.event.EventPublisher;
import com.platon.wallet.utils.NumberParserUtils;
import com.platon.wallet.utils.RxUtils;

import org.web3j.abi.datatypes.BytesType;
import org.web3j.abi.datatypes.generated.Uint16;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.GasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;


public class DelegateManager {
    public static final String DELEGATE_CONTRACT_ADDRESS = "lat1zqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqzsjx8h7";
    private static class InstanceHolder {
        private static volatile DelegateManager INSTANCE = new DelegateManager();
    }

    public static DelegateManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public Single<String> delegate(Web3j mWeb3j, String amount, String nodeId, StakingAmountType stakingAmountType, GasProvider gasProvider,  String password, Wallet walletEntity ) {

        return Single
            .create(new SingleOnSubscribe<String>(){
                @SuppressLint("CheckResult")
                @Override
                public void subscribe(SingleEmitter<String> emitter) throws Exception {
                    System.out.println("send_delegate111111");
                    String chainId = NodeManager.getInstance().getChainId();
                    System.out.println("send_delegate33333");
                    PlatOnFunction platOnFunction = new PlatOnFunction(FunctionType.DELEGATE_FUNC_TYPE,
                        Arrays.asList(new Uint16(stakingAmountType.getValue())
                                , new BytesType(Numeric.hexStringToByteArray(nodeId))
                                , new Uint256(Convert.toVon(amount, Convert.Unit.LAT).toBigInteger())), gasProvider);
                    String encodeData = "0x" + platOnFunction.getEncodeData();

                    NetworkParameters.MainNetParams.setChainId(Long.parseLong(NodeManager.getInstance().getChainId()));
                     TransactionManager.getInstance().sendContractTransaction(NumberParserUtils.parseLong(chainId), mWeb3j,password, walletEntity, gasProvider.getGasPrice() ,gasProvider.getGasLimit(),DELEGATE_CONTRACT_ADDRESS,encodeData)
                         .compose(RxUtils.getSingleSchedulerTransformer())
                         .subscribe(new Consumer<String>() {
                             @Override
                             public void accept(String s) throws Exception {
                                 emitter.onSuccess(s);
                             }
                         });
                }
            });

    }

    private Single<RPCTransactionResult> createRPCTransactionResult(RPCTransactionResult rpcTransactionResult) {
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

    private Single<Transaction> insertTransaction(Credentials credentials, String hash, String to, String amount, String nodeId, String nodeName, String feeAmount, String transactionType) {

        return Single.just(new Transaction.Builder()
                .from(credentials.getAddress())
                .to(to)
                .timestamp(System.currentTimeMillis())
                .txType(transactionType)
                .value(Convert.toVon(amount, Convert.Unit.LAT).toBigInteger().toString())
                .actualTxCost(feeAmount)
                .unDelegation(Convert.toVon(amount, Convert.Unit.LAT).toBigInteger().toString())
                .nodeName(nodeName)
                .nodeId(nodeId)
                .chainId(NodeManager.getInstance().getChainId())
                .txReceiptStatus(TransactionStatus.PENDING.ordinal())
                .hash(hash)
                .remark("")
                .build())
                .doOnSuccess(new Consumer<Transaction>() {
                    @Override
                    public void accept(Transaction transaction) throws Exception {
                        EventPublisher.getInstance().sendUpdateTransactionEvent(transaction);
                    }
                })
                .filter(new Predicate<Transaction>() {
                    @Override
                    public boolean test(Transaction transaction) throws Exception {
                        return TransactionDao.insertTransaction(transaction.toTransactionEntity());
                    }
                })
                .doOnSuccess(new Consumer<Transaction>() {
                    @Override
                    public void accept(Transaction transaction) throws Exception {
                        TransactionManager.getInstance().putPendingTransaction(transaction.getFrom(), transaction.getTimestamp());
                       // TransactionManager.getInstance().putTask(transaction.getHash(), TransactionManager.getInstance().getTransactionByLoop(transaction));
                    }
                })
                .toSingle();
    }

    /**
     * 赎回委托
     *
     * @param nodeId
     * @param stakingBlockNum
     * @param amount
     * @param gasProvider
     * @return
     */
    public Single<String> withdrawDelegate(Web3j mWeb3j, String password, Wallet wallet,  String nodeId,  String stakingBlockNum, String amount,  GasProvider gasProvider) throws Exception {
        return Single
                .create(new SingleOnSubscribe<String>(){
                    @SuppressLint("CheckResult")
                    @Override
                    public void subscribe(SingleEmitter<String> emitter) throws Exception {
                        String chainId = NodeManager.getInstance().getChainId();
                        PlatOnFunction platOnFunction = new PlatOnFunction(FunctionType.WITHDREW_DELEGATE_FUNC_TYPE,
                                Arrays.asList(new Uint64(new BigInteger(stakingBlockNum))
                                        , new BytesType(Numeric.hexStringToByteArray(nodeId))
                                        , new Uint256(Convert.toVon(amount, Convert.Unit.LAT).toBigInteger())), gasProvider);
                        String encodeData = "0x" + platOnFunction.getEncodeData();

                        NetworkParameters.MainNetParams.setChainId(Long.parseLong(NodeManager.getInstance().getChainId()));
                        TransactionManager.getInstance().sendContractTransaction(NumberParserUtils.parseLong(chainId), mWeb3j, password, wallet, gasProvider.getGasPrice() ,gasProvider.getGasLimit(),DELEGATE_CONTRACT_ADDRESS,encodeData)
                                .compose(RxUtils.getSingleSchedulerTransformer())
                                .subscribe(new Consumer<String>() {
                                    @SuppressLint("CheckResult")
                                    @Override
                                    public void accept(String s) throws Exception {
                                        emitter.onSuccess(s);
                                    }
                                });

                    }
                });
}



    /**
     * 领取奖励
     *
     * @param gasProvider
     * @return
     */
    public Single<String> withdrawDelegateReward(Web3j mWeb3j, String password, Wallet wallet,  GasProvider gasProvider) {
        return Single
                .create(new SingleOnSubscribe<String>(){
                    @SuppressLint("CheckResult")
                    @Override
                    public void subscribe(SingleEmitter<String> emitter) throws Exception {
                        String chainId = NodeManager.getInstance().getChainId();
                       PlatOnFunction platOnFunction = new PlatOnFunction(FunctionType.WITHDRAW_DELEGATE_REWARD_FUNC_TYPE, gasProvider);
                        String encodeData = "0x" + platOnFunction.getEncodeData();

                        NetworkParameters.MainNetParams.setChainId(Long.parseLong(NodeManager.getInstance().getChainId()));
                        TransactionManager.getInstance().sendContractTransaction(NumberParserUtils.parseLong(chainId), mWeb3j, password, wallet, gasProvider.getGasPrice() ,gasProvider.getGasLimit(),DELEGATE_CONTRACT_ADDRESS,encodeData)
                                .compose(RxUtils.getSingleSchedulerTransformer())
                                .subscribe(new Consumer<String>() {
                                    @SuppressLint("CheckResult")
                                    @Override
                                    public void accept(String s) throws Exception {
                                        emitter.onSuccess(s);
                                    }
                                });

                    }
                });
        
    }

}
