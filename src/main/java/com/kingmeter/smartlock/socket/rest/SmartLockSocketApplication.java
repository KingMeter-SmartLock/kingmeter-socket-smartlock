package com.kingmeter.smartlock.socket.rest;


import com.alibaba.fastjson.JSON;
import com.kingmeter.common.KingMeterMarker;
import com.kingmeter.dto.smartlock.rest.request.SettingCardListRequestDto;
import com.kingmeter.dto.smartlock.socket.in.LockQueryRequestDto;
import com.kingmeter.dto.smartlock.socket.in.SetCardListRequestDto;
import com.kingmeter.dto.smartlock.socket.in.SetCertForLockRequestDto;
import com.kingmeter.dto.smartlock.socket.in.UnLockSendRequestDto;
import com.kingmeter.dto.smartlock.socket.out.SendCardListResponseDto;
import com.kingmeter.dto.smartlock.socket.out.UnLockSendResponseDto;
import com.kingmeter.smartlock.socket.business.code.ServerFunctionCodeType;
import com.kingmeter.socket.framework.application.SocketApplication;
import com.kingmeter.socket.framework.util.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class SmartLockSocketApplication {
    @Autowired
    private SocketApplication socketApplication;


    public UnLockSendRequestDto unLockSend(long lockId,String userId,String card){

        UnLockSendResponseDto responseDto = new UnLockSendResponseDto(userId,card);

        String key = "UnLock_" + lockId+"_"+userId;
        CacheUtil.getInstance().getDeviceResultMap().remove(key);

        socketApplication.sendSocketMsg(lockId,
                ServerFunctionCodeType.UnLockSend,
                responseDto);

        log.info(new KingMeterMarker("Socket,UnLockSend,C002"),
                "{}|{}|{}", lockId, userId, card);

        //4,wait for lock response
        Map<String, String> result = socketApplication.waitForMapResult(key);
        return JSON.parseObject(result.get("UnLock"), UnLockSendRequestDto.class);
    }


    public LockQueryRequestDto queryLockInfo(long lockId){
        String key = "QueryLockInfo_" + lockId;
        CacheUtil.getInstance().getDeviceResultMap().remove(key);

        socketApplication.sendSocketMsg(lockId,
                ServerFunctionCodeType.QueryLockInfo,
                null);

        log.info(new KingMeterMarker("Socket,QueryLockInfo,C006"),
                "{}", lockId);

        //4,wait for lock response
        Map<String, String> result = socketApplication.waitForMapResult(key);
        return JSON.parseObject(result.get("QueryLockInfo"), LockQueryRequestDto.class);
    }


    public SetCardListRequestDto setCardList(SettingCardListRequestDto requestDto){
        long lockId = requestDto.getLockId();
        int userId = requestDto.getUserId();

        String key = "SetCard_" + lockId;
        CacheUtil.getInstance().getDeviceResultMap().remove(key);

        SendCardListResponseDto responseDto = new SendCardListResponseDto(
                String.valueOf(userId),
                requestDto.getCid(),
                requestDto.getOtherCard1(), requestDto.getOtherCard2(),
                requestDto.getOtherCard3(), requestDto.getOtherCard4()
        );

        socketApplication.sendSocketMsg(lockId,
                ServerFunctionCodeType.SendCardList,
                responseDto);

        log.info(new KingMeterMarker("Socket,SetCardList,C018"),
                "{}", lockId);

        //4,wait for lock response
        Map<String, String> result = socketApplication.waitForMapResult(key);
        return JSON.parseObject(result.get("SetCardList"), SetCardListRequestDto.class);
    }

    public SetCertForLockRequestDto setCert(long lockId,String cert){
        String key = "SetCert_" + lockId;
        CacheUtil.getInstance().getDeviceResultMap().remove(key);

        socketApplication.sendSocketMsg(lockId,
                ServerFunctionCodeType.SetCertForLock,
                cert+",");

        log.info(new KingMeterMarker("Socket,SetCertForLock,C019"),
                "{}|{}", lockId,cert);

        //4,wait for lock response
        Map<String, String> result = socketApplication.waitForMapResult(key);
        return JSON.parseObject(result.get("SetCert"), SetCertForLockRequestDto.class);
    }

}
