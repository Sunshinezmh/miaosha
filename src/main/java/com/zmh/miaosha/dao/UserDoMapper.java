package com.zmh.miaosha.dao;

import com.zmh.miaosha.dataobject.UserDo;

public interface UserDoMapper {
    int deleteByPrimaryKey(String id);

    int insert(UserDo record);

    int insertSelective(UserDo record);

    UserDo selectByPrimaryKey(String id);

    UserDo selectBytelphone(String telphone);

    int updateByPrimaryKeySelective(UserDo record);

    int updateByPrimaryKey(UserDo record);
}