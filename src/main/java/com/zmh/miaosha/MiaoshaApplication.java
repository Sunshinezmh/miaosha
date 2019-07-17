package com.zmh.miaosha;

import com.zmh.miaosha.dao.UserDoMapper;
import com.zmh.miaosha.dataobject.UserDo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@SpringBootApplication
@MapperScan("com.zmh.miaosha.dao")
public class MiaoshaApplication {
    public static void main(String[] args) {

        SpringApplication.run(MiaoshaApplication.class,args);
    }


    @Autowired
    private UserDoMapper userDoMapper;

    @RequestMapping("/")
    public String home(){
        UserDo userDo=userDoMapper.selectByPrimaryKey("1");
        if (userDo == null) {
            return "用户记录不存在";
        }else{
            return userDo.getName();
        }
    }
}
