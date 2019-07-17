package com.zmh.miaosha.dao;

import com.zmh.miaosha.dataobject.PromoDo;

public interface PromoDoMapper {
    int deleteByPrimaryKey(String id);

    int insert(PromoDo record);

    int insertSelective(PromoDo record);

    PromoDo selectByPrimaryKey(String id);

    PromoDo selectByItemId(String itemId);

    int updateByPrimaryKeySelective(PromoDo record);

    int updateByPrimaryKey(PromoDo record);
}