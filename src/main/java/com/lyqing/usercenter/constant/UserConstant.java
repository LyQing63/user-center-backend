package com.lyqing.usercenter.constant;

/**
 * @auther lyqing
 */
public interface UserConstant {

    /**
     * 用户登录状态
     */
    String USER_LOGIN_STATE = "userLoginState";

    // ----------权限----------

    /**
     * 默认权限
     */
    int DEFAULT_ROLE = 0;

    /**
     * 管理员权限
     */
    int ADMIN_ROLE = 1;

    /**
     * userAccount 最短长度
     */
    int MIN_USER_ACCOUNT_LENGTH = 4;

    /**
     * 密码最短长度
     */
    int MIN_USER_PASSWORD_LENGTH = 8;
}
