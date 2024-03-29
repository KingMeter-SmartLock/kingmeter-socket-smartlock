package com.kingmeter.smartlock.socket.business.strategy;

import com.kingmeter.common.KingMeterMarker;
import com.kingmeter.dto.smartlock.socket.in.LockQueryRequestDto;
import com.kingmeter.smartlock.socket.acl.SmartLockService;
import com.kingmeter.socket.framework.dto.RequestBody;
import com.kingmeter.socket.framework.dto.ResponseBody;
import com.kingmeter.socket.framework.strategy.RequestStrategy;
import com.kingmeter.utils.StringUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component()
public class QueryLockInfoStrategy implements RequestStrategy {

    @Autowired
    private SmartLockService smartLockService;

    @Override
    public void process(RequestBody requestBody, ResponseBody responseBody, ChannelHandlerContext channelHandlerContext) {
        LockQueryRequestDto requestDto =
                new LockQueryRequestDto(requestBody.getData().split(",", -1));

        long lockId = requestDto.getLockId();

        smartLockService.dealWithQueryLockInfo(requestDto);

//        String tmp = "Lock Info Data is Start-->";
//        tmp += requestBody.getData();
//        tmp += "<--END";
//        log.info(tmp);

        log.info(new KingMeterMarker("Socket,QueryLockInfo,A006"),
                "{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}",
                lockId, requestDto.getBikeId(),
                requestDto.getHdv(), requestDto.getSfv(),
                requestDto.getLon(), requestDto.getLat(),
                requestDto.getAlm(), requestDto.getStu(),
                requestDto.getSoc(), requestDto.getBoc(),
                requestDto.getFlk(), requestDto.getFlc(), requestDto.getFlb());

    }

}
