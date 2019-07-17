package com.zmh.miaosha.controller;

import com.zmh.miaosha.controller.viewobject.UserVO;
import com.zmh.miaosha.error.BusinessException;
import com.zmh.miaosha.error.EmBusinessError;
import com.zmh.miaosha.response.CommonReturnType;
import com.zmh.miaosha.service.UserService;
import com.zmh.miaosha.service.model.UserModel;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping(value = "/user")
@Slf4j
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("get")
    public CommonReturnType getUser(@RequestParam(name = "id") String id) throws BusinessException {
        UserVO userVO=new UserVO();
        //调用service服务获取对应id的用户对象并返回给前端
        UserModel userModel=userService.getUserById(id);
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }
        BeanUtils.copyProperties(userModel,userVO);
        return CommonReturnType.create(userVO);
    }

    /**
     * 用户获取otp短信接口
     */
    @RequestMapping(value = "/getotp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType getOtp(@RequestParam(name = "telphone") String telphone){

        //按照一定的规则生成OTP验证码
        Random random=new Random();
        int randomInt=random.nextInt(99999);
        randomInt += 10000;
        String otpCode=String.valueOf(randomInt);
        //将OTP验证码与对应的手机号进行关联
        httpServletRequest.getSession().setAttribute(telphone,otpCode);
        log.info("数据：",httpServletRequest.getSession());
        //将OTP验证码通过短信通道发送给用户
        System.out.println("手机号"+telphone+"验证码"+otpCode);
        return CommonReturnType.create(null);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @ApiOperation(value = "登录")
    public CommonReturnType login(@RequestParam(name = "telphone")String telphone,
                                  @RequestParam(name = "password") String password) throws BusinessException, NoSuchAlgorithmException {
        //入参校验
        if (StringUtils.isEmpty(telphone) || StringUtils.isEmpty(password)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        //用户登录
        UserModel userModel=userService.login(telphone,this.Md5Password(password));

        //将登录凭证加入到用户登录成功的session内
        //this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
        //this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);

        //优化--修改成若用户登录成功后将对应的登录信息和登录凭证一起存入redis中
        //生成登录凭证token UUID
        String uuidToken =UUID.randomUUID().toString();
        uuidToken = uuidToken.replace("-","");
        redisTemplate.opsForValue().set(uuidToken,userModel);
        redisTemplate.expire(uuidToken,1, TimeUnit.HOURS);

        // 下发token
        return CommonReturnType.create(uuidToken);
    }


    /**
     * 用户注册
     */
    @PostMapping("/register")
    @ApiOperation(value = "注册")
    public CommonReturnType register(@RequestParam(name = "telphone") String telphone,
                                     @RequestParam(name = "otpCode") String otpCode,
                                     @RequestParam(name = "name") String name,
                                     @RequestParam(name = "gender") Byte gender,
                                     @RequestParam(name = "password") String password,
                                     @RequestParam(name = "age") Integer age) throws BusinessException, NoSuchAlgorithmException {

        //验证手机号和验证码是否相符合
        String inSesssionOtpCode=this.httpServletRequest.getSession().getAttribute(telphone).toString();
        if (!StringUtils.equals(otpCode,inSesssionOtpCode)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"短信验证码不符合");
        }
        UserModel userModel=new UserModel();
        userModel.setName(name);
        userModel.setGender(gender);
        userModel.setAge(age);
        userModel.setTelphone(telphone);
        userModel.setRegisterMode("byphone");
        userModel.setEncrptPassward(Md5Password(password));
        userService.register(userModel);
        return CommonReturnType.create(null);

    }

    public String Md5Password(String password) throws NoSuchAlgorithmException {
        MessageDigest digest=MessageDigest.getInstance("md5");
        byte[] result=digest.digest(password.getBytes());
        StringBuffer buffer=new StringBuffer();
        //把每一个byte做一个与运算oxff
        for (byte b : result) {
            int number = b & 0xff;// 加盐
            String str = Integer.toHexString(number);
            if (str.length() == 1) {
                buffer.append("0");
            }
            buffer.append(str);

        }
        // 标准的md5加密后的结果
        return buffer.toString();
    }

}
