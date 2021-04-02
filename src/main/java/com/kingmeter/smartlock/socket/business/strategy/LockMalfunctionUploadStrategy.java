package com.kingmeter.smartlock.socket.business.strategy;

import com.kingmeter.dto.KingMeterMarker;
import com.kingmeter.dto.smartlock.socket.in.LockMalfunctionUploadRequestDto;
import com.kingmeter.dto.smartlock.socket.out.LockMalfunctionUploadResponseDto;
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
 * 智能锁故障上报
 */
@Slf4j
@Component()
public class LockMalfunctionUploadStrategy implements RequestStrategy {

    @Autowired
    private SmartLockService smartLockService;

    @Override
    public void process(RequestBody requestBody, ResponseBody responseBody, ChannelHandlerContext ctx) {
        LockMalfunctionUploadRequestDto requestDto =
                new LockMalfunctionUploadRequestDto(requestBody.getData().split(",", -1));

        String lockId = requestBody.getDeviceId();

        log.info(new KingMeterMarker("Socket,LockMalfunctionUpload,A005"),
                "{}|{}|{}|{}|{}|{}", lockId,  requestDto.getSoc(),requestDto.getBoc(),
                requestDto.getFlk(), requestDto.getFlc(), requestDto.getFlb());

        smartLockService.lockMalfunctionUpload(requestDto);

        //包装传出data
        responseBody.setTokenArray(requestBody.getTokenArray());
        responseBody.setFunctionCodeArray(ServerFunctionCodeType.LockMalfunctionUpload);

        LockMalfunctionUploadResponseDto dto = new LockMalfunctionUploadResponseDto();
        dto.setSls("0");
        responseBody.setData(dto.toString());
        ctx.writeAndFlush(responseBody);

        log.info(new KingMeterMarker("Socket,LockMalfunctionUpload,C005"),
                "{}|{}", lockId, 0);
    }

}
