package com.zmh.miaosha.controller;


import com.zmh.miaosha.controller.viewobject.ItemVO;
import com.zmh.miaosha.error.BusinessException;
import com.zmh.miaosha.response.CommonReturnType;
import com.zmh.miaosha.service.ItemService;
import com.zmh.miaosha.service.model.ItemModel;
import io.swagger.annotations.ApiOperation;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/item")
public class ItemController extends BaseController {


    @Autowired
    private ItemService itemService;

    @Autowired
    private RedisTemplate redisTemplate;


    // 创建商品
    @PostMapping("/create")
    @ApiOperation(value = "创建商品")
    public CommonReturnType createItem(@RequestParam(name = "title") String title,
                                       @RequestParam(name = "description") String description,
                                       @RequestParam(name = "price") BigDecimal price,
                                       @RequestParam(name = "stock") String stock,
                                       @RequestParam(name = "imgUrl") String imgUrl) throws BusinessException {
        // 封装service请求用来创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setDescription(description);
        itemModel.setImgUrl(imgUrl);

        ItemModel itemModelforReturn = itemService.createItem(itemModel);
        ItemVO itemVo = new ItemVO();
        if (itemModel == null) {
            return null;
        }
        BeanUtils.copyProperties(itemModelforReturn,itemVo);
        return CommonReturnType.create(itemVo);
    }

    /**
     * 商品详情页浏览
     */
    @GetMapping(value = "/get")
    public CommonReturnType getItem(@RequestParam(name = "id") String id) throws BusinessException {
        //根据商品的id到reids内获取商品
        String keyItem="item_id"+":"+id;
        ItemModel itemModel= (ItemModel) redisTemplate.opsForValue().get(keyItem);
        //若redis内不存在对应的itemModel，则访问数据库
        if (itemModel == null) {
             itemModel=itemService.getItemById(id);
        //设置itemModel放到redis内
            redisTemplate.opsForValue().set(keyItem,itemModel);
            redisTemplate.expire(keyItem,10, TimeUnit.MINUTES);
        }

        ItemVO itemVO=convertVoFromModel(itemModel);
        return CommonReturnType.create(itemVO);
    }

    /**
     * 商品列表页面浏览
     */
    @GetMapping(value = "/list")
    public CommonReturnType listItem(){
        List<ItemModel> itemModelList=itemService.listItem();

        //使用stream api将list内的itemModel转化为itemVo
        List<ItemVO> itemVOList= itemModelList.stream().map(itemModel -> {
            ItemVO itemVO=convertVoFromModel(itemModel);
            return itemVO;
        }).collect(Collectors.toList());
        return CommonReturnType.create(itemVOList);
    }

    /**
     * itemModel转化为itemVo
     * @param itemModel
     * @return
     */
    public ItemVO convertVoFromModel(ItemModel itemModel){
        ItemVO itemVO=new ItemVO();
        if (itemModel == null) {
            return null;
        }
        BeanUtils.copyProperties(itemModel,itemVO);
        if (itemModel.getPromoModel() != null) {
            itemVO.setPromoStatus(itemModel.getPromoModel().getStatus());
            itemVO.setPromoId(itemModel.getPromoModel().getId());
            itemVO.setStartDate(itemModel.getPromoModel().getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            itemVO.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
        } else {
            itemVO.setPromoStatus(0);
        }
        return itemVO;
    }

}
