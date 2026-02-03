package edu.scau.mis.common.domain;

import lombok.Getter;

@Getter
public enum HttpCode {
    SUCCESS(200, "操作成功"),
    CREATED(201, "资源创建成功"),
    ACCEPTED(202, "请求已接受"),
    NO_CONTENT(204, "无内容"),

    BAD_REQUEST(400, "请求参数不合法"),
    UNAUTHORIZED(401, "未授权，请登录"),
    FORBIDDEN(403, "权限不足，禁止访问"),
    NOT_FOUND(404, "请求资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    REQUEST_TIMEOUT(408, "请求超时"),
    CONFLICT(409, "资源冲突，如重复提交"),
    UNSUPPORTED_MEDIA_TYPE(415, "不支持的媒体类型"),
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后再试"),

    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    NOT_IMPLEMENTED(501, "服务未实现"),
    BAD_GATEWAY(502, "网关错误"),
    SERVICE_UNAVAILABLE(503, "服务暂时不可用"),
    GATEWAY_TIMEOUT(504, "网关超时"),
    // 自定义业务逻辑状态码    
    SERVICE_LOGIC_ERROR(600, "网关超时"),
    // 自定义业务逻辑状态码 6xx
    SERVICE_LOGIC_EXCEPTION(601, "业务逻辑异常"),
    PRODUCT_SN_ALREADY_EXIST(602, "商品编号已存在");

    private final int code;
    private final String message;

    HttpCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
