package com.zmh.miaosha.controller;


import com.zmh.miaosha.error.BusinessException;
import com.zmh.miaosha.error.EmBusinessError;
import com.zmh.miaosha.response.CommonReturnType;
import com.zmh.miaosha.service.OrderService;
import com.zmh.miaosha.service.model.OrderModel;
import com.zmh.miaosha.service.model.UserModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/order")
public class OrderController extends  BaseController{

    @Autowired
    private OrderService orderService;
    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private RedisTemplate redisTemplate;

    //封装下单请求
    @RequestMapping(value = "/createorder",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType createOrder(@RequestParam(name = "itemId") String itemId,
                                        @RequestParam(name = "amount") Integer amount,
                                        @RequestParam(name = "promoId",required = false) String promoId) throws BusinessException {

    // 获取用户的登陆信息
    //    Boolean isLogin=(Boolean) this.httpServletRequest.getSession().getAttribute("IS_LOGIN");
        String token= this.httpServletRequest.getParameterMap().get("token")[0];
        if (StringUtils.isEmpty(token)) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户还未登陆");
        }
        UserModel userModel= (UserModel) redisTemplate.opsForValue().get(token);
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"用户信息为空");
        }
        OrderModel orderModel=orderService.createOrder(userModel.getId(),itemId,promoId,amount);

        return CommonReturnType.create(null);
    }
}
