package com.kingmeter.smartlock.socket.business.strategy;

import com.kingmeter.common.KingMeterMarker;
import com.kingmeter.dto.smartlock.socket.in.SetCertForLockRequestDto;
import com.kingmeter.smartlock.socket.acl.SmartLockService;
import com.kingmeter.socket.framework.dto.RequestBody;
import com.kingmeter.socket.framework.dto.ResponseBody;
import com.kingmeter.socket.framework.strategy.RequestStrategy;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component()
public class SetCertForLockStrategy implements RequestStrategy {

    @Autowired
    private SmartLockService smartLockService;

    @Override
    public void process(RequestBody requestBody, ResponseBody responseBody, ChannelHandlerContext channelHandlerContext) {

        SetCertForLockRequestDto requestDto =
                new SetCertForLockRequestDto(requestBody.getData().split(",",-1));

        long lockId = requestDto.getLockId();

        log.info(new KingMeterMarker("Socket,SetCertForLock,A019"),
                "{}|{}", lockId, requestDto.getSls());

        smartLockService.dealWithSetCert(requestDto);
    }
}
