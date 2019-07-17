package com.zmh.miaosha.service.model;

import lombok.Data;

import java.math.BigDecimal;

//用户下单的交易领域模型
@Data
public class OrderModel {

    //交易ID
    private String id;

    //购买的用户ID
    private String userId;

    // 若非空,则表示以秒杀方式下单
    private String promoId;

    //购买单价,若promoId非空,则表示秒杀商品价格
    private BigDecimal itemPrice;

    //购买的商品ID
    private String itemId;

    //购买的数量
    private Integer amount;

    //购买金额,若promoId非空,则表示秒杀商品价格
    private BigDecimal orderPrice;

}
