package edu.scau.mis.product.dto;

import lombok.Data;

/**
 * 新增/修改收货地址参数
 */
@Data
public class AddressSaveDTO {

    /**
     * 地址ID，新增时无需传入，修改时必传
     */
    private Long id;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人手机号
     */
    private String receiverPhone;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 邮政编码
     */
    private String zipCode;

    /**
     * 是否默认：0->否；1->是
     */
    private Integer isDefault = 0;
}
