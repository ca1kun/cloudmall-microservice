package edu.scau.mis.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    public static final long TTL = 60 * 60 * 1000L * 24; // 24小时
    public static final String KEY = "SCAU_MIS_SECRET_KEY_123456_MUST_BE_VERY_VERY_LONG_TO_SECURE"; // 密钥

    // 将密钥字符串缓存为 SecretKey 对象，避免重复创建
    private SecretKey secretKey;

    /**
     * 根据配置的密钥字符串生成 SecretKey 对象。
     * 这是 jjwt 推荐的、更安全的方式。
     */
    private SecretKey getSecretKey() {
        if (secretKey == null) {
            // 使用 Keys.hmacShaKeyFor 方法从字符串安全地生成密钥
            this.secretKey = Keys.hmacShaKeyFor(KEY.getBytes(StandardCharsets.UTF_8));
        }
        return this.secretKey;
    }

    /**
     * 创建JWT
     * @param subject 通常是用户ID
     * @return JWT字符串
     */
    public String createJWT(String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + TTL);

        // 使用新的、未被弃用的 API
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                // 使用 SecretKey 对象和指定的算法进行签名
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 解析JWT
     * @param jwt JWT字符串
     * @return Claims
     */
    public Claims parseJWT(String jwt) {
        // 使用新的、未被弃用的 API
        return Jwts.parserBuilder()
                // 设置用于验证签名的密钥
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }
}