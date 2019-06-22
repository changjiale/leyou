package leyou;

import leyou.auth.entity.UserInfo;
import leyou.auth.utils.JwtUtils;
import leyou.auth.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {

    private static final String pubKeyPath = "E:\\code\\rsa\\rsa.pub";

    private static final String priKeyPath = "E:\\code\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void parseToken() {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTU2MTAwODQyMH0.Kh03-Y7cSnLuhe4wJbnbKw5KZlJUFCDyO7CQFJcuqOSM47FIc_xjU1aBwd2CTznHGuhwpqe9s05IB7PzSv_d139Es7RvUCU7whITWnscJQUUeuCe9-DfuLYUCD2SAzPq8WApDd2b0hO_lQWoHdSoRYJ5YbV5vIR8tVdn8Pj9oxY";
        UserInfo userInfo = JwtUtils.getUserInfo(publicKey, token);
        System.out.println("id:" + userInfo.getId());
        System.out.println("name:" + userInfo.getName());
    }
}