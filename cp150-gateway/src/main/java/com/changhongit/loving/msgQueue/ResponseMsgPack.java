package com.changhongit.loving.msgQueue;

import org.msgpack.annotation.Message;

@Message
public class ResponseMsgPack {

    private String imei;

    private String seq;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }
}
