package leyou.order.interceptor;

import leyou.auth.entity.UserInfo;
import leyou.auth.utils.JwtUtils;
import leyou.common.utils.CookieUtils;
import leyou.order.config.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class UserInterceptor implements HandlerInterceptor {

    private JwtProperties props;

    private static final ThreadLocal<UserInfo> tl = new ThreadLocal<>();

    public UserInterceptor(JwtProperties props) {
        this.props = props;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //获取cookie
        String token = CookieUtils.getCookieValue(request, props.getCookieName());
        try {
            //解析token
            UserInfo user = JwtUtils.getInfoFromToken(token,props.getPublicKey());
            //传递user
            //request.setAttribute("user",user);
            tl.set(user);
            //放行
            return true;
        }catch (Exception e){
            log.error("[订单微服务] 解析用户身份失败",e);
            return false;
        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        //最后用完数据，一定要清空
        tl.remove();
    }

    public static UserInfo getUser(){
        return tl.get();
    }
}
