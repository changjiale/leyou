package leyou.gateway.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import leyou.auth.entity.UserInfo;
import leyou.auth.utils.JwtUtils;
import leyou.common.exception.LyException;
import leyou.common.utils.CookieUtils;
import leyou.gateway.config.FilterProperties;
import leyou.gateway.config.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class AuthFilter extends ZuulFilter {
    @Autowired
    private JwtProperties props;
    @Autowired
    private FilterProperties filterProps;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;  //过滤器类型 前置过滤器
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 1; //过滤器顺序
    }

    @Override
    public boolean shouldFilter() {
        //获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = ctx.getRequest();
        //获取url
        String path = request.getRequestURI();

        //判断是否放行 ，放行则返回false  拦截返回true
        boolean isAllowPath = isAllowPath(path);
        return !isAllowPath;
    }

    private boolean isAllowPath(String path) {
        for (String allowPath : filterProps.getAllowPaths()){
            //判断是否允许
            if (path.startsWith(allowPath)){
                return true;
            }
        }
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        //获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        //获取cookie中的token
        HttpServletRequest request = ctx.getRequest();
        String token = CookieUtils.getCookieValue(request, props.getCookieName());
        //解析token
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token,props.getPublicKey());
            //TODO 权限管理
        }catch (Exception e){
            //解析token失败， 未登录拦截
            ctx.setSendZuulResponse(false);
            //返回状态码
            ctx.setResponseStatusCode(403);
        }

        return null;
    }
}
