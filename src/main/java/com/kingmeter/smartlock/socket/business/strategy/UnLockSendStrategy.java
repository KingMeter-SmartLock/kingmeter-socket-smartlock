package com.kingmeter.smartlock.socket.business.strategy;

import com.kingmeter.common.KingMeterMarker;
import com.kingmeter.dto.smartlock.socket.in.UnLockSendRequestDto;
import com.kingmeter.smartlock.socket.acl.SmartLockService;
import com.kingmeter.socket.framework.dto.RequestBody;
import com.kingmeter.socket.framework.dto.ResponseBody;
import com.kingmeter.socket.framework.strategy.RequestStrategy;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 智能锁开锁上传socket消息给服务器,这里负责接收
 */
@Slf4j
@Component()
public class UnLockSendStrategy implements RequestStrategy {

    @Autowired
    private SmartLockService smartLockService;

    @Override
    public void process(RequestBody requestBody, ResponseBody responseBody, ChannelHandlerContext channelHandlerContext) {
        UnLockSendRequestDto requestDto =
                new UnLockSendRequestDto(requestBody.getData().split(",", -1));

        String lockId = requestBody.getDeviceId();

        log.info(new KingMeterMarker("Socket,UnLockSend,A002"),
                "{}|{}|{}", lockId, requestDto.getStu(), requestDto.getUid());

        smartLockService.dealWithUnLock(Long.parseLong(lockId), requestDto);

//        CacheUtil.setScanUnlock_result_map(lockId+"_"+requestDto.getUid(),
//                Integer.parseInt(requestDto.getStu()));

//        //这里先不对锁状态进行判断
//        redis.set("lock_"+requestBody.getDeviceId()+"_scan",requestDto.getStu(),flagInRedisTime);//表示成功开锁
//        //这个不向智能锁发送socket信息
    }
}
