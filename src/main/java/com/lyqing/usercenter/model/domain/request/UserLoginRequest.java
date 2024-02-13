package com.lyqing.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 *
 * @auther lyqing
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = -1068898403016244124L;

    private String userAccount;

    private String userPassword;
}
