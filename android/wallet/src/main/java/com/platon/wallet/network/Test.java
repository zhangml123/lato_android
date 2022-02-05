
package com.platon.wallet.network;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.support.retrofit.Retrofit2ConverterFactory;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class Test<T>  {
    private T data;
    private ApiErrorCode result;
    private String errMsg;
    private int code;
    private static Context sContext;

    public static void init(Context context) {
        System.out.println("ApiResponse111111111111111 init");
        sContext = context;
    }

    private String getString(@StringRes int resId) {
        System.out.println("ApiResponse111111111111111 getString");
        return sContext.getString(resId);
    }

    public Test() {

    }
    public Test(ApiErrorCode apiErrorCode, T d) {
        result = apiErrorCode;
        data = d;
    }

    public Test(ApiErrorCode apiErrorCode) {
        System.out.println("ApiResponse111111111111111 ApiResponse");
        result = apiErrorCode;
        if (apiErrorCode != null) {
            errMsg = getString(apiErrorCode.descId);
        }
    }

    public Test(ApiErrorCode apiErrorCode, Throwable throwable) {
        System.out.println("ApiResponse111111111111111 ApiResponse2222");
        result = apiErrorCode;
        if (apiErrorCode != null) {
            errMsg = getString(apiErrorCode.descId);
        }
    }

    @JSONField(serialize = false)
    public ApiErrorCode getResult() {
        System.out.println("ApiResponse111111111111111 getResult");
        return result;
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
        System.out.println("ApiResponse111111111111111 getData");
        return data;
    }

    public void setData(T data) {
        System.out.println("ApiResponse111111111111111 setData");
        this.data = data;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
    public String getErrMsg() {
        return this.errMsg;
    }
    public int getCode() {
        return this.code;
    }
    public void setCode(int code) {
        this.code = code;
    }
}
