package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.service.ValidateService;
import com.itheima.utils.ValidateCodeUtils;
import org.apache.xmlbeans.impl.xb.xsdschema.MinExclusiveDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

/**
 * 验证码操作
 */
@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {
    @Reference
    private ValidateService validateService;
    @Autowired
    private JedisPool jedisPool;

    //用户在线体检预约发送验证码
    @RequestMapping("/send4Order")
    public Result send4Order(String mail){
        //随机生成一个四位数字验证码
        Integer code = ValidateCodeUtils.generateValidateCode(4);
        boolean send = validateService.send(mail,code);
        if (!send){
            return new Result(false,MessageConstant.SEND_VALIDATECODE_FAIL);
        }
        //将验证码保存到redis（5分钟）
        jedisPool.getResource().setex(mail + RedisMessageConstant.SENDTYPE_ORDER,300,code.toString());
        return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }

    //用户手机快速登录时发送验证码
    @RequestMapping("/send4Login")
    public Result send4Login(String mail){
        //随机生成一个六位数字验证码
        Integer code = ValidateCodeUtils.generateValidateCode(6);
        boolean send = validateService.send(mail,code);
        if (!send){
            return new Result(false,MessageConstant.SEND_VALIDATECODE_FAIL);
        }
        //将验证码保存到redis（5分钟）
        jedisPool.getResource().setex(mail + RedisMessageConstant.SENDTYPE_LOGIN,300,code.toString());
        return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }

}
