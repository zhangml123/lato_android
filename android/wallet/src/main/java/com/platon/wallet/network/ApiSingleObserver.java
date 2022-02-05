package com.platon.wallet.network;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.ResponseBody;
import retrofit2.Response;


/**
 * @author ziv
 */
public abstract class ApiSingleObserver<T> extends AtomicReference<Disposable> implements SingleObserver<Response<ApiResponse<T>>>, Disposable {

    private static final String TAG = "ApiService/ApiSingleObserver";
    private static final long serialVersionUID = 6117901259965613041L;

    private ApiCallback callback;

    public ApiSingleObserver() {
        this.callback = new ApiCallback<T>() {
            @Override
            public void onSuccess(T result) {
                System.out.println("ApiSingleObserver ApiCallback onSuccess");
                System.out.println("ApiSingleObserver ApiCallback onSuccess result = "+result);
                onApiSuccess(result);
            }

            @Override
            public void onFailure(ApiResponse errorResponse) {
                onApiFailure(errorResponse);
            }
        };
    }

    @Override
    public void onError(Throwable e) {
       try {
            if (!NetConnectivity.getConnectivityManager().isConnected()) {
                callback.onFailure(new ApiResponse(ApiErrorCode.NETWORK_ERROR, e));
            } else {
                callback.onFailure(new ApiResponse(ApiErrorCode.SYSTEM_ERROR, e));
            }
            e.printStackTrace();
        } catch (Throwable ex) {
            callback.onFailure(new ApiResponse(ApiErrorCode.SYSTEM_ERROR, e));
            Exceptions.throwIfFatal(ex);
            RxJavaPlugins.onError(new CompositeException(e, ex));
        }
    }

    @Override
    public void onSubscribe(Disposable d) {
        DisposableHelper.setOnce(this, d);
    }

    @Override
    public void onSuccess(Response<ApiResponse<T>> value) {
        try {
           Response<ApiResponse<T>> response = value;
            if (response.isSuccessful()) {
                ApiResponse apiResponse = response.body();
                System.out.println("ApiSignleObserver11111111111111 111111  apiResponse ="+apiResponse);
                if (apiResponse != null && apiResponse.getResult() == ApiErrorCode.SUCCESS) {
                    System.out.println("ApiSignleObserver11111111111111 222222");
                    Object data = apiResponse.getData();
                    System.out.println("ApiSignleObserver11111111111111 data="+data);
                    callback.onSuccess((T) data);
                } else if (apiResponse.getResult() != ApiErrorCode.SUCCESS) {
                    System.out.println("ApiSignleObserver11111111111111 333333");
                    callback.onFailure(new ApiResponse(apiResponse.getResult()));
                }
            } else {
                ResponseBody responseBody = response.errorBody();
                ApiResponse apiResponse = JSON.parseObject(responseBody.string(), ApiResponse.class);
                callback.onFailure(new ApiResponse(apiResponse.getResult()));
            }
        } catch (Throwable ex) {
            callback.onFailure(new ApiResponse(ApiErrorCode.SYSTEM_ERROR, ex));
            Exceptions.throwIfFatal(ex);
            RxJavaPlugins.onError(ex);
            ex.printStackTrace();
        }
    }

    public abstract void onApiSuccess(T t);

    public void onApiFailure(ApiResponse response) {
    }
    @Override
    public void dispose() {
        DisposableHelper.dispose(this);
    }
    @Override
    public boolean isDisposed() {
        return get() == DisposableHelper.DISPOSED;
    }
}
