package com.zmh.miaosha.service;

import com.zmh.miaosha.error.BusinessException;
import com.zmh.miaosha.service.model.OrderModel;

public interface OrderService {

    /**
     * 创建订单
     * @param userId 用户
     * @param itemId 商品
     * @param amount 数量
     * @return
     */
    // 通过url上传过来秒杀活动的id,然后下单接口内校验对应id是否属于对应商品且活动已开始
    OrderModel createOrder(String userId,String itemId,String promoId,Integer amount) throws BusinessException;
}
