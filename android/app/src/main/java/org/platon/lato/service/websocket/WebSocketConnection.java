package org.platon.lato.service.websocket;

import android.util.Log;

import com.squareup.okhttp.internal.Util;

import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketConnection extends WebSocketListener {
    private static final String TAG                       = WebSocketConnection.class.getSimpleName();
    private static final int    KEEPALIVE_TIMEOUT_SECONDS = 55;

    private WebSocket    client;
    private final String wsUri;
    private boolean      connected;
    private final ConnectivityListener          listener;

    private int                 attempts;

    public WebSocketConnection(String wsUri, ConnectivityListener listener) {
        this.wsUri = wsUri;
        this.listener = listener;
        this.attempts            = 0;
    }

    public synchronized void connect() {
        Log.w(TAG, "WSC connect()...");

        if (client == null) {
            //Pair<SSLSocketFactory, X509TrustManager> socketFactory = createTlsSocketFactory(trustStore);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .writeTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(KEEPALIVE_TIMEOUT_SECONDS + 10, TimeUnit.SECONDS)
                    .connectTimeout(KEEPALIVE_TIMEOUT_SECONDS + 10, TimeUnit.SECONDS)
                    .build();


            Request.Builder requestBuilder = new Request.Builder().url(wsUri);

            this.connected = false;
            this.client    = okHttpClient.newWebSocket(requestBuilder.build(), this);
        }
    }
    public synchronized void disconnect() {
        Log.w(TAG, "WSC disconnect()...");

        if (client != null) {
            client.close(1000, "OK");
            client    = null;
            connected = false;
        }

        /*if (keepAliveSender != null) {
            keepAliveSender.shutdown();
            keepAliveSender = null;
        }*/
    }

    @Override
    public synchronized void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        if (client != null ) {
            Log.w(TAG, "onConnected()");
            attempts        = 0;
            connected       = true;

            if (listener != null) listener.onConnected();
        }
    }
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        //收到消息...（一般是这里处理json）
    }
    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        super.onMessage(webSocket, bytes);
        //收到消息...（一般很少这种消息）
    }
    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
        Log.w(TAG, "onClose()...");
        this.connected = false;


        if (listener != null) {
            listener.onDisconnected();
        }

       // Util.wait(this, Math.min(++attempts * 200, TimeUnit.SECONDS.toMillis(15)));
        if (client != null) {
            client.close(1000, "OK");
            client    = null;
            connected = false;
           // connect();
        }
    }
    @Override
    public void onFailure(WebSocket webSocket, Throwable throwable, Response response) {
        super.onFailure(webSocket, throwable, response);
        Log.w(TAG, "onFailure()");
        Log.w(TAG, throwable);

        if (response != null && (response.code() == 401 || response.code() == 403)) {
            if (listener != null) listener.onAuthenticationFailure();
        }

        if (client != null) {
            onClosed(webSocket, 1000, "OK");
        }
    }


    @Override
    public synchronized void onClosing(WebSocket webSocket, int code, String reason) {
        Log.w(TAG, "onClosing()!...");
        webSocket.close(1000, "OK");
    }

}
