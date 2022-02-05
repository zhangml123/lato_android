package org.platon.lato.service.websocket;

import org.platon.lato.service.cache.BaseCachePool;
import org.platon.lato.service.cache.ICacheTarget;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import okhttp3.WebSocket;
import okio.ByteString;

public class WebSocketInfoPool extends BaseCachePool<WebSocketInfo>  {
    WebSocketInfo webSocketInfo;
    @Override
    public WebSocketInfo onCreateCache() {
        WebSocketInfo webSocketInfo= new WebSocketInfo();
        return webSocketInfo;
    }

    @Override
    public int onSetupMaxCacheCount() {
        return 10;
    }

}
