package com.kingmeter.smartlock.socket.business.strategy;

import com.kingmeter.common.KingMeterMarker;
import com.kingmeter.dto.smartlock.socket.in.LockLoginRequestDto;
import com.kingmeter.dto.smartlock.socket.out.LockLoginResponseDto;
import com.kingmeter.smartlock.socket.acl.SmartLockService;
import com.kingmeter.smartlock.socket.business.code.ServerFunctionCodeType;
import com.kingmeter.socket.framework.dto.RequestBody;
import com.kingmeter.socket.framework.dto.ResponseBody;
import com.kingmeter.socket.framework.strategy.RequestStrategy;
import com.kingmeter.utils.StringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component()
public class LoginStrategy implements RequestStrategy {


    @Autowired
    private SmartLockService smartLockService;

    @Override
    public void process(RequestBody requestBody, ResponseBody responseBody, ChannelHandlerContext ctx) {
        //解析传入data
        LockLoginRequestDto requestDto =
                new LockLoginRequestDto(requestBody.getData().split(",", -1));

        long lockId = requestDto.getLockId();
        long bikeId = 0;
        if(StringUtil.isNotEmpty(requestDto.getBid())){
            bikeId = Long.parseLong(requestDto.getBid());
        }

        log.info(new KingMeterMarker("Socket,Login,A001"),
                "{}|{}|{}|{}|{}|{}|{}", lockId, bikeId,requestDto.getPwd(),
                requestDto.getHdv(),requestDto.getSfv(),
                requestDto.getIcd(),requestDto.getSls());

        LockLoginResponseDto responseDto =
                smartLockService.getLoginPermission(requestDto,
                        (SocketChannel) ctx.channel());

        if(responseDto==null){
            ctx.close();
            return;
        }

        //包装传出data
        responseBody.setTokenArray(responseDto.getToken());
        responseBody.setFunctionCodeArray(ServerFunctionCodeType.LoginType);
        responseBody.setData(responseDto.toString(requestDto.getSfv()));
        ctx.writeAndFlush(responseBody);

        log.info(new KingMeterMarker("Socket,Login,C001"),
                "{}|{}|{}|{}|{}|{}|{}|{}|{}", lockId, responseDto.getSls(),
                responseDto.getCsn(),0,responseDto.getCid(),
                responseDto.getOtherCard1(),responseDto.getOtherCard2(),
                responseDto.getOtherCard3(),responseDto.getOtherCard4());
    }

//    private void dealWithLoginInRedis(LockLoginRequestDto requestDto, String token) {
//        long lockId = requestDto.getLockId();
//        Map<String, String> lockMap = new HashMap<>();
//
//        if (CacheUtil.Device_info_mapContainsKey(lockId)) {
//            lockMap = CacheUtil.getDeviceInfoFromDevice_info_map(lockId);
//        } else {
//            DateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
//            lockMap.put("loginTime", sdf.format(new Date()));
//        }
//
//        lockMap.put("token", token);
//        Date now = new Date();
//        DateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
//
//        lockMap.put("bikeId",requestDto.getBid());
//        lockMap.put("pwd", requestDto.getPwd());
//        lockMap.put("hdv", String.valueOf(requestDto.getHdv()));
//        lockMap.put("sfv", String.valueOf(requestDto.getSfv()));
//        lockMap.put("icd", requestDto.getIcd());
//        lockMap.put("loginTime", sdf.format(now));
//
//        CacheUtil.setDevice_info_map(lockId, lockMap);
//    }


}
