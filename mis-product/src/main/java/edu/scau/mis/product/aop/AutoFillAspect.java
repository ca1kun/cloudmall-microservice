package edu.scau.mis.product.aop;

import edu.scau.mis.common.annotation.AutoFill; // 确保导入了注解
import edu.scau.mis.common.utils.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature; // 导入 MethodSignature
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method; // 导入 Method
import java.util.Collection;
import java.util.Date;

@Aspect
@Component
public class AutoFillAspect {

    private static final Logger log = LoggerFactory.getLogger(AutoFillAspect.class);

    @Pointcut("@annotation(edu.scau.mis.common.annotation.AutoFill)")
    public void autoFillPointcut() {}

    @Before("autoFillPointcut()")
    public void autoFill(JoinPoint joinPoint) {
        log.debug("AutoFill Aspect triggered for method: {}", joinPoint.getSignature().getName());

        try {
            // 1. 【关键修改】获取方法签名和注解实例
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            AutoFill autoFillAnnotation = method.getAnnotation(AutoFill.class);

            // 2. 【关键修改】从注解中直接获取操作类型
            boolean isInsert = autoFillAnnotation.isInsert();

            // 3. 获取方法参数
            Object[] args = joinPoint.getArgs();
            if (args == null || args.length == 0) {
                return;
            }

            // 4. 调用填充逻辑
            fillFields(args, isInsert);

        } catch (Exception e) {
            log.error("Error during auto-fill aspect execution", e);
        }
    }

    private void fillFields(Object[] args, boolean isInsert) {
        String username = getUsernameOrDefault();
        Date now = new Date();

        for (Object arg : args) {
            if (arg == null) continue;

            if (arg instanceof Collection<?> collection) {
                for (Object item : collection) {
                    doFill(item, isInsert, username, now);
                }
            } else {
                doFill(arg, isInsert, username, now);
            }
        }
    }

    private void doFill(Object object, boolean isInsert, String username, Date now) {
        if (object == null || object.getClass().isPrimitive() || object.getClass().getName().startsWith("java.")) {
            return;
        }

        try {
            // 填充更新字段 (所有操作都需要)
            setFieldIfNull(object, "updateTime", now);
            setFieldIfNull(object, "updateBy", username);

            if (isInsert) {
                // 填充创建字段 (仅插入操作)
                setFieldIfNull(object, "createTime", now);
                setFieldIfNull(object, "createBy", username);
                // 【关键修复】填充 delFlag 字段
                setFieldIfNull(object, "delFlag", "0"); // 假设 '0' 代表未删除
            }
        } catch (Exception e) {
            log.warn("AutoFill failed for class {}: {}", object.getClass().getName(), e.getMessage());
        }
    }

    // setFieldIfNull, getField, getUsernameOrDefault 等辅助方法保持不变...
    // ... (为简洁省略，请保留你原有的这些方法)
    /**
     * 仅当字段为 null 时才设置值
     */
    private void setFieldIfNull(Object target, String fieldName, Object value) {
        try {
            Field field = getField(target.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                Object currentVal = field.get(target);
                if (currentVal == null) {
                    field.set(target, value);
                }
            }
        } catch (Exception e) {
            // 忽略
        }
    }

    /**
     * 递归查找字段（支持父类字段）
     */
    private Field getField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    /**
     * 获取用户名辅助方法
     */
    private String getUsernameOrDefault() {
        String username = SecurityUtils.getUsername();
        return (username != null) ? username : "admin"; // 默认值
    }
}
