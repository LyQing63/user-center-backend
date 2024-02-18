package com.lyqing.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @auther lyqing
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -2297806816263410610L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;

    private String planetCode;
}
