package org.platon.lato.service.cache;

import okhttp3.WebSocket;

public interface ICacheTarget<T> {
    /**
     * 重置方法
     *
     * @return 重置后的对象
     */
    T reset();

}