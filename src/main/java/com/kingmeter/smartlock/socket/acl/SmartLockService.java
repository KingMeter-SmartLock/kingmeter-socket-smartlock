package com.kingmeter.smartlock.socket.acl;

import com.alibaba.fastjson.JSON;
import com.kingmeter.dto.smartlock.socket.in.*;
import com.kingmeter.dto.smartlock.socket.out.LockLoginResponseDto;
import com.kingmeter.socket.framework.util.CacheUtil;
import com.kingmeter.utils.MD5Util;
import com.kingmeter.utils.TokenResult;
import com.kingmeter.utils.TokenUtils;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component
public class SmartLockService {

    @Value("${kingmeter.default.companyCode}")
    private String defaultCompanyCode;

    @Value("${kingmeter.default.timezone}")
    private int defaultTimezone;

    @Value("${kingmeter.default.password:100419}")
    private String defaultPassword;

    @Autowired
    private BusinessService businessService;

    public LockLoginResponseDto getLoginPermission(LockLoginRequestDto requestDto,
                                                   SocketChannel channel) {
        //100419
        if (!requestDto.getPwd().equals(defaultPassword)) {
            return null;
        }

        long lockId = requestDto.getLockId();

        TokenResult tokenResult = TokenUtils.getInstance().getRandomLockToken();

        LockLoginResponseDto responseDto =
                new LockLoginResponseDto();

        responseDto.setUid("");
        responseDto.setCid("");
        responseDto.setCsn(defaultCompanyCode);
        responseDto.setSls("0");
        responseDto.setOtherCard1("");
        responseDto.setOtherCard2("");
        responseDto.setOtherCard3("");
        responseDto.setOtherCard4("");
        responseDto.setToken(tokenResult.getTokenArray());

        Map<String, String> lockMap = CacheUtil.getInstance()
                .getDeviceInfoMap()
                .getOrDefault(lockId, new ConcurrentHashMap<>());

        lockMap.put("token", tokenResult.getToken());
        lockMap.put("bikeId", requestDto.getBid());
        lockMap.put("channelId", channel.id().asLongText());
        lockMap.put("pwd", requestDto.getPwd());
        lockMap.put("hdv", String.valueOf(requestDto.getHdv()));
        lockMap.put("sfv", String.valueOf(requestDto.getSfv()));
        lockMap.put("icd", requestDto.getIcd());

        lockMap.put("companyCode", defaultCompanyCode);
        lockMap.put("timezone", String.valueOf(defaultTimezone));

        CacheUtil.getInstance().getDeviceInfoMap().put(lockId, lockMap);
        CacheUtil.getInstance().dealWithLoginSucceed(String.valueOf(lockId),
                tokenResult.getToken(), tokenResult.getTokenArray(), channel);

        return responseDto;
    }

    public void lockedUpload(LockedUploadRequestDto lockedUploadRequestDto) {

    }

    public void lockMalfunctionUpload(LockMalfunctionUploadRequestDto lockMalfunctionUploadRequestDto) {

    }

    public void dealWithHeartBeat(long l, String s) {

    }

    public void dealWithQueryLockInfo(LockQueryRequestDto requestDto) {
        Map<String, String> result = new HashMap<>();
        result.put("QueryLockInfo",
                JSON.toJSONString(requestDto));

        CacheUtil.getInstance().getDeviceResultMap().put(
                "QueryLockInfo_" + requestDto.getLockId(), result);
    }

    public void dealWithSetCardList(SetCardListRequestDto requestDto) {
        Map<String, String> result = new HashMap<>();
        result.put("SetCardList",
                JSON.toJSONString(requestDto));

        CacheUtil.getInstance().getDeviceResultMap().put(
                "SetCard_" + requestDto.getLockId(), result);
    }

    public void dealWithSetCert(SetCertForLockRequestDto requestDto) {
        Map<String, String> result = new HashMap<>();
        result.put("SetCert",
                JSON.toJSONString(requestDto));

        CacheUtil.getInstance().getDeviceResultMap().put(
                "SetCert_" + requestDto.getLockId(), result);
    }

    public void dealWithUnLock(long lockId,UnLockSendRequestDto requestDto) {
        Map<String, String> result = new HashMap<>();
        result.put("UnLock",
                JSON.toJSONString(requestDto));

        String key = "UnLock_" + lockId+"_"+requestDto.getUid();

        CacheUtil.getInstance().getDeviceResultMap().put(
                key, result);
    }

    public void offlineNotify(long l) {

    }
}
