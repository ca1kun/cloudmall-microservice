package edu.scau.mis.common.exception;

import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.domain.HttpCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException; // 引入正确的 AccessDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 全局异常处理器
 * 统一处理应用中抛出的各种异常，并返回标准格式的API响应。
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 权限校验异常 (Spring Security)
     * 当用户尝试访问其没有权限的资源时触发。
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResult<String>> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.warn("权限不足: 请求地址 '{}', 错误信息: '{}'", requestURI, e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResult.fail(HttpCode.FORBIDDEN));
    }

    /**
     * 404 - 找不到处理器异常
     * 当请求的URL没有匹配到任何Controller方法时触发。
     * 注意：需要配置 spring.mvc.throw-exception-if-no-handler-found=true
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResult<String>> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.warn("资源不存在: 请求地址 '{}'", requestURI);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResult.fail(HttpCode.NOT_FOUND));
    }

    /**
     * 请求参数类型不匹配异常
     * 例如，期望一个数字，但传入了一个字符串。
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResult<String>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        // 构造更清晰的错误信息
        String message = String.format("参数类型不匹配: 参数 '%s' 的值 '%s' 无法转换为期望的类型 '%s'",
                e.getName(), e.getValue(), e.getRequiredType().getSimpleName());
        log.warn("请求参数不合法: 请求地址 '{}', {}", requestURI, message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResult.error(HttpCode.BAD_REQUEST.getCode(), message));
    }

    /**
     * 业务逻辑异常 (ServiceException)
     * 这是由业务代码主动抛出的、可预期的异常，例如“用户名已存在”、“密码错误”等。
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiResult<String>> handleServiceException(ServiceException e) {
        // 业务异常通常是可预期的，使用 warn 级别日志即可
        log.warn("业务异常: {}", e.getMessage());

        // 关键修复：
        // 假设 ServiceException 中的 code 是 int 类型，它不可能为 null。
        // 我们需要定义一个规则，比如如果 code 是默认值 0，我们就使用一个标准的业务错误码。
        int code = e.getCode();
        // 如果业务异常没有指定code，则使用一个通用的业务失败码，例如 50000
        int finalCode = code != 0 ? code : 50000;

        // 返回 HTTP 状态码 200 OK，但在响应体中指明业务处理失败
        return ResponseEntity.ok(ApiResult.error(finalCode, e.getMessage()));
    }

    /**
     * 通用异常处理器 (最终防线)
     * 捕获所有未被其他处理器捕获的异常。
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<String>> handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        // 这是真正的系统内部错误，使用 error 级别日志，并打印完整堆栈
        log.error("系统内部异常: 请求地址 '{}'", requestURI, e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResult.error(HttpCode.INTERNAL_SERVER_ERROR));
    }

}
