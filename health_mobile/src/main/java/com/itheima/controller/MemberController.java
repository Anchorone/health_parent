package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * 处理会员相关工作
 */
@RestController
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private JedisPool jedisPool;
    @Reference
    private MemberService memberService;

    //手机号快速登录
    @RequestMapping("/login")
    public Result login(HttpServletResponse response, @RequestBody Map map){
        String mail = (String) map.get("mail");
        String validateCode = (String) map.get("validateCode");
        String telephone = (String) map.get("telephone");
        //从Redis中获取保存的验证码
        String validateCodeInRedis = jedisPool.getResource().get(mail + RedisMessageConstant.SENDTYPE_LOGIN);
        if (validateCode != null && validateCodeInRedis != null && validateCode.equals(validateCodeInRedis)){
            //验证码输入正确
            //判断当前用户是否是会员
            Member member = memberService.findByTelephone(telephone);
            if (member == null){
                //不是会员，自动完成注册（将会员信息保存到会员表）
                member = new Member();
                member.setRegTime(new Date());
                member.setPhoneNumber(telephone);
                member.setEmail(mail);
                memberService.add(member);
            }
            //向客户端浏览器写入cookie，内容是手机号
            Cookie cookie = new Cookie("login_member_telephone",telephone);
            cookie.setPath("/");//路径
            cookie.setMaxAge(60*60*24*30);//有效期30天
            response.addCookie(cookie);
            //将会员信息保存到redis
            String json = JSON.toJSON(member).toString();
            jedisPool.getResource().setex(telephone,60*30,json);
            return new Result(true,MessageConstant.LOGIN_SUCCESS);
        }else {
            //验证码输入错误
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
    }
}
