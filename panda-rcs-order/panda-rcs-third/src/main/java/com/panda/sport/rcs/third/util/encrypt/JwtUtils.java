package com.panda.sport.rcs.third.util.encrypt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Beulah
 * @date 2023/3/28 19:09
 * @description jwt令牌工具
 */
@Slf4j
public class JwtUtils {

    //过期时间1天
    public static final long EXPIRE = 1000 * 60 * 60 * 24;
    //秘钥种子
    public static final String APP_SECRET = "ukc8BDbRigUDaY6pZFfWus2jZWLPHO";

    /**
     * 传入参数，获取jwt字符串
     *
     * @param args 可变参数
     * @return token
     */
    public static String getJwtToken(String... args) {

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setSubject("user")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))

                /*
                 * 在这里可以一直添加参数
                 */
                .claim("id", args[0])
                .claim("name", args[1])
                .signWith(SignatureAlgorithm.HS256, APP_SECRET)
                .compact();
    }

    /**
     * 判断token是否存在与有效，无效会抛异常
     */
    public static boolean checkToken(String jwtToken) {
        if (StringUtils.isEmpty(jwtToken)) return false;
        try {
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (MalformedJwtException e) {
            log.error("::{}::token无效", jwtToken);
            return false;
        } catch (ExpiredJwtException e) {
            log.error("::{}::token过期", jwtToken);
            return false;
        } catch (Exception e) {
            log.error("::{}::token解析失败", jwtToken);
            return false;
        }
        return true;
    }

    /**
     * 判断token是否存在与有效，无效会抛异常
     *
     * @param request
     * @return
     */
    public static boolean checkToken(HttpServletRequest request) {
        try {
            String jwtToken = request.getHeader("token");
            if (StringUtils.isEmpty(jwtToken)) return false;
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据token获取参数id
     *
     * @param jwtToken 请求参数
     * @return map
     */
    public static Map<String, Object> getMemberIdByJwtToken(String jwtToken) {
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();
        Map<String, Object> map = new HashMap<>();
        map.put("id", claims.get("id"));
        map.put("name", claims.get("name"));
        return map;
    }

    public static String getToken(HttpServletRequest request) throws IOException {
        String jwtToken = request.getHeader("token");
        if (jwtToken != null) {
            return jwtToken;
        }
        BufferedReader br = request.getReader();
        String str;
        StringBuilder wholeStr = new StringBuilder();
        while ((str = br.readLine()) != null) {
            wholeStr.append(str);
        }
        return wholeStr.toString();
    }


    public static void main(String[] args) {
        //生成toke
        String token = getJwtToken("123", "panda_rcs_third");
        System.out.println("token：" + token);

        //生成签名
        Map<String, Object> params = new HashMap<>();
        params.put("AuthToken", token);
        params.put("TS", "123456789");

        StringBuffer sb = new StringBuffer();
        params.forEach((k, v) -> {
            sb.append(k).append(v);
        });
        sb.append("sharedKey");
        String sign = MD5Util.sha256Hex(sb.toString());

        System.out.println("签名：" + sign);
        /*try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
        System.out.println(checkToken(token));

    }
}
