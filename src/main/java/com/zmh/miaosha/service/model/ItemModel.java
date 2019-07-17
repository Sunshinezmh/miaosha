package com.zmh.miaosha.service.model;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ItemModel implements Serializable{

    private String id;

    // 商品名称
    @NotBlank(message = "商品名称不能为空")
    private String title;

    //价格
    @NotNull(message = "商品价格不能为空")
    @Min(value = 0,message = "")
    private BigDecimal price;

    //商品库存
    @NotNull(message = "库存不能不填")
    private String stock;

    // 商品描述
    @NotNull(message = "商品描述信息不能为空")
    private String description;

    // 商品销量
    private String sales;

    // 图片
    @NotNull(message = "图片信息不能为空")
    private String imgUrl;

    // 使用聚合模型,如果promomodel不为空,则表示其拥有还未结束的秒杀活动
    private PromoModel promoModel;

}
