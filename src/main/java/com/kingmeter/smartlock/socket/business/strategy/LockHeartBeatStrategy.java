package com.kingmeter.smartlock.socket.business.strategy;

import com.kingmeter.common.KingMeterMarker;
import com.kingmeter.dto.smartlock.socket.in.LockHeartBeatRequestDto;
import com.kingmeter.dto.smartlock.socket.out.LockHeartBeatResponseDto;
import com.kingmeter.smartlock.socket.acl.SmartLockService;
import com.kingmeter.smartlock.socket.business.code.ServerFunctionCodeType;
import com.kingmeter.socket.framework.dto.RequestBody;
import com.kingmeter.socket.framework.dto.ResponseBody;
import com.kingmeter.socket.framework.strategy.RequestStrategy;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 这里心跳其实就是位置信息上报
 */
@Slf4j
@Component()
public class LockHeartBeatStrategy implements RequestStrategy {


    @Autowired
    private SmartLockService smartLockService;

    @Override
    public void process(RequestBody requestBody, ResponseBody responseBody, ChannelHandlerContext ctx) {

        long lockId = Long.parseLong(requestBody.getDeviceId());

        LockHeartBeatRequestDto requestDto =
                new LockHeartBeatRequestDto(requestBody.getData().split(",",-1));
        requestDto.setLockId(lockId);
        log.info(new KingMeterMarker("Socket,HeartBeat,A004"),
                "{}|{}|{}|{}|{}", lockId,
                requestDto.getLon(),requestDto.getLat(),
                requestDto.getStu(),requestDto.getAlm());

        //包装传出data
        responseBody.setTokenArray(requestBody.getTokenArray());
        responseBody.setFunctionCodeArray(ServerFunctionCodeType.LockHeartBeat);
        LockHeartBeatResponseDto dto = new LockHeartBeatResponseDto();
        dto.setSls("0");

        responseBody.setData(dto.toString());
        ctx.writeAndFlush(responseBody);

        log.info(new KingMeterMarker("Socket,HeartBeat,C004"),
                "{}|{}", lockId, dto.getSls());

        smartLockService.dealWithHeartBeat(lockId,requestBody.getData());
    }



}
