package com.zmh.miaosha.dao;

import com.zmh.miaosha.dataobject.UserPasswordDo;

public interface UserPasswordDoMapper {
    int deleteByPrimaryKey(String id);

    int insert(UserPasswordDo record);

    int insertSelective(UserPasswordDo record);

    UserPasswordDo selectByPrimaryKey(String id);

    UserPasswordDo selectByUserId(String userId);

    int updateByPrimaryKeySelective(UserPasswordDo record);

    int updateByPrimaryKey(UserPasswordDo record);
}