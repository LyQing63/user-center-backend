package com.lyqing.usercenter.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyqing.usercenter.model.domain.User;
import com.lyqing.usercenter.service.UserService;
import com.lyqing.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.lyqing.usercenter.constant.UserConstant.*;

/**
* @author yjxx_2022
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-02-11 16:36:59
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;

    private static final String SALT = "lyqing";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            // TODO: 转换为自定义错误类型
            return -1;
        }
        // 账户长度不小于4，密码长度不小于8
        if (userAccount.length() < MIN_USER_ACCOUNT_LENGTH || userPassword.length() < MIN_USER_PASSWORD_LENGTH) {
            return -1;
        }
        // 校验密码
        if (!checkPassword.equals(userPassword)) {
            return -1;
        }
        // 账户无特殊字符
        String validPattern = "^[a-zA-Z0-9_]+$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.matches()) {
            return -1;
        }
        // 账户不重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount" ,userAccount);
        Long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            return -1;
        }
        
        // 加密

        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean result = this.save(user);
        if (!result) {
            return -1;
        }

        return user.getId();
    }


    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            // TODO 转换为自定义错误类型
            return null;
        }
        // 账户长度不小于4，密码长度不小于8
        if (userAccount.length() < MIN_USER_ACCOUNT_LENGTH || userPassword.length() < MIN_USER_PASSWORD_LENGTH) {
            return null;
        }

        // 账户无特殊字符
        String validPattern = "^\\w+$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.matches()) {
            return null;
        }

        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 账户不重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount" ,userAccount);
        queryWrapper.eq("userPassword" ,encryptPassword);
        User user = userMapper.selectOne(queryWrapper);

        // 用户名与密码不匹配
        if (user == null) {
            log.info("user login failed, user account is not matching password");
            return null;
        }
        User safeUser = safetyUser(user);
        request.getSession().setAttribute(USER_LOGIN_STATE, safeUser);

        return safeUser;
    }

    /**
     * 通过username模糊搜索脱敏后数据
     *
     * @param username 用户名
     * @return List of User 用户数据
     */
    @Override
    public List<User> searchUsersByName(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }

        return userMapper.selectList(queryWrapper).stream()
                .map(this::safetyUser).collect(Collectors.toList());
    }

    /**
     * 用户数据脱敏
     *
     * @param originUser 未脱敏数据
     * @return safeUser 脱敏后数据
     */
    private User safetyUser(User originUser) {
        User safeUser = new User();
        safeUser.setId(originUser.getId());
        safeUser.setUserAccount(originUser.getUserAccount());
        safeUser.setUsername(originUser.getUsername());
        safeUser.setGender(originUser.getGender());
        safeUser.setUserPassword(null);
        safeUser.setPhone(originUser.getPhone());
        safeUser.setEmail(originUser.getEmail());
        safeUser.setUserStatus(originUser.getUserStatus());
        safeUser.setAvatarUrl(originUser.getAvatarUrl());
        safeUser.setCreateTime(originUser.getCreateTime());
        safeUser.setUserRole(originUser.getUserRole());

        return safeUser;
    }

}




