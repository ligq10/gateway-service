package com.changhongit.loving;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.changhongit.loving.message.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager extends UntypedActor {

    Logger logger = LoggerFactory.getLogger(getClass());

    Map<String, ActorRef> cacheConnection = new ConcurrentHashMap<String, ActorRef>();

    @Override
    public void onReceive(Object msg) throws Exception {

        if (msg instanceof RegisterConnection) {
            RegisterConnection registerConnection = (RegisterConnection) msg;
            cacheConnection.put(registerConnection.getImei(), registerConnection.getHandler());
            logger.debug("registered connection for imei: {}", registerConnection.getImei());
        } else if (msg instanceof UnRegisterConnection) {
            UnRegisterConnection unRegisterConnection = (UnRegisterConnection) msg;
            
            if (StringUtils.isEmpty(unRegisterConnection.getImei()) == false && cacheConnection.containsKey(unRegisterConnection.getImei())) {
                cacheConnection.remove(unRegisterConnection.getImei());
                logger.debug("unregistered connection for imei: {}", unRegisterConnection.getImei());
            }            	
            

        } else if (msg instanceof Cp150DownMessage) {
            Cp150DownMessage cp150Message = (Cp150DownMessage) msg;
            logger.debug("Received cp150 message: imei: {}, message: {}", cp150Message.getImei(), cp150Message.getMessage());
            if (StringUtils.isEmpty(cp150Message.getImei()) == false && cacheConnection.containsKey(cp150Message.getImei())) {
                ActorRef handler = cacheConnection.get(cp150Message.getImei());
                handler.tell(cp150Message, getSelf());
            } else {
                logger.debug("can not find connection for imei: {}", cp150Message.getImei());
            }
        }
    }
}
