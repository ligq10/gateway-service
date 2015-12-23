package com.changhongit.loving;


public interface MessageHandler {

    boolean matchType(String message);

    void processMessage(String message);

    String getResponse(String message);
}
