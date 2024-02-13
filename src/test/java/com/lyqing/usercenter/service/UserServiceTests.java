package com.lyqing.usercenter.service;
import java.util.Date;

import com.lyqing.usercenter.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class UserServiceTests {

    @Resource
    private UserService userService;

    @Test
    void addUser() {
        User user = new User();
        user.setUserAccount("lyqing4");
        user.setUsername("lyqing4");
        user.setGender(0);
        user.setUserPassword("12345678");
        user.setPhone("12345678899");
        user.setEmail("123@123123.com");
        user.setUserStatus(0);
        user.setAvatarUrl("213124214");
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setIsDelete(0);
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);

    }

    @Test
    void userRegister() {
        // 为空检验
        String userAccount = "";
        String userPassword = "";
        String checkPassword = "";
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);
        // 账户长度不少于4
        userAccount = "ly";
        userPassword = "12345678";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);
        // 密码不少于89
        userAccount = "lyqing5";
        userPassword = "123";
        checkPassword = "123";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);
        // 账户不重复
        userAccount = "lyqing1";
        userPassword = "12345678";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);
        // 不包含特殊字符
        userAccount = "ly qing ";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);
        //检验密码
        userAccount = "lyqing5";
        checkPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);
        // 插入数据
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
//        Assertions.assertTrue(result>0);

    }
}
