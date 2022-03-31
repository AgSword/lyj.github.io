package com.example.seckill.validator;

import com.example.seckill.utils.ValidatorUtil;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    boolean required = false;

    //初始化的时候，获取是不是必填的
    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(required){
            return ValidatorUtil.isMobile(s);
        }
        else {
            if(StringUtils.hasLength(s)){
                return ValidatorUtil.isMobile(s);
            }else {
                return true;
            }
        }
    }
}
