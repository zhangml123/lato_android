package org.platon.lato.service.websocket;

import android.annotation.SuppressLint;
import android.content.Context;

import org.platon.lato.util.RxSchedulerUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import static org.platon.lato.service.websocket.WebSocketInfo.CONNECT;
import static org.platon.lato.service.websocket.WebSocketInfo.RECEIVE_STRING_MSG;
import static org.platon.lato.service.websocket.WebSocketInfo.RECONNECT;

public class WebSocketManager {
    //private final String URL =  "ws://199.247.27.165:8080";

    private final String URL =  "ws://199.247.27.165:808";//dev
    private RxWebSocket rxWebSocket;
    private  WebSocketManager(){
    }
    private static class InstanceHolder {
        private static volatile WebSocketManager INSTANCE = new WebSocketManager();
    }
    public static WebSocketManager getInstance() {
        return WebSocketManager.InstanceHolder.INSTANCE;
    }
    public WebSocketListener listener;

    public RxWebSocket getRxWebSocket(){
        return rxWebSocket;
    }

    public void reConnect(Context context){
        this.rxWebSocket.closeAll();
        connect(context);
    }
    @SuppressLint("CheckResult")
    public void connect(Context context) {

        OkHttpClient mClient = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(3, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(3, TimeUnit.SECONDS)//设置连接超时时间
                .build();

        RxWebSocket rxWebSocket = new RxWebSocketBuilder(context)
                .isPrintLog(true)
                .reconnectInterval(5, TimeUnit.SECONDS)
                .client(mClient)
                .build();
        rxWebSocket.get(URL)
                .compose(RxSchedulerUtils.observableToMain())
                .subscribe(new Consumer<WebSocketInfo>() {
                    @Override
                    public void accept(WebSocketInfo webSocketInfo) throws Exception {

                        System.out.println("listener11111 ="+listener);
                        if(listener != null){
                            WebSocket websocket= webSocketInfo.getWebSocket();
                            int actionType = webSocketInfo.getActionType();
                            if(actionType == CONNECT || actionType == RECONNECT){
                                listener.onOpen(websocket,null);
                            }else if(actionType == RECEIVE_STRING_MSG){
                                String json = webSocketInfo.getStringMsg();
                                listener.onMessage(websocket, json);
                                System.out.println("json111 = "+json);
                            }

                        }

                    }
                });

        this.rxWebSocket = rxWebSocket;
    }
    @SuppressLint("CheckResult")
    public Observable<Boolean> asyncSend(String text){
       return this.rxWebSocket.asyncSend(URL, text)
                .compose(RxSchedulerUtils.observableToMain());
    }
    @SuppressLint("CheckResult")
    public void setAddress(String address){
        this.rxWebSocket.asyncSend(URL, "{\"type\":\"set_address\",\"address\":\""+address+"\"}")
            .compose(RxSchedulerUtils.observableToMain())
            .subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean isSuccess) throws Exception {
                    if(isSuccess) {
                        //发送成功
                    } else {
                        //发送失败
                    }
                }
            });
    }
    public void setWebSocketListener(WebSocketListener listener){
        this.listener = listener;
    }

}
