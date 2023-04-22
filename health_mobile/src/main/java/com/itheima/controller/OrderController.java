package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Order;
import com.itheima.service.OrderService;
import com.itheima.service.ValidateService;
import com.itheima.utils.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.Map;

/**
 * 体检预约处理
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private JedisPool jedisPool;
    @Reference
    private OrderService orderService;
    @Reference
    private ValidateService validateService;

    //在线体检预约
    @RequestMapping("/submit")
    public Result submit(@RequestBody Map map){
        String mail = (String) map.get("mail");
        //从Redis中获得保存的验证码
        String validateCodeInRedis = jedisPool.getResource().get(mail + RedisMessageConstant.SENDTYPE_ORDER);
        String validateCode = (String) map.get("validateCode");
        //将用户输入的与保存的验证码进行比较
        if (validateCodeInRedis != null && validateCode != null && validateCode.equals(validateCodeInRedis)){
            //如果比对成功，调用服务完成预约服务
            map.put("orderType", Order.ORDERTYPE_WEIXIN);//设置预约的类型
            Result result = null;
            try {
                result = orderService.order(map);//通过远程调用服务实现在线预约业务处理
            } catch (Exception e) {
                e.printStackTrace();
                return result;
            }
            if (result.isFlag()){
                //预约成功。可以为用户发送邮件
                validateService.send(mail, "预约成功，请您在" + (String)map.get("orderDate") + "及时到达");
            }
            return result;
        }else {
            //比对不成功，直接返回结果给页面
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
    }

    //根据预约ID查询预约相关信息
    @RequestMapping("/findById")
    public Result findById(Integer id){
        try {
            Map map = orderService.findById(id);
            return new Result(true,MessageConstant.QUERY_ORDER_SUCCESS,map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_ORDER_FAIL);
        }
    }
}
