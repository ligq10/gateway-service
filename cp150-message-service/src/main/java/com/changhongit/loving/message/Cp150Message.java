package com.changhongit.loving.message;

public interface Cp150Message<T> {

     String getImei();

     void setImei(String imei);

     T getMessage();

    void setMessage(T message);
}
