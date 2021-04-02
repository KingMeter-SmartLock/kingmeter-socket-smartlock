package com.kingmeter.smartlock.socket.business.code;

import com.kingmeter.smartlock.socket.business.strategy.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 这里的都是指 智能锁 发送的 功能编码
 */
public enum ClientFunctionCodeType {

    LoginType(0xA001, LoginStrategy.class),//智能锁登录
    UnLockSend(0xA002, UnLockSendStrategy.class),//智能锁开锁下发后得上报
    LockedUpload(0xA003, LockedUploadStrategy.class),//关锁上报
    LockHeartBeat(0xA004, LockHeartBeatStrategy.class),//智能锁心跳上报位置
    LockHealthUpload(0xA005, LockMalfunctionUploadStrategy.class),//智能锁故障上报
    QueryLockInfo(0xA006, QueryLockInfoStrategy.class),//后台读取锁编号，智能锁硬件还没实现

    SetCardList(0xA018, SetCardListStrategy.class),//设置卡号

    SetCertForLock(0xA019,SetCertForLockStrategy.class);//后台读取锁编号，智能锁硬件还没实现
	
    private int value;
    private Class className;

    ClientFunctionCodeType(int value, Class className) {
        this.value = value;
        this.className = className;
    }

    public int value() {
        return value;
    }

    public Class getClassName() {
        return className;
    }

    static Map<Integer, ClientFunctionCodeType> enumMap = new HashMap();

    static {
        for (ClientFunctionCodeType type : ClientFunctionCodeType.values()) {
            enumMap.put(type.value(), type);
        }
    }

    public static ClientFunctionCodeType getEnum(Integer value) {
        return enumMap.get(value);
    }

    public static boolean containsValue(Integer value) {
        return enumMap.containsKey(value);
    }
}
