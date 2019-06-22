package leyou.auth.web;

import io.jsonwebtoken.Jwt;
import leyou.auth.entity.UserInfo;
import leyou.auth.properties.JwtProperties;
import leyou.auth.service.AuthService;
import leyou.auth.utils.JwtUtils;
import leyou.common.enums.ExceptionEnum;
import leyou.common.exception.LyException;
import leyou.common.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties props;

    @Value("${ly.jwt.cookieName}")
    private String cookieName;

    @PostMapping("accredit")
    public ResponseEntity<Void> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String token = authService.login(username, password);
        System.out.println(request.getRequestURL());

        //将Token写入cookie中
        System.out.println(cookieName);
        CookieUtils.newBuilder(response).httpOnly().request(request).build(cookieName, token);
        return ResponseEntity.ok().build();
    }

    /**
     * 校验用户登录状态
     * @param token
     * @return
     */
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verify(@CookieValue("LY_TOKEN") String token,
                                           HttpServletRequest request,
                                           HttpServletResponse response){
        if(StringUtils.isBlank(token)){
            //如果没有token，证明未登录，返回403
            throw new LyException(ExceptionEnum.UN_AUTHORIZED);
        }
        //解析客户端
        try{
            UserInfo info = JwtUtils.getInfoFromToken(token,props.getPublicKey());

            //刷新token，重新生成
            String newToken = JwtUtils.generateToken(info, props.getPrivateKey(), props.getExpire());
            CookieUtils.newBuilder(response).httpOnly().request(request).build(cookieName, newToken);

            //已登录 返回用户信息
            return ResponseEntity.ok(info);
        }catch (Exception e){
            //token已过期
            throw new LyException(ExceptionEnum.UN_AUTHORIZED);
        }

    }
}
