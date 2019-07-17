package com.zmh.miaosha.service;

import com.zmh.miaosha.error.BusinessException;
import com.zmh.miaosha.service.model.ItemModel;
import io.swagger.models.auth.In;

import java.util.List;

public interface ItemService {

    // 创建商品
    ItemModel createItem(ItemModel itemModel) throws BusinessException;

    // 商品列表浏览
    List<ItemModel> listItem();

    // 商品详情浏览
    ItemModel getItemById(String id) throws BusinessException;

    //库存扣减
    boolean decreaseStock(String itemId,Integer amount);

    //商品销量增加
    void increaseSales(String itemId, Integer amount);
}
