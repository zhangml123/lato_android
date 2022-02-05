package com.platon.wallet.network;

import android.content.Context;

import androidx.annotation.StringRes;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;




public class ApiResponse<T>{
    public ApiResponse() {}
    private ApiErrorCode result;
    public T data;
    public String errMsg;
    public int code;
    public String getErrMsg(){
        return this.errMsg;
    }
    public int getCode(){
        return this.code;
    }
    private static Context sContext;
    public static void init(Context context) {
        System.out.println("ApiResponse111111111111111 init");
        sContext = context;
    }

    private String getString(@StringRes int resId) {
        System.out.println("ApiResponse111111111111111 getString");
        return sContext.getString(resId);
    }


    public ApiResponse(ApiErrorCode apiErrorCode, T d) {
        result = apiErrorCode;
        data = d;
    }

    public ApiResponse(ApiErrorCode apiErrorCode) {
        System.out.println("ApiResponse111111111111111 ApiResponse");
        result = apiErrorCode;
        if (apiErrorCode != null) {
            errMsg = getString(apiErrorCode.descId);
        }
    }

    public ApiResponse(ApiErrorCode apiErrorCode, Throwable throwable) {
        System.out.println("ApiResponse111111111111111 ApiResponse2222");
        result = apiErrorCode;
        if (apiErrorCode != null) {
            System.out.println("ApiResponse111111111111111 ApiResponse2222 apiErrorCode = "+apiErrorCode);
            errMsg = getString(apiErrorCode.descId);
        }
    }

    @JSONField(serialize = false)
    public ApiErrorCode getResult() {
        System.out.println("ApiResponse111111111111111 getResult");
        if(result !=null){
            return result;
        }else{
            return ApiErrorCode.fromCode(code);
        }

    }

    @JSONField(deserialize = false)
    public void setResult(ApiErrorCode result) {
        System.out.println("ApiResponse111111111111111 setResult");
        this.result = result;
    }

    @JSONField(name = "code")
    public void setErrorCode(int code) {
        System.out.println("ApiResponse111111111111111 setResult");
        this.result = ApiErrorCode.fromCode(code);
    }

    @JSONField(name = "code")
    public int getErrorCode() {
        return result.code;
    }

    public T getData() {
        return data;
    }

    public void setData(T  data) {
        System.out.println("ApiResponse111111111111111 setData");
        this.data = data;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getErrMsg(Context context) {
        return context.getString(result.descId);
    }
}
