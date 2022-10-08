package com.kingmeter.smartlock.socket.business.utils;

import com.kingmeter.common.KingMeterException;
import com.kingmeter.common.ResponseCode;
import com.kingmeter.common.SpringContexts;
import com.kingmeter.smartlock.socket.acl.SmartLockService;
import com.kingmeter.smartlock.socket.business.code.ClientFunctionCodeType;
import com.kingmeter.smartlock.socket.business.code.ServerFunctionCodeType;
import com.kingmeter.socket.framework.business.WorkerTemplate;
import com.kingmeter.socket.framework.config.HeaderCode;
import com.kingmeter.socket.framework.dto.ResponseBody;
import com.kingmeter.socket.framework.strategy.RequestStrategy;
import com.kingmeter.socket.framework.util.CacheUtil;
import com.kingmeter.utils.StringUtil;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Worker extends WorkerTemplate {

    @Autowired
    private SmartLockService smartLockService;

    @Autowired
    private SpringContexts springContexts;

    @Autowired
    private HeaderCode headerCode;


    @Override
    public RequestStrategy getRequestStrategy(int functionCode) {
        return (RequestStrategy)springContexts.getBean(ClientFunctionCodeType.getEnum(functionCode).getClassName());
    }

    @Override
    public void doDealWithOffline(SocketChannel channel, String deviceId) {
        if (StringUtil.isNotEmpty(deviceId)) {
            smartLockService.offlineNotify(Long.parseLong(deviceId));
        }
    }

    @Override
    public ResponseBody getConnectionTestCommand(String deviceId) {
        byte[] tokenArray;
        if (CacheUtil.getInstance().getDeviceIdAndTokenArrayMap().containsKey(deviceId)) {
            tokenArray = CacheUtil.getInstance().getDeviceIdAndTokenArrayMap().get(deviceId);
        } else {
            throw new KingMeterException(ResponseCode.Device_Not_Logon);
        }
        ResponseBody responseBody = new ResponseBody();
        responseBody.setTokenArray(tokenArray);
        responseBody.setFunctionCodeArray(ServerFunctionCodeType.QueryLockInfo);
        responseBody.setData("");
        responseBody.setSTART_CODE_1(headerCode.getSTART_CODE_1());
        responseBody.setSTART_CODE_2(headerCode.getSTART_CODE_2());
        responseBody.setEND_CODE_1(headerCode.getEND_CODE_1());
        responseBody.setEND_CODE_2(headerCode.getEND_CODE_2());
        responseBody.setToken_length(headerCode.getTOKEN_LENGTH());
        responseBody.setDeviceId(Long.parseLong(deviceId));
        return responseBody;
    }
}
