package com.changhongit.loving.model;

public interface Cp150Message<T> {

     String getImei();

     void setImei(String imei);

     T getMessage();

    void setMessage(T message);
}
