package com.lyqing.usercenter.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyqing.usercenter.Exception.BusinessException;
import com.lyqing.usercenter.common.ErrorCode;
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

    private static final int SUCCESS = 1;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        // 账户长度不小于4
        if (userAccount.length() < MIN_USER_ACCOUNT_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户过短");
        }

        //密码长度不小于8位
        if (userPassword.length() < MIN_USER_PASSWORD_LENGTH || checkPassword.length() < MIN_USER_PASSWORD_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码过短");
        }

        // 校验密码
        if (!checkPassword.equals(userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码输入不同");
        }
        // 星球码长度限度
        if (planetCode.length() > MAX_PLANET_CODE_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编码过长");
        }
        // 账户无特殊字符
        String validPattern = "^\\w+$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户包含特殊字符");
        }
        // 账户不重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount" ,userAccount);
        Long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户已存在");
        }

        // 星球码不重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode" ,planetCode);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该星球编号用户已经被注册");
        }

        // 加密

        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean result = this.save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存用户信息出错");
        }

        return user.getId();
    }


    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        // 账户长度不小于4
        if (userAccount.length() < MIN_USER_ACCOUNT_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户过短");
        }

        //密码长度不小于8位
        if (userPassword.length() < MIN_USER_PASSWORD_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码过短");
        }

        // 账户无特殊字符
        String validPattern = "^\\w+$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户包含特殊字符");
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
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误或用户不存在");
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
    public User safetyUser(User originUser) {

        if (originUser == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "需脱敏用户为空");
        }

        User safeUser = new User();
        safeUser.setId(originUser.getId());
        safeUser.setUserAccount(originUser.getUserAccount());
        safeUser.setUsername(originUser.getUsername());
        safeUser.setGender(originUser.getGender());
        safeUser.setUserPassword(null);
        safeUser.setPhone(originUser.getPhone());
        safeUser.setEmail(originUser.getEmail());
        safeUser.setPlanetCode(originUser.getPlanetCode());
        safeUser.setUserStatus(originUser.getUserStatus());
        safeUser.setAvatarUrl(originUser.getAvatarUrl());
        safeUser.setCreateTime(originUser.getCreateTime());
        safeUser.setUserRole(originUser.getUserRole());

        return safeUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return SUCCESS;
    }

}




