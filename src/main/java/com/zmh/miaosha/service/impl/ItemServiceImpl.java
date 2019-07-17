package com.zmh.miaosha.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.zmh.miaosha.dao.ItemDoMapper;
import com.zmh.miaosha.dao.ItemStockDoMapper;
import com.zmh.miaosha.dataobject.ItemDo;
import com.zmh.miaosha.dataobject.ItemStockDo;
import com.zmh.miaosha.error.BusinessException;
import com.zmh.miaosha.error.EmBusinessError;
import com.zmh.miaosha.service.ItemService;
import com.zmh.miaosha.service.PromoService;
import com.zmh.miaosha.service.model.ItemModel;
import com.zmh.miaosha.service.model.PromoModel;
import com.zmh.miaosha.validator.ValidationResult;
import com.zmh.miaosha.validator.ValidatorImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService{

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private ItemDoMapper itemDoMapper;

    @Autowired
    private ItemStockDoMapper itemStockDoMapper;

    @Autowired
    private PromoService promoService;


    @Override
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        //校验入参
        ValidationResult result = validator.validate(itemModel);
        if (result.isHasError()) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }
        //转化itemmodel->dataobject
        if (itemModel == null) {
            return null;
        }
        ItemDo itemDo= new ItemDo();
        BeanUtils.copyProperties(itemModel,itemDo);
        itemDo.setPrice(itemModel.getPrice().doubleValue());
        itemDo.setId(IdWorker.getIdStr());
        //写入数据库
        itemDoMapper.insertSelective(itemDo);

        ItemStockDo itemStockDo=new ItemStockDo();
        itemStockDo.setId(IdWorker.getIdStr());
        itemStockDo.setItemId(itemDo.getId());
        itemStockDo.setStock(itemModel.getStock());
        itemStockDoMapper.insertSelective(itemStockDo);

        //返回创建完成的对象
        return getItemById(itemDo.getId());
    }

    /**
     * 库存消减
     */
    @Override
    @Transactional
   public boolean decreaseStock(String itemId,Integer amount){
       int affectedRow = itemStockDoMapper.decreaseStock(itemId, amount);
        if (affectedRow>0) {
            //更新库存成功
            return true;
        }else{
            //更新库存失败
            return false;
        }
   }

    /**
     * 商品销量增加
     * @param itemId
     * @param amount
     */
    @Override
    @Transactional
    public void increaseSales(String itemId, Integer amount) {
        itemDoMapper.increaseSales(itemId,amount);
    }

    /**
     * 商品详情信息的展示
     * @return
     */
    @Override
    public List<ItemModel> listItem() {
        List<ItemDo> listItem=itemDoMapper.listItem();
        List<ItemModel> itemModelList=listItem.stream().map(itemDo -> {
            ItemStockDo itemStockDo=itemStockDoMapper.selectByItemId(itemDo.getId());
            ItemModel itemModel=convertItemModelFromDO(itemDo,itemStockDo);
            return itemModel;
        }).collect(Collectors.toList());


        return itemModelList;
    }

    @Override
    public ItemModel getItemById(String id) throws BusinessException {
        ItemDo itemDo=itemDoMapper.selectByPrimaryKey(id);
        if (itemDo == null) {
            return null;
        }
        //操作获得库存数量
        ItemStockDo itemStockDo=itemStockDoMapper.selectByItemId(itemDo.getId());
        //将dataObject 转换为model
        ItemModel itemModel=convertItemModelFromDO(itemDo,itemStockDo);

        //获取活动商品信息
        PromoModel promoModel = promoService.getPromoByItemId(itemModel.getId());
        if (promoModel != null && promoModel.getStatus() != 3) {
            itemModel.setPromoModel(promoModel);
        }

        return itemModel;
    }


    private ItemModel convertItemModelFromDO(ItemDo itemDo, ItemStockDo itemStockDo) {
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDo, itemModel);
        itemModel.setStock(itemStockDo.getStock());
        itemModel.setPrice(new BigDecimal(itemDo.getPrice()));
        return itemModel;
    }
}
