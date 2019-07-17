package com.zmh.miaosha.service.impl;

import com.zmh.miaosha.dao.OrderDoMapper;
import com.zmh.miaosha.dao.SequenceDoMapper;
import com.zmh.miaosha.dataobject.OrderDo;
import com.zmh.miaosha.dataobject.SequenceDo;
import com.zmh.miaosha.error.BusinessException;
import com.zmh.miaosha.error.EmBusinessError;
import com.zmh.miaosha.service.ItemService;
import com.zmh.miaosha.service.OrderService;
import com.zmh.miaosha.service.UserService;
import com.zmh.miaosha.service.model.ItemModel;
import com.zmh.miaosha.service.model.OrderModel;
import com.zmh.miaosha.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderDoMapper orderDoMapper;

    @Autowired
    private SequenceDoMapper sequenceDoMapper;

    /**
     * 创建订单
     * @param userId 用户
     * @param itemId 商品
     * @param amount 数量
     * @return
     */
    @Override
    public OrderModel createOrder(String userId, String itemId, String promoId,Integer amount) throws BusinessException {
        //1.校验下单状态，下单的商品是否存在，用户是否合法，购买数量是否正确
        ItemModel itemModel = itemService.getItemById(itemId);
        //1.1下单的商品是否存在
        if (itemModel == null) {
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }
        //1.2 用户是否合法
        UserModel userModel=userService.getUserById(userId);
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }
        //1.3 购买数量是否正确
        if (amount<=0 || amount>99) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"数量信息不合法");
        }
        //1.4 校验活动id
        if (promoId != null) {
            // 校验活动是否存在这个适用商品
            if (!promoId.equals(itemModel.getPromoModel().getId())) {
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动信息不合法");
            }
            //校验活动是否正在进行中
            else if(!itemModel.getPromoModel().getStatus().equals(2)){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动还未开始");
            }
        }
        //2.落单减库存
        boolean result = itemService.decreaseStock(itemId, amount);
        if (!result) {
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }
        //3.订单入库
        OrderModel orderModel=new OrderModel();
        orderModel.setAmount(amount);
        orderModel.setItemId(itemId);
        orderModel.setUserId(userId);
        if (promoId != null) {
            orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else{
            orderModel.setItemPrice(itemModel.getPrice());
        }
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(new BigDecimal(amount)));
        orderModel.setId(generarteOrderNo());
        orderModel.setPromoId(promoId);
        OrderDo orderDo=convertFromModelToDO(orderModel);
        orderDoMapper.insertSelective(orderDo);

        //加上商品的销量
        itemService.increaseSales(itemId,amount);
        //4.返回前端
        return orderModel;
    }

    /**
     * 生成ID
     */
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public String generarteOrderNo(){
      //订单号16位
        StringBuilder stringBuilder=new StringBuilder();
        //前8位为时间信息，年月日
        LocalDateTime now=LocalDateTime.now();
        String nowDate=now.format(DateTimeFormatter.ISO_DATE).replace("-","");
        stringBuilder.append(nowDate);
     //中间6位为自增序列
     //   TODO 不太理解
        int sequence=0;
        SequenceDo sequenceDo = sequenceDoMapper.getSequenceByName("order_info");
        sequence=sequenceDo.getCurrentValue();
        sequenceDo.setCurrentValue(sequenceDo.getCurrentValue()+sequenceDo.getStep());
        sequenceDoMapper.updateByPrimaryKeySelective(sequenceDo);
        String valueOf = String.valueOf(sequence);
        for (int i = 0; i < 6- valueOf.length(); i++) {
            stringBuilder.append(0);
        }
        stringBuilder.append(valueOf);
        //最后2位为分库分表位
        stringBuilder.append("00");

        return stringBuilder.toString();
    }

    /**
     * 类型转换
     * @param orderModel
     * @return
     */
    private OrderDo convertFromModelToDO(OrderModel orderModel){
        if (orderModel == null) {
            return null;
        }
        OrderDo orderDo=new OrderDo();
        BeanUtils.copyProperties(orderModel,orderDo);
        orderDo.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderDo.setOrderPrice(orderModel.getOrderPrice().doubleValue());
        return orderDo;
    }
}
