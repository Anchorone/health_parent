package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.OrderSettingDao;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 预约服务设置
 */
@Service(interfaceClass = OrderSettingService.class)
@Transactional
public class OrderSettingServiceImpl implements OrderSettingService{

    @Autowired
    private OrderSettingDao orderSettingDao;

    //批量导入预约设置数据
    @Override
    public void add(List<OrderSetting> list) {
        if (list != null && list.size() > 0){
            for (OrderSetting orderSetting : list){
                //判断当前日期是否已经进行预约设置
                long countByOrderDate = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
                if (countByOrderDate > 0){
                    //已经进行预约设置，执行更新操作
                    orderSettingDao.editNumberByOrderDate(orderSetting);
                }
                else {
                    //没有进行预约设置，执行插入操作
                    orderSettingDao.add(orderSetting);
                }
            }
        }
    }

/*    //根据月份查询对应的预约设置数据
    @Override
    public List<Map> getOrderSettingByMonth(String date) {//格式：
        String begin = date + "-1";
        String end = date + "-30";
        Map<String,String> map = new HashMap<>();
        map.put("begin",begin);
        map.put("end",end);
        List<OrderSetting> list = orderSettingDao.getOrderSettingByMonth(map);
        List<Map> result = new ArrayList<>();
        if (list != null && list.size() > 0){
            for (OrderSetting orderSetting : list){
                Map<String,Object> m = new HashMap<>();
                m.put("date",orderSetting.getOrderDate().getDate());//获取日期数字（几号）
                m.put("number",orderSetting.getNumber());
                m.put("reservations",orderSetting.getReservations());
                result.add(m);
            }
        }
        return result;
    }*/

    //根据月份查询对应的预约设置数据
    @Override
    public List<Map> getOrderSettingByMonth(String date) {//格式：
        String[] splitList = date.split("-");
        //判断是否为月份只有一个字符
        if (splitList[1].length() == 1){
            splitList[1] = "0" + splitList[1];
        }
        Map<String,String> map = new HashMap<>();
        map.put("year",splitList[0]);
        map.put("month",splitList[1]);
        List<OrderSetting> list = orderSettingDao.getOrderSettingByMonth(map);
        List<Map> result = new ArrayList<>();
        if (list != null && list.size() > 0){
            for (OrderSetting orderSetting : list){
                Map<String,Object> m = new HashMap<>();
                m.put("date",orderSetting.getOrderDate().getDate());//获取日期数字（几号）
                m.put("number",orderSetting.getNumber());
                m.put("reservations",orderSetting.getReservations());
                result.add(m);
            }
        }
        return result;
    }

    //根据日期修改对应的预约设置数据
    @Override
    public void editNumberByDate(OrderSetting orderSetting) {

        Date orderDate = orderSetting.getOrderDate();
        //根据日期查询是否已经进行了预约设置
        long count = orderSettingDao.findCountByOrderDate(orderDate);
        if (count > 0){
             //当前日期已经进行了预约设置，需要执行更新操作
            orderSettingDao.editNumberByOrderDate(orderSetting);
        }else {
            //当前日期没有进行预约操作，需要执行插入操作
            orderSettingDao.add(orderSetting);
        }
    }

    /**
     *业务优化：
     *当预约满人的时候，修改的可预约人数须超过预约人数
     */
}
