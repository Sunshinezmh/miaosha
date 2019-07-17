package com.zmh.miaosha.dao;

import com.zmh.miaosha.dataobject.ItemStockDo;
import org.apache.ibatis.annotations.Param;

public interface ItemStockDoMapper {
    int deleteByPrimaryKey(String id);

    int insert(ItemStockDo record);

    int insertSelective(ItemStockDo record);

    ItemStockDo selectByPrimaryKey(String id);

    ItemStockDo selectByItemId(String itemId);
    int updateByPrimaryKeySelective(ItemStockDo record);

    int updateByPrimaryKey(ItemStockDo record);

    int decreaseStock(@Param("itemId") String itemId, @Param("amount") Integer amount);
}