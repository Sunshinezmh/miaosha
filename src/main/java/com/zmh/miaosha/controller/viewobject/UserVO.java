package com.zmh.miaosha.controller.viewobject;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserVO {

    private Integer id;
    @NotBlank(message = "用户名不能为空")
    private String name;

    @NotNull(message = "性别不能不填")
    private Byte gender;
    private Integer age;
    @NotNull(message = "手机号不能不填")
    private String telphone;
}
