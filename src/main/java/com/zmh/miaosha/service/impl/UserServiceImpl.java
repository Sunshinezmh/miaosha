package com.zmh.miaosha.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.zmh.miaosha.dao.UserDoMapper;
import com.zmh.miaosha.dao.UserPasswordDoMapper;
import com.zmh.miaosha.dataobject.UserDo;
import com.zmh.miaosha.dataobject.UserPasswordDo;
import com.zmh.miaosha.error.BusinessException;
import com.zmh.miaosha.error.EmBusinessError;
import com.zmh.miaosha.service.UserService;
import com.zmh.miaosha.service.model.UserModel;
import com.zmh.miaosha.validator.ValidationResult;
import com.zmh.miaosha.validator.ValidatorImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

  @Autowired
  private UserDoMapper userDoMapper;

  @Autowired
  private UserPasswordDoMapper userPasswordDoMapper;

   @Autowired
   private ValidatorImpl validator;
    @Override
    public UserModel getUserById(String id) {
        //调用userdomapper获取到对应的用户dataobject
        UserDo userDo=userDoMapper.selectByPrimaryKey(id);

        if (userDo == null) {
            return null;
        }
        //通过用户id获取对应的用户加密密码信息
        UserPasswordDo userPasswordDo=userPasswordDoMapper.selectByUserId(userDo.getId());

        return convertFromDataObject(userDo,userPasswordDo);
    }

    /**
     * 用户注册
     * @param userModel
     * @throws BusinessException
     */
       @Override
       @Transactional(rollbackFor = Exception.class)
    public void register(UserModel userModel) throws BusinessException {
        UserDo userDo=new UserDo();
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

       ValidationResult validate = validator.validate(userModel);
        if (validate.isHasError()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,validate.getErrMsg());
        }
        BeanUtils.copyProperties(userModel,userDo);
        userDo.setId(IdWorker.getIdStr());
        userDo.setThirdPartyId(IdWorker.getIdStr());
        userDoMapper.insertSelective(userDo);
        UserPasswordDo userPasswordDo=new UserPasswordDo();
        userPasswordDo.setId(IdWorker.getIdStr());
        userPasswordDo.setUserId(userDo.getId().toString());
        userPasswordDo.setEncrptPassword(userModel.getEncrptPassward());
        userPasswordDoMapper.insertSelective(userPasswordDo);
    }

    /**
     * 用户登录
     * @param telphone
     * @param password
     */
    @Override
    public UserModel login(String telphone, String password) throws BusinessException {
        //通过用户的手机获取用户信息
        UserDo userDo=userDoMapper.selectBytelphone(telphone);
        if (userDo==null){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        UserPasswordDo userPasswordDo=userPasswordDoMapper.selectByUserId(userDo.getId());
        UserModel userModel=convertFromDataObject(userDo,userPasswordDo);
        //对比用户信息内加密的密码是否和传输过来的密码一致
        if (!StringUtils.equals(password,userModel.getEncrptPassward())) {
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        return userModel;
    }


    private UserModel convertFromDataObject(UserDo userDo, UserPasswordDo userPasswordDo){
        if (userDo == null) {
            return null;
        }
        UserModel userModel=new UserModel();
        BeanUtils.copyProperties(userDo,userModel);
        if (userPasswordDo != null) {
            userModel.setEncrptPassward(userPasswordDo.getEncrptPassword());
        }
     return userModel;
    }
}
