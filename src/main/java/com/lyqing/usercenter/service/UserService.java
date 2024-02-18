package com.lyqing.usercenter.service;

import com.lyqing.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lyqing.usercenter.model.domain.request.UserRegisterRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author yjxx_2022
* @description 针对表【user】的数据库操作Service
* @createDate 2024-02-11 16:36:59
*/
public interface UserService extends IService<User> {
    /**
     * 注册业务实现
     *
     * @auther lyqing
     * @param userPassword
     * @return user id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);

    /**
     * 登录业务实现
     *
     * @param userAccount 账户
     * @param userPassword 密码
     * @param request 请求体
     * @return user 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 通过username搜索usr
     *
     * @param username 用户名
     * @return List of user 数据
     */
    List<User> searchUsersByName(String username);

    /**
     * 获取脱敏后的用户信息
     *
     * @param originUser 未脱敏用户信息
     * @return 脱敏后的用户信息
     */
    User safetyUser(User originUser);

    /**
     * 用户注销
     *
     * @param request
     */
    int userLogout(HttpServletRequest request);
}
