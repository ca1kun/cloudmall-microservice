package edu.scau.mis.product.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收货地址返回对象
 */
@Data
public class AddressVO {

    private Long id;

    private String receiverName;

    private String receiverPhone;

    private String province;

    private String city;

    private String district;

    private String detailAddress;

    private String zipCode;

    private Integer isDefault;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
