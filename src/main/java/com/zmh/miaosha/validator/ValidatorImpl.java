package com.zmh.miaosha.validator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

@Component
public class ValidatorImpl implements InitializingBean{

    private Validator validator;

    //实现校验方法并返回校验结果
    public ValidationResult validate(Object bean){
        final   ValidationResult result=new ValidationResult();
        Set<ConstraintViolation<Object>> validate = validator.validate(bean);
        if (validate.size()>0) {
            result.setHasError(true);
            validate.forEach(constraintViolation->{
                String errMsg=constraintViolation.getMessage();
                String propertyName=constraintViolation.getPropertyPath().toString();
                result.getErrorMsgMap().put(propertyName,errMsg);
            });
        }
        return result;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        this.validator= Validation.buildDefaultValidatorFactory().getValidator();
    }
}
