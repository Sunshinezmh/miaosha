package com.zmh.miaosha.dao;

import com.zmh.miaosha.dataobject.ItemDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemDoMapper {
    int deleteByPrimaryKey(String id);

    int insert(ItemDo record);

    int insertSelective(ItemDo record);

    ItemDo selectByPrimaryKey(String id);

    List<ItemDo> listItem();

    int updateByPrimaryKeySelective(ItemDo record);

    int updateByPrimaryKey(ItemDo record);

    int increaseSales(@Param(value = "id") String itemId, @Param(value = "amount") Integer amount);
}