package edu.scau.mis.order.vo;

import lombok.Data;
import java.util.List;

@Data
public class OrderDetailResponseVO {
    private OrderDetailVO order;
    private List<OrderItemVO> items;
}
