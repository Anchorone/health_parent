package com.itheima.service;

public interface ValidateService {
    boolean send(String mail,Integer code);
    void send(String mail,String test);
}
