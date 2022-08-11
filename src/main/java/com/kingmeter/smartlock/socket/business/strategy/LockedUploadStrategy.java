package com.kingmeter.smartlock.socket.business.strategy;

import com.kingmeter.common.KingMeterMarker;
import com.kingmeter.dto.smartlock.socket.in.LockedUploadRequestDto;
import com.kingmeter.dto.smartlock.socket.out.LockedUploadResponseDto;
import com.kingmeter.smartlock.socket.acl.SmartLockService;
import com.kingmeter.smartlock.socket.business.code.ServerFunctionCodeType;
import com.kingmeter.socket.framework.dto.RequestBody;
import com.kingmeter.socket.framework.dto.ResponseBody;
import com.kingmeter.socket.framework.strategy.RequestStrategy;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component()
public class LockedUploadStrategy implements RequestStrategy {

    @Autowired
    private SmartLockService smartLockService;

    @Override
    public void process(RequestBody requestBody, ResponseBody responseBody, ChannelHandlerContext ctx) {
        long lockId = Long.parseLong(requestBody.getDeviceId());

        LockedUploadRequestDto requestDto =
                new LockedUploadRequestDto(requestBody.getData().split(",", -1));
        String userIdStr = requestDto.getUid();
//        long userId = 0;
//        if (StringUtil.isNotEmpty(userIdStr)) {
//            userId = Long.parseLong(requestDto.getUid(), 16);
//        }
//        requestDto.setUid(String.valueOf(userId));
        requestDto.setLockId(lockId);

        smartLockService.lockedUpload(requestDto);

        log.info(new KingMeterMarker("Socket,LockedUpload,A003"),
                "{}|{}", lockId, requestDto.getUid());

        //包装传出data
        responseBody.setTokenArray(requestBody.getTokenArray());
        responseBody.setFunctionCodeArray(ServerFunctionCodeType.LockedUpload);
        LockedUploadResponseDto dto = new LockedUploadResponseDto();
        dto.setSls("0");
        responseBody.setData(dto.toString());
        ctx.writeAndFlush(responseBody);

        log.info(new KingMeterMarker("Socket,LockedUpload,C003"),
                "{}|{}|{}", lockId, requestDto.getUid(),0);
    }

}
