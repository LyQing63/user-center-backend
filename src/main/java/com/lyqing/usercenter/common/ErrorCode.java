package com.lyqing.usercenter.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码
 *
 * @auther lyqing
 */
@AllArgsConstructor
@Getter
public enum ErrorCode {
    /**
     * 成功
     */
    SUCCESS(400, "成功", ""),
    /**
     * 请求参数错误
     */
    PARAMS_ERROR(40000, "请求参数错误", ""),
    /**
     * 请求数据为空
     */
    NULL_ERROR(40001, "请求数据为空", ""),
    /**
     * 未登录
     */
    NOT_LOGIN(40100, "未登录", ""),
    /**
     * 无权限
     */
    NOT_AUTH(40101, "无权限", ""),
    /**
     * 系统内部异常
     */
    SYSTEM_ERROR(50000, "系统内部异常", "");


    /**
     * 状态码
     */
    private final int code;
    /**
     * 状态码信息
     */
    private final String message;
    /**
     * 状态码描述
     */
    private final String description;

}
