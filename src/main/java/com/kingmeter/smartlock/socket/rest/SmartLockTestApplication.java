package com.kingmeter.smartlock.socket.rest;


import com.kingmeter.common.KingMeterMarker;
import com.kingmeter.dto.smartlock.socket.in.UnLockSendRequestDto;
import com.kingmeter.smartlock.socket.business.code.ServerFunctionCodeType;
import com.kingmeter.socket.framework.application.SocketApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmartLockTestApplication {

    @Autowired
    private SocketApplication socketApplication;

    @Autowired
    private SmartLockSocketApplication smartLockSocketApplication;

    public void stopUnLock(long lockId) {
        TestMemoryCache.getInstance().getUnlockFlag().remove(lockId);
    }


    public String batchUnLock(long lockId, int times, long perLock) {

        TestMemoryCache.getInstance().addUnLockFlag(lockId, true);

        log.info("getUnlockFlag1:{}{}{}--times: {},perLock: {}", lockId,
                TestMemoryCache.getInstance().getUnlockFlag().containsKey(lockId),
                TestMemoryCache.getInstance().getUnlockFlag().get(lockId),
                times, perLock);

        new Thread(new TestUnLockPerTime(lockId, times, perLock)).start();

        return "batch query Lock info succeed";
    }

    class TestUnLockPerTime implements Runnable {

        private long lockId;
        private int times;
        private long perLock;

        public TestUnLockPerTime(long lockId, int times, long perLock) {
            this.lockId = lockId;
            this.times = times;
            this.perLock = perLock;
        }

        public void run() {
            for (int i = 0; i < times; i++) {
                log.info("getUnlockFlag2:{}-{}-{}---times: {}", lockId,
                        TestMemoryCache.getInstance().getUnlockFlag().containsKey(lockId),
                        TestMemoryCache.getInstance().getUnlockFlag().get(lockId),
                        i+1);

                if (TestMemoryCache.getInstance().getUnlockFlag().containsKey(lockId) &&
                        TestMemoryCache.getInstance().getUnlockFlag().get(lockId)) {

                    boolean flag = unLockSingleLock(lockId);
                    if (!flag) {
                        if (TestMemoryCache.getInstance().queryUnlockCount(lockId) > 3) {
                            TestMemoryCache.getInstance().removeUnLockFlag(lockId);
                            TestMemoryCache.getInstance().removeUnlockCountMap(lockId);
                            break;
                        } else {
                            continue;
                        }
                    }else{
                        TestMemoryCache.getInstance().removeUnlockCountMap(lockId);
                    }

                    try {
                        Thread.sleep(perLock * 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
        }
    }

    public boolean unLockSingleLock(long lockId) {
        try {
//            UnLockSendResponseDto responseDto = new UnLockSendResponseDto("0","");
//            socketApplication.sendSocketMsg(lockId,
//                    ServerFunctionCodeType.UnLockSend,
//                    responseDto);

            UnLockSendRequestDto response =
                    smartLockSocketApplication.unLockSend(lockId, "123", "");

            log.info(new KingMeterMarker("Socket,UnLockSend,C002"),
                    "{}|{}|{}", lockId, 0, "");

            if (response.getStu().equals("3")) {
                return false;
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public void stopQueryLockInfo(long lockId) {
        TestMemoryCache.getInstance().getQueryLockInfoFlag().remove(lockId);
    }


    public String batchQueryLockInfo(long lockId, int times, long perLock) {

        TestMemoryCache.getInstance().addQueryLockInfoFlag(lockId, true);

        new Thread(new TestQueryLockInfoPerTime(lockId, times, perLock)).start();

        return "batch query Lock info succeed";
    }

    class TestQueryLockInfoPerTime implements Runnable {

        private long lockId;
        private int times;
        private long perLock;


        public TestQueryLockInfoPerTime(long siteId, int times, long perLock) {
            this.lockId = siteId;
            this.times = times;
            this.perLock = perLock;
        }

        public void run() {
            for (int i = 0; i < times; i++) {
                if (TestMemoryCache.getInstance().getQueryLockInfoFlag().containsKey(lockId) &&
                        TestMemoryCache.getInstance().getQueryLockInfoFlag().get(lockId)) {

                    boolean flag = querySingleLockInfo(lockId);
                    if (!flag) {
                        TestMemoryCache.getInstance().removeQueryLockInfoFlag(lockId);
                        break;
                    }
                    try {
                        Thread.sleep(perLock * 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
        }
    }

    public boolean querySingleLockInfo(long lockId) {
        try {
            socketApplication.sendSocketMsg(lockId,
                    ServerFunctionCodeType.QueryLockInfo,
                    null);

            log.info(new KingMeterMarker("Socket,QueryLockInfo,C006"),
                    "{}", lockId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
