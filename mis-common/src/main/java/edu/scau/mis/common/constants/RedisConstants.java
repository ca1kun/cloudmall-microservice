package edu.scau.mis.common.constants;

public class RedisConstants {
    private RedisConstants(){}
    public static final String ID_PREFIX = "icr:";
    public static final String CACHE_PRODUCT_KEY = "cache:product:all:";
    public static final Long CACHE_PRODUCT_TTL = 30L; // 缓存时间，单位分钟
    // 验证码 Key 前缀，后面拼接手机号，例如: captcha_codes:13800138000
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";
    // 验证码有效期 (分钟)
    public static final Integer CAPTCHA_EXPIRATION = 5;
}
