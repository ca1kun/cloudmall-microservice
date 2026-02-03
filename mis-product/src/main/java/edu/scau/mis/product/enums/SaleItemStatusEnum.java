package edu.scau.mis.product.enums;

public enum SaleItemStatusEnum {
    RESERVED("reserved", "已预订"),
    PAID("paid", "已支付"),
    DELIVERED("delivered", "已发货"),
    RECEIPTED("receipted", "已收货"),
    CANCELLED("cancelled", "已取消");

    private final String code;
    private final String info;

    SaleItemStatusEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 枚举对象
     */
    public static SaleItemStatusEnum getByCode(String code) {
        for (SaleItemStatusEnum e : SaleItemStatusEnum.values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 根据编码获取描述信息
     *
     * @param code 编码
     * @return 描述信息
     */
    public static String getInfoByCode(String code) {
        SaleItemStatusEnum e = getByCode(code);
        return e == null ? "" : e.getInfo();
    }
}
