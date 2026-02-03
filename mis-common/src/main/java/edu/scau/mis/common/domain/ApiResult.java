package edu.scau.mis.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // 忽略 null 字段
public class ApiResult<T> {
    private int code;
    private String message;
    private T data;

    public ApiResult() {}

    public ApiResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 静态工厂方法：成功响应
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(HttpCode.SUCCESS.getCode(), HttpCode.SUCCESS.getMessage(), data);
    }

    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(HttpCode.SUCCESS.getCode(), message, data);
    }

    // 静态工厂方法：创建成功（201）
    public static <T> ApiResult<T> created(T data) {
        return new ApiResult<>(HttpCode.CREATED.getCode(), HttpCode.CREATED.getMessage(), data);
    }

    // 静态工厂方法：无内容（204）
    public static <T> ApiResult<T> noContent() {
        return new ApiResult<>(HttpCode.NO_CONTENT.getCode(), HttpCode.NO_CONTENT.getMessage(), null);
    }

    // 静态工厂方法：失败响应（通用）

    public static <T> ApiResult<T> error(String message) {
        return new ApiResult<>(HttpCode.INTERNAL_SERVER_ERROR.getCode(), message, null);
    }
    public static <T> ApiResult<T> error(int code, String message) {
        return new ApiResult<>(code, message, null);
    }

    public static <T> ApiResult<T> error(HttpCode resultCode) {
        return new ApiResult<>(resultCode.getCode(), resultCode.getMessage(), null);
    }


    // 业务失败
    public static <T> ApiResult<T> fail(String message) {
        return new ApiResult<>(HttpCode.SERVICE_LOGIC_ERROR.getCode(), message, null);
    }
    public static <T> ApiResult<T> fail(int code, String message) {
        return new ApiResult<>(code, message, null);
    }

    public static <T> ApiResult<T> fail(HttpCode resultCode) {
        return new ApiResult<>(resultCode.getCode(), resultCode.getMessage(), null);
    }
}
