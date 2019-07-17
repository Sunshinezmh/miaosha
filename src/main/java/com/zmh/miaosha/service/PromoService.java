package com.zmh.miaosha.service;

import com.zmh.miaosha.error.BusinessException;
import com.zmh.miaosha.service.model.PromoModel;

public interface PromoService {

    /**
     * 根据itemId获取即将进行的或正在进行的秒杀活动
     * @param itemId
     * @return
     * @throws BusinessException
     */
    PromoModel getPromoByItemId(String itemId) throws BusinessException;
}
