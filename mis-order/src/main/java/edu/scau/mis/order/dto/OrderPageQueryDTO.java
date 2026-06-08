package edu.scau.mis.order.dto;

import lombok.Data;

@Data
public class OrderPageQueryDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String orderNo;
    private String status;
    private String startTime;
    private String endTime;
    private String buyerName;
    private String buyerPhone;
}
