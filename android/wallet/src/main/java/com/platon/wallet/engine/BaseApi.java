package com.platon.wallet.engine;


import com.platon.wallet.entity.AccountBalance;
import com.platon.wallet.entity.ClaimRewardRecord;
import com.platon.wallet.entity.DelegateInfo;
import com.platon.wallet.entity.DelegateNodeDetail;
import com.platon.wallet.entity.DelegationValue;
import com.platon.wallet.entity.EstimateGasResult;
import com.platon.wallet.entity.GasProvider;
import com.platon.wallet.entity.Transaction;
import com.platon.wallet.entity.TransactionReceipt;
import com.platon.wallet.entity.VerifyNode;
import com.platon.wallet.entity.VerifyNodeDetail;
import com.platon.wallet.entity.VersionInfo;
import com.platon.wallet.network.ApiRequestBody;
import com.platon.wallet.network.ApiResponse;
import com.platon.wallet.network.Test;
import com.platon.wallet.network.Test1;
import com.platon.wallet.network.TestResponse;
import java.util.List;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface BaseApi {

    /**
     * 获取版本信息
     *
     * @return
     */
    @POST("app/checkUpdate")
    Single<Response<ApiResponse<VersionInfo>>> getVersionInfo(@Body ApiRequestBody body);
    /**
     * 获取版本信息
     *
     * @return
     */
    @POST("app/checkUpdate")
    Single<Response<Test1>> getVersionInfo1(@Body ApiRequestBody body);
    /**
     * 获取交易记录
     *
     * @param body
     * @return
     */
    @POST("app/v0760/transaction/list")
    Single<Response<ApiResponse<List<Transaction>>>> getTransactionList(@Body ApiRequestBody body);



    /**
     * 获取交易记录
     *
     * @param body
     * @return
     */
    @POST("app/v0760/transaction/list")
    Single<Response<Test<Test1>>> getTransactionList1(@Body ApiRequestBody body);

    /**--------------v0.7.0---------------------*/

    /**
     * 获取验证节点列表
     */

    @POST("app/v0760/node/nodelist")
    Single<Response<ApiResponse<List<VerifyNode>>>> getVerifyNodeList();


    /**
     * 验证节点详情
     * nodeId   //节点id
     */

    @POST("app/v0760/node/nodeDetails")
    Single<Response<ApiResponse<VerifyNodeDetail>>> getNodeCandidateDetail(@Body ApiRequestBody body);


    /**
     * 获取我的委托列表
     *
     * @param body
     * @return
     */
    @POST("app/v0760/node/listDelegateGroupByAddr")
    Single<Response<ApiResponse<List<DelegateInfo>>>> getMyDelegateList(@Body ApiRequestBody body);


    /**
     * 获取委托详情数据
     *
     * @param "cid": "",                          //链Id
     *               "walletAddrs":"address1"            //地址列表
     *               "beginSequence":120,                //起始序号 (必填) 客户端首次进入页面时传-1，-1：代表最新记录
     *               "listSize":100,                     //列表大小 (必填)
     *               "direction":""                      //方向 (必填) new：朝最新记录方向, old：朝最旧记录方向,
     *               客户端首次进入页面时或者上拉时传old。客户端自动获取最新记录时传new。
     */

    @POST("app/v0760/node/delegateDetails")
    Single<Response<ApiResponse<DelegateNodeDetail>>> getDelegateDetailList(@Body ApiRequestBody body);


    /**
     * 获取委托记录的数据
     *
     * @param "cid": "",                          //链Id
     *               "beginSequence":120,                //起始序号 (必填) 客户端首次进入页面时传-1，-1：代表最新记录
     *               "listSize":100,                     //列表大小 (必填)
     *               "direction":""                      //方向 (必填) new：朝最新记录方向, old：朝最旧记录方向,
     *               客户端首次进入页面时或者上拉时传old。客户端自动获取最新记录时传new。
     *               "type":"all",                       //类型
     *               all —— 全部
     *               redeem —— 赎回
     *               delegate —— 委托
     *               "walletAddrs":[                     //地址列表
     *               "address1",
     *               "address2"
     *               ]
     */

    @POST("app/v0760/transaction/delegateRecord")
    Single<Response<ApiResponse<List<Transaction>>>> getDelegateRecordList(@Body ApiRequestBody body);


    /**
     * 批量获取账户余额
     * "addrs":[   //地址列表
     * "address1",
     * "address2"]
     */
    @POST("app/v0760/account/getBalance")
    Single<Response<ApiResponse<List<AccountBalance>>>> getAccountBalance(@Body ApiRequestBody body);


    /**
     * 获取委托金额
     * "addr":"",                             //委托的地址
     * "nodeId":"",                          //节点id
     */
    @POST("app/v0760/node/getDelegationValue")
    Single<Response<ApiResponse<DelegationValue>>> getDelegationValue(@Body ApiRequestBody body);

    /**
     * 批量查询交易记录状态
     *
     * @param body
     * @return
     */
    @POST("app/v0760/transaction/getTransactionsStatus")
    Single<Response<ApiResponse<List<TransactionReceipt>>>> getTransactionsStatus(@Body ApiRequestBody body);

    /**
     * 获取app 配置
     *
     * @return
     */
    @GET("config/config.json")
    Single<String> getAppConfig();

    /**
     * 查询领取奖励记录
     *
     * @return
     */
    @POST("app/v0760/transaction/getRewardTransactions")
    Single<Response<ApiResponse<List<ClaimRewardRecord>>>> getRewardTransactions(@Body ApiRequestBody body);

    /**
     * 提交一个签名后的交易
     *
     * @param body
     * @return
     */
    @POST("app/v0760/transaction/submitSignedTransaction")
    Single<Response<ApiResponse<String>>> submitSignedTransaction(@Body ApiRequestBody body);

    /**
     * 估算gas
     *
     * @param body
     * @return
     */
    @POST("app/v0760/transaction/estimateGas")
    Single<Response<ApiResponse<EstimateGasResult>>> estimateGas(@Body ApiRequestBody body);
}
