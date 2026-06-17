package edu.scau.mis.marketing.MQ;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponSeckillMessage {

    private Long couponId;

    private Long userId;

    private String username;
}