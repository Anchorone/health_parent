package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetmealDao {
    public void add(Setmeal setmeal);
    public void setSetmealAndCheckgroup(Map map);
    public Page<Setmeal> findByCondition(String queryString);
    public List<Setmeal> findAll();
    public Setmeal findById(Integer id);
    public List<Map<String,Object>> findSetmealCount();
}
