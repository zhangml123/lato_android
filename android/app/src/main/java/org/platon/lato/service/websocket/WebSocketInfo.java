package org.platon.lato.service.websocket;

import org.platon.lato.service.cache.BaseCachePool;
import org.platon.lato.service.cache.ICacheTarget;

import java.io.Serializable;

import okhttp3.WebSocket;
import okio.ByteString;

public class WebSocketInfo implements Serializable, ICacheTarget<WebSocketInfo> {
    private static final long serialVersionUID = -880481254453932113L;
    public static final int CONNECT                 = 1;
    public static final int RECONNECT               = 2;
    public static final int PREPARE_RECONNECT       = 3;
    public static final int RECEIVE_STRING_MSG      = 4;
    public static final int RECEIVE_BYTE_STRING_MSG = 5;
    public static final int CLOSE                   = 6;
    private int actionType;
    private WebSocket mWebSocket;
    private String mStringMsg;
    private ByteString mByteStringMsg;
    /**
     * 连接成功
     */
    private boolean isConnect;
    /**
     * 重连成功
     */
    private boolean isReconnect;
    /**
     * 准备重连
     */
    private boolean isPrepareReconnect;

    /**
     * 重置
     */
    @Override
    public WebSocketInfo reset() {
        this.mWebSocket = null;
        this.mStringMsg = null;
        this.mByteStringMsg = null;
        this.isConnect = false;
        this.isReconnect = false;
        this.isPrepareReconnect = false;
        return this;
    }

    public WebSocket getWebSocket() {
        return this.mWebSocket;
    }

    public WebSocketInfo setWebSocket(WebSocket webSocket) {
        this.mWebSocket = webSocket;
        return this;
    }
    public int getActionType(){
        return this.actionType;
    }
    public WebSocketInfo setActionType(int actionType){
        this.actionType = actionType;
        return this;
    }
    public WebSocketInfo setReconnect(boolean b) {
        isReconnect = b;
        return this;
    }

    public WebSocketInfo setPrepareReconnect(boolean b) {
        isPrepareReconnect = b;
        return this;
    }

    public WebSocketInfo setConnect(boolean b) {
        isConnect = b;
        return this;
    }

    public WebSocketInfo setStringMsg(String stringMsg) {
        mStringMsg = stringMsg;
        return this;
    }

    public WebSocketInfo setByteStringMsg(ByteString byteMsg) {
        mByteStringMsg = byteMsg;
        return this;
    }

    public WebSocketInfo obtain(String url) {
        return this;
    }

    public String getStringMsg() {
        return mStringMsg;
    }


}