package com.kingmeter.smartlock.socket.business.code;

/**
 * 这里都是指的 服务器发送 的功能编码
 */
public interface ServerFunctionCodeType {

    byte[] LoginType = {(byte) 192, (byte) 1};//("C0 01"),//登录返回
    byte[] UnLockSend = {(byte) 192, (byte) 2};//("C0 02"),//开锁下发
    byte[] LockedUpload = {(byte) 192, (byte) 3};//("C0 03"),//关锁上报
    byte[] LockHeartBeat = {(byte) 192, (byte) 4};//("C0 04"),//位置信息心跳上报
    byte[] LockMalfunctionUpload = {(byte) 192, (byte) 5};//("C0 05"),//故障上报
    byte[] QueryLockInfo = {(byte) 192, (byte) 6};//("C0 06"),//后台读取锁编号,智能锁硬件还没实现
    byte[] SendCardList = {(byte) 192, (byte) 24};//("C0 18"),
    byte[] SetCertForLock = {(byte) 192, (byte) 25};//("C0 19");
}
