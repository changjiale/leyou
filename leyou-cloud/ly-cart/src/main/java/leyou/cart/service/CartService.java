package leyou.cart.service;

import leyou.auth.entity.UserInfo;
import leyou.cart.interceptor.UserInterceptor;
import leyou.cart.pojo.Cart;
import leyou.common.enums.ExceptionEnum;
import leyou.common.exception.LyException;
import leyou.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String key_prefix = "cart:user:id";

    public void addCart(Cart cart) {
        //获取登录用户
        UserInfo user = UserInterceptor.getUser();
        //key
        String key = key_prefix + user.getId();
        //hashkEY
        String hashKey = cart.getSkuId().toString();
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);
        //判断当前购物商品是否存在
        if (operation.hasKey(hashKey)) {
            //存在,修改数量
             String json = operation.get(hashKey).toString();
            Cart cacheCart = JsonUtils.toBean(json, Cart.class);
            cacheCart.setNum(cacheCart.getNum() + cart.getNum());
            //写入redis
            operation.put(hashKey, JsonUtils.toString(cacheCart));
        }else{
            //否 新增
            operation.put(hashKey, JsonUtils.toString(cart));
        }

    }

    public List<Cart> queryCartList() {
        //获取登录用户
        UserInfo user = UserInterceptor.getUser();
        //key
        String key = key_prefix+ user.getId();
        //获取当前用户的所有购物车
        if (!redisTemplate.hasKey(key)){
            //key不存在 返回404
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);

        }
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);

        List<Cart> carts = operation.values().stream()
                .map(o -> JsonUtils.toBean(o.toString(), Cart.class))
                .collect(Collectors.toList());

        return carts;
    }

    public void updateNum(Long skuId, Integer num) {
        //获取登录用户
        UserInfo user = UserInterceptor.getUser();
        //key
        String key = key_prefix+ user.getId();
        //hashkey
        String hashkey = skuId.toString();

        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);

        //获取当前用户的所有购物车
        if (!redisTemplate.hasKey(key)){
            //key不存在 返回404
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);

        }
        //查询
        Cart cart = JsonUtils.toBean(operation.get(hashkey).toString(), Cart.class);
        cart.setNum(num);

        //写回redis
        operation.put(hashkey, JsonUtils.toString(cart));


    }

    public void deleteCart(Long skuId) {
        //获取登录用户
        UserInfo user = UserInterceptor.getUser();
        //key
        String key = key_prefix+ user.getId();

        //删除
        redisTemplate.opsForHash().delete(key, skuId.toString());

    }
}
