package org.platon.lato.service.cache;

import java.io.Serializable;

public class CacheItem<T> implements Serializable {
    private static final long serialVersionUID = -401778630524300400L;

    /**
     * 缓存的对象
     */
    private T cacheTarget;
    /**
     * 最近使用时间
     */
    private long recentlyUsedTime;

    public CacheItem(T cacheTarget, long recentlyUsedTime) {
        this.cacheTarget = cacheTarget;
        this.recentlyUsedTime = recentlyUsedTime;
    }

    public T getCacheTarget() {
        return cacheTarget;
    }

    public void setCacheTarget(T cacheTarget) {
        this.cacheTarget = cacheTarget;
    }

    public long getRecentlyUsedTime() {
        return recentlyUsedTime;
    }

    public void setRecentlyUsedTime(long recentlyUsedTime) {
        this.recentlyUsedTime = recentlyUsedTime;
    }
}