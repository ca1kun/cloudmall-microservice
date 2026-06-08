package edu.scau.mis.order.enums;

import java.util.Arrays;

public enum OrderStatusEnum {
    PENDING_PAYMENT(0, "待支付"),
    PAID(1, "已支付"),
    SHIPPED(2, "已发货"),
    COMPLETED(3, "已完成"),
    CANCELED(4, "已取消"),
    REFUNDING(5, "退款中"),
    REFUNDED(6, "已退款"),
    RETURN_REJECTED(7, "退货被拒");

    private final int code;
    private final String text;

    OrderStatusEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() { return code; }
    public String getText() { return text; }

    public static OrderStatusEnum fromCode(Integer code) {
        if (code == null) return null;
        return Arrays.stream(values()).filter(item -> item.code == code).findFirst().orElse(null);
    }

    public static String nameOf(Integer code) {
        OrderStatusEnum status = fromCode(code);
        return status == null ? null : status.name();
    }

    public static Integer codeOf(String name) {
        if (name == null || name.trim().isEmpty()) return null;
        String normalized = name.trim().toUpperCase();
        return Arrays.stream(values())
                .filter(item -> item.name().equals(normalized))
                .map(OrderStatusEnum::getCode)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的订单状态: " + name));
    }
}
