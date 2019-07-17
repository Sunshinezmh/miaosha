package com.zmh.miaosha.service.impl;

import com.zmh.miaosha.dao.PromoDoMapper;
import com.zmh.miaosha.dataobject.PromoDo;
import com.zmh.miaosha.error.BusinessException;
import com.zmh.miaosha.service.PromoService;
import com.zmh.miaosha.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PromoServiceImpl implements PromoService {

    @Autowired
    private PromoDoMapper promoDoMapper;

    /**
     * 根据itemId获取即将进行的或正在进行的秒杀活动
     * @param itemId
     * @return
     * @throws BusinessException
     */
    @Override
    public PromoModel getPromoByItemId(String itemId) throws BusinessException {
        PromoDo promoDo = promoDoMapper.selectByItemId(itemId);
        PromoModel promoModel = convertFromDataObject(promoDo);

        if (promoModel == null) {
            return null;
        }
        //判断当前时间是否秒杀活动即将开始或者正在进行
        if (promoModel.getStartDate().isAfterNow()) {
            promoModel.setStatus(1);
        }else if(promoModel.getEndDate().isBeforeNow()){
            promoModel.setStatus(3);
        }else{
            promoModel.setStatus(2);
        }
        return promoModel;
    }

    private PromoModel convertFromDataObject(PromoDo promoDo) throws BusinessException {
        PromoModel promoModel=new PromoModel();
        if (promoDo == null) {
          return null;
        }
        BeanUtils.copyProperties(promoDo,promoModel);
        promoModel.setPromoItemPrice(new BigDecimal(promoDo.getPromoItemPrice()));
        promoModel.setStartDate(new DateTime(promoDo.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDo.getEndDate()));
        return promoModel;
    }
}
