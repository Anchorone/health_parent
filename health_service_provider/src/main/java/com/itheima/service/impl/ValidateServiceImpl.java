package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.constant.MailConstant;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.service.ValidateService;
import com.itheima.utils.MailUtiles;
import com.itheima.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisPool;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service(interfaceClass = ValidateService.class)
@Transactional
public class ValidateServiceImpl implements ValidateService {
    @Autowired
    private JavaMailSenderImpl mailSender;

    //发送预约的验证码
    @Override
    public boolean send(String mail,Integer code) {
        //给用户发送验证码
        try {
            MailUtiles.send(MailConstant.MailFrom,mail,"验证码",String.valueOf(code));
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //发送预约成功信息
    @Override
    public void send(String mail, String test) {
        try {
            MailUtiles.send(MailConstant.MailFrom,mail,"预约成功",test);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
