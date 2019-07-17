package com.zmh.miaosha.service;

import com.zmh.miaosha.error.BusinessException;
import com.zmh.miaosha.service.model.UserModel;

public interface UserService {

    UserModel getUserById(String id);

    void register(UserModel userModel) throws BusinessException;

    UserModel login(String telphone,String password) throws BusinessException;
}
