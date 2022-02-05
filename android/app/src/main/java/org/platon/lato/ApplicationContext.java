package org.platon.lato;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.platon.wallet.app.AppFramework;

import org.platon.lato.service.websocket.ConnectivityListener;
import org.platon.lato.service.websocket.RxWebSocket;
import org.platon.lato.service.websocket.RxWebSocketBuilder;
import org.platon.lato.service.websocket.WebSocketConnection;
import org.platon.lato.service.websocket.WebSocketInfo;
import org.platon.lato.service.websocket.WebSocketManager;
import org.platon.lato.util.RxSchedulerUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.OkHttpClient;


public class ApplicationContext extends Application {
    private static Context context;
    public static ApplicationContext getInstance(Context context) {
        return (ApplicationContext)context.getApplicationContext();
    }

    @SuppressLint("CheckResult")
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("ApplicationContext", "onCreate()");

        //初始化钱包
        context = this;
        AppFramework.getAppFramework().initAppFramework(context);
        WebSocketManager.getInstance().connect(context);



        SharedPreferences update = getSharedPreferences("UPDATE", MODE_PRIVATE);
        SharedPreferences.Editor editor = update.edit();
        editor.putBoolean("update", true);
        editor.apply();
    }
}
