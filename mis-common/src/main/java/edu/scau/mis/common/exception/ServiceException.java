package edu.scau.mis.common.exception;

import edu.scau.mis.common.domain.HttpCode;
import lombok.Getter;

/**
 * 业务异常类 (Service Exception)
 * 用于表示在业务逻辑处理过程中发生的、可预期的、非系统性的错误。
 */
@Getter
public class ServiceException extends RuntimeException {
    private final int code;

    public ServiceException(String message) {
        super(message); // ✅ 必须加这个！
        this.code = 500;
    }

    public ServiceException(int code, String message) {
        super(message); // ✅ 必须加这个！
        this.code = code;
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }

    public ServiceException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public ServiceException(HttpCode httpCode) {
        super(httpCode.getMessage());
        this.code = httpCode.getCode();
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}

