package leyou.auth.service;

import leyou.auth.client.UserClient;
import leyou.auth.entity.UserInfo;
import leyou.auth.properties.JwtProperties;
import leyou.auth.utils.JwtUtils;
import leyou.common.enums.ExceptionEnum;
import leyou.common.exception.LyException;
import leyou.user.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthService {
    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties props;


    public String login(String username, String password) {
        try {
            User user = userClient.queryUser(username, password);
            if (user == null) {
                throw new LyException(ExceptionEnum.USERNAME_OR_PASSWORD_ERROR);
            }
            UserInfo userInfo = new UserInfo(user.getId(), user.getUsername());
            //生成Token
            String token = JwtUtils.generateToken(userInfo, props.getPrivateKey(), props.getExpire());
            if (StringUtils.isBlank(token)) {
                throw new LyException(ExceptionEnum.USERNAME_OR_PASSWORD_ERROR);
            }
            return token;
        } catch (Exception e) {
            log.error("【授权中心】用户名和密码错误，用户名：{}", username,e);
            throw new LyException(ExceptionEnum.USERNAME_OR_PASSWORD_ERROR);
        }
    }
}
