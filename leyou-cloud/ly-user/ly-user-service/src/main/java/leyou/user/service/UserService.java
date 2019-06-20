package leyou.user.service;

import leyou.common.enums.ExceptionEnum;
import leyou.common.exception.LyException;
import leyou.common.utils.NumberUtils;
import leyou.user.mapper.UserMapper;
import leyou.user.pojo.User;
import leyou.user.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final static String key_prefix = "user:verify:phone:";

    public Boolean checkData(String data, Integer type) {
        User record = new User();
        //判断数据类型
        switch (type){
            case 1:
                record.setUsername(data);
                break;
            case 2:
                record.setPhone(data);
                break;
            default:
                throw new LyException(ExceptionEnum.INVALID_USER_DATA_TYPE);

        }
        return userMapper.selectCount(record) == 0;
    }

    public void sendCode(String phone) {
        //生成key
        String key = key_prefix+phone;
        //生成验证码
        String code = NumberUtils.generateCode(6);

        Map<String, String> msg = new HashMap<>();
        msg.put("phone",phone);
        msg.put("code",code);
        //发送验证码
        amqpTemplate.convertAndSend("ly.sms.exchange","sms.verity.code",msg);

        //保存验证码  失效时长5分钟
        redisTemplate.opsForValue().set(key, code,5, TimeUnit.MINUTES);

    }

    public void register(User user, String code) {
        user.setId(null);
        user.setCreated(new Date());
        String key = key_prefix + user.getPhone();

        String value = redisTemplate.opsForValue().get(key);

        if (!StringUtils.equals(code, value)) {
            //验证码不匹配
            throw new LyException(ExceptionEnum.VERIFY_CODE_NOT_MATCHING);
        }

        //生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);

        //生成密码
        String md5Pwd = CodecUtils.md5Hex(user.getPassword(), user.getSalt());

        user.setPassword(md5Pwd);

        //保存到数据库
        int count = userMapper.insert(user);

        if (count != 1) {
            throw new LyException(ExceptionEnum.INVALID_PARAM);
        }

        //把验证码从Redis中删除
        redisTemplate.delete(key);


    }

    public User queryUser(String username, String password) {
        User record = new User();
        record.setUsername(username);

        //首先根据用户名查询用户
        User user = userMapper.selectOne(record);

        if (user == null) {
            throw new LyException(ExceptionEnum.USER_NOT_EXIST);
        }

        //检验密码是否正确
        if (!StringUtils.equals(CodecUtils.md5Hex(password, user.getSalt()), user.getPassword())) {
            //密码不正确
            throw new LyException(ExceptionEnum.PASSWORD_NOT_MATCHING);
        }

        return user;
    }
}
