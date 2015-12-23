package com.changhongit.loving.message;

import akka.actor.ActorRef;

public class RegisterConnection {

    private String imei;
    private ActorRef handler;

    public RegisterConnection(String imei, ActorRef handler) {
        this.imei = imei;
        this.handler = handler;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public ActorRef getHandler() {
        return handler;
    }

    public void setHandler(ActorRef handler) {
        this.handler = handler;
    }
}
