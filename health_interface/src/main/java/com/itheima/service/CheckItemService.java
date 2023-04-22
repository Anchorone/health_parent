package com.itheima.service;


import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckItem;

import java.util.List;


public interface CheckItemService {
    void add(CheckItem checkItem);
    PageResult pageQuery(QueryPageBean queryPageBean);
    void deleteById(Integer id);
    void edit(CheckItem checkItem);
    CheckItem findById(Integer id);
    List<CheckItem> findAll();
}
