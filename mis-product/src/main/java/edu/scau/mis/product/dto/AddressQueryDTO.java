package edu.scau.mis.product.dto;

import lombok.Data;

/**
 * 收货地址分页查询参数
 */
@Data
public class AddressQueryDTO {

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;

    /**
     * 收货人姓名，模糊查询
     */
    private String receiverName;

    /**
     * 收货人手机号，模糊查询
     */
    private String receiverPhone;
}
