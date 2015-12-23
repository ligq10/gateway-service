package com.changhongit.loving.upactor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.changhongit.loving.MessageHandler;
import com.changhongit.loving.entity.Shutdown;
import com.changhongit.loving.message.SendMessage;
import com.changhongit.loving.repository.ShutdownRepository;
import com.changhongit.loving.response.CommonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.util.Date;

public class ShutDownMessageHandler extends UntypedActor implements
        MessageHandler {

    Logger log = LoggerFactory.getLogger(getClass());

    final ActorRef mainHandler;

    final ApplicationContext ctx;

    private ShutdownRepository shutdownRepository;

    public ShutDownMessageHandler(ActorRef mainHandler, ApplicationContext ctx) {
        this.mainHandler = mainHandler;
        this.ctx = ctx;
        shutdownRepository = ctx.getBean(ShutdownRepository.class);
    }

    @Override
    public boolean matchType(String message) {
        return message.startsWith("C P") && message.contains("|0x0006|");
    }

    @Override
    public String getResponse(String message) {
        String[] params = message.split(" ");
        CommonResponse response = new CommonResponse(params[2], params[3], "0");
        return response.formatToString();
    }

    @Override
    public void processMessage(String message) {
        saveMessage(message);
    }

    private void saveMessage(String message) {
        String[] params = message.split(" ");
        String imei = params[3];
        Shutdown shutdown = new Shutdown();
        shutdown.setImei(imei);
        shutdown.setDate(new Date());
        shutdownRepository.save(shutdown);
    }

    @Override
    public void onReceive(Object msg) {
        if (msg instanceof String) {
            String message = (String) msg;
            if (matchType(message)) {
                log.debug("message matched shutdown.");
                processMessage(message);
                String response = getResponse(message);
                if (!StringUtils.isEmpty(response)) {
                    mainHandler
                            .tell(new SendMessage(response), getSelf());
                }
            }
        }
    }
}
