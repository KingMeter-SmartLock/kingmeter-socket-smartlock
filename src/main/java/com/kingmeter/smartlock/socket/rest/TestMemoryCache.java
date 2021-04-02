package com.kingmeter.smartlock.socket.rest;

import lombok.Getter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class TestMemoryCache {

    private volatile static TestMemoryCache instance;

    private TestMemoryCache() {
    }

    public static TestMemoryCache getInstance() {
        if (instance == null) {
            synchronized (TestMemoryCache.class) {
                if (instance == null) {
                    instance = new TestMemoryCache();
                }
            }
        }
        return instance;
    }

    private volatile ConcurrentMap<Long, Boolean> unlockFlag = new ConcurrentHashMap();
    private volatile ConcurrentMap<Long, AtomicInteger> unlockCountMap = new ConcurrentHashMap();
    private volatile ConcurrentMap<Long, Boolean> queryLockInfoFlag = new ConcurrentHashMap();


    public void addUnLockFlag(long lockId,boolean flag){
        this.unlockFlag.put(lockId,flag);
    }

    public void removeUnLockFlag(long lockId){
        this.unlockFlag.remove(lockId);
    }


    public int queryUnlockCount(long lockId){
        AtomicInteger tmp = this.unlockCountMap.getOrDefault(lockId,new AtomicInteger(1));
        tmp.getAndIncrement();
        this.unlockCountMap.put(lockId,tmp);
        return tmp.get();
    }

    public void removeUnlockCountMap(long lockId){
        this.unlockCountMap.remove(lockId);
    }


    public void addQueryLockInfoFlag(long lockId,boolean flag){
        this.queryLockInfoFlag.put(lockId,flag);
    }

    public void removeQueryLockInfoFlag(long lockId){
        this.queryLockInfoFlag.remove(lockId);
    }

}
