package com.lyqing.usercenter.controller;

import com.lyqing.usercenter.model.domain.User;
import com.lyqing.usercenter.model.domain.request.UserLoginRequest;
import com.lyqing.usercenter.model.domain.request.UserRegisterRequest;
import com.lyqing.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.lyqing.usercenter.constant.UserConstant.ADMIN_ROLE;
import static com.lyqing.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author lyqing
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return null;
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }

        return userService.userRegister(userAccount, userPassword, checkPassword);
    }

    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }

        return userService.userLogin(userAccount, userPassword, request);
    }

    @GetMapping("/search")
    public List<User> userSearch(String username, HttpServletRequest request) {
        if (isAdmin(request)) {
            return  new ArrayList<>();
        }
        return userService.searchUsersByName(username);
    }

    @PostMapping("/delete")
    public boolean userDelete(@RequestBody long id, HttpServletRequest request) {
        if (isAdmin(request)) {
            return false;
        }
        if (id <= 0) {
            return false;
        }
        return userService.removeById(id);
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        return user != null && user.getUserRole() != ADMIN_ROLE;
    }
}
