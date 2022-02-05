package org.platon.lato.service.websocket;

import android.content.Context;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Timed;
import okhttp3.OkHttpClient;
import okio.ByteString;
import io.reactivex.Observable;

public class RxWebSocket implements WebSocketWorker {
    private Context mContext;
    /**
     * 是否打印Log
     */
    private boolean mIsPrintLog;
    /**
     * Log代理对象
     */
    private Logger.LogDelegate mLogDelegate;
    /**
     * 支持外部传入OkHttpClient
     */
    private OkHttpClient mClient;
    /**
     * 支持SSL
     */
    private SSLSocketFactory mSslSocketFactory;
    private X509TrustManager mTrustManager;
    /**
     * 重连间隔时间
     */
    private long mReconnectInterval;
    /**
     * 重连间隔时间的单位
     */
    private TimeUnit mReconnectIntervalTimeUnit;
    /**
     * 具体干活的实现类
     */
    private WebSocketWorker mWorkerImpl;

    private RxWebSocket() {
    }

    RxWebSocket(RxWebSocketBuilder builder) {
        this.mContext = builder.mContext;
        this.mIsPrintLog = builder.mIsPrintLog;
        this.mLogDelegate = builder.mLogDelegate;
        this.mClient = builder.mClient == null ? new OkHttpClient() : builder.mClient;
        this.mSslSocketFactory = builder.mSslSocketFactory;
        this.mTrustManager = builder.mTrustManager;
        this.mReconnectInterval = builder.mReconnectInterval == 0 ? 1 : builder.mReconnectInterval;
        this.mReconnectIntervalTimeUnit = builder.mReconnectIntervalTimeUnit == null ? TimeUnit.SECONDS : builder.mReconnectIntervalTimeUnit;
        setup();
    }

    /**
     * 开始配置
     */
    private void setup() {
        this.mWorkerImpl = new WebSocketWorkerImpl(
                this.mContext,
                this.mIsPrintLog,
                this.mLogDelegate,
                this.mClient,
                this.mSslSocketFactory,
                this.mTrustManager,
                this.mReconnectInterval,
                this.mReconnectIntervalTimeUnit);
    }

    @Override
    public Observable<WebSocketInfo> get(String url) {
        return this.mWorkerImpl.get(url);
    }

    @Override
    public Observable<WebSocketInfo> get(String url, long timeout, TimeUnit timeUnit) {
        return this.mWorkerImpl.get(url, timeout, timeUnit);
    }

    @Override
    public Observable<Boolean> send(String url, String msg) {
        return this.mWorkerImpl.send(url, msg);
    }

    @Override
    public Observable<Boolean> send(String url, ByteString byteString) {
        return this.mWorkerImpl.send(url, byteString);
    }

    @Override
    public Observable<Boolean> asyncSend(String url, String msg) {
        return this.mWorkerImpl.asyncSend(url, msg);
    }

    @Override
    public Observable<Boolean> asyncSend(String url, ByteString byteString) {
        return this.mWorkerImpl.asyncSend(url, byteString);
    }

    @Override
    public Observable<Boolean> close(String url) {
        return this.mWorkerImpl.close(url);
    }

    @Override
    public boolean closeNow(String url) {
        return this.mWorkerImpl.closeNow(url);
    }

    @Override
    public Observable<List<Boolean>> closeAll() {
        return this.mWorkerImpl.closeAll();
    }

    @Override
    public void closeAllNow() {

    }
    public Observable<Boolean> heartBeat(String url, int period, TimeUnit unit) {

        return Observable
                .interval(period, unit)
                //timestamp操作符，给每个事件加一个时间戳
                .timestamp()
                .retry()
                .flatMap(new Function<Timed<Long>, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(Timed<Long> timed) throws Exception {
                        long timestamp = timed.time();
                        //判断网络，存在网络才发消息，否则直接返回发送心跳失败
                        if (mContext != null ) {
                            //String heartBeatMsg = heartBeatGenerateCallback.onGenerateHeartBeatMsg(timestamp);
                            //Logger.d(TAG, "发送心跳消息: " + heartBeatMsg);
                           // if (hasWebSocketConnection(url)) {
                                return send(url, "");
                            /*} else {
                                //这里必须用异步发送，如果切断网络，再重连，缓存的WebSocket会被清除，此时再重连网络
                                //是没有WebSocket连接可用的，所以就需要异步连接完成后，再发送
                                return asyncSend(url, heartBeatMsg);
                            }*/
                        }
                        return Observable.create(new ObservableOnSubscribe<Boolean>() {
                            @Override
                            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                                emitter.onNext(false);
                            }
                        });
                    }
                });
    }

}