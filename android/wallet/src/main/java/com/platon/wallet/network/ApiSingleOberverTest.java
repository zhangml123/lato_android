package com.platon.wallet.network;

import com.alibaba.fastjson.JSON;

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
public abstract class ApiSingleOberverTest extends AtomicReference<Disposable> implements SingleObserver<Response<Test1>> {
    public ApiSingleOberverTest() {

    }

    @Override
    public void onError(Throwable e) {
        System.out.println("ApiSignalObserver222222 onError "+e.getMessage());

    }

    @Override
    public void onSubscribe(Disposable d) {
        DisposableHelper.setOnce(this, d);
    }

    @Override
    public void onSuccess(Response<Test1> value) {
       Test1 test = value.body();
        assert test != null;
        System.out.println("ApiSignalObserver222222 onSuccess test.aa" +test.getAa());
       // System.out.println("ApiSignalObserver222222 onSuccess " +value.body());
       // System.out.println("ApiSignalObserver222222 onSuccess " +value.isSuccessful());
        //System.out.println("ApiSignalObserver222222 onSuccess " +value.message());
       // System.out.println("ApiSignalObserver222222 onSuccess " +value.toString());
        //onApiSuccess();

    }
    public abstract void onApiSuccess();

}
