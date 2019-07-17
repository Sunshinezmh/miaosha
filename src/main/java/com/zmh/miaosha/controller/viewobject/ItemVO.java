package com.zmh.miaosha.controller.viewobject;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ItemVO {

    private String id;

    // 商品名称
    private String title;

    //价格
    private BigDecimal price;

    //商品库存
    private String stock;

    // 商品描述
    private String description;

    // 商品销量
    private String sales;

    // 图片
    private String imgUrl;

    // 记录商品是否在秒杀活动中,以及对应的状态 0:没有秒杀活动 1:代开始 2:进行中
    private Integer promoStatus;

    // 秒杀活动价格
    private BigDecimal promoPrice;

    // 秒杀活动id
    private String promoId;

    // 秒杀活动开始时间
    private String startDate;
}
