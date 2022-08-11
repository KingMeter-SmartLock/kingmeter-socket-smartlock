package com.kingmeter.smartlock.socket.business.utils;

import com.kingmeter.common.SpringContexts;
import com.kingmeter.smartlock.socket.acl.SmartLockService;
import com.kingmeter.smartlock.socket.business.code.ClientFunctionCodeType;
import com.kingmeter.socket.framework.business.WorkerTemplate;
import com.kingmeter.socket.framework.strategy.RequestStrategy;
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


}
