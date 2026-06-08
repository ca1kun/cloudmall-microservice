package edu.scau.mis.order.dto;

import lombok.Data;

@Data
public class ReturnAuditDTO {
    private Boolean approved;
    private String remark;
}
