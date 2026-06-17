package edu.scau.mis.pay.controller;

import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.domain.OmsOrder;
import edu.scau.mis.common.mapper.OmsOrderMapper;
import edu.scau.mis.pay.service.PayService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private PayService payService;

    @Autowired
    private OmsOrderMapper orderMapper;

    @Value("${mall.frontend-base-url}")
    private String frontendBaseUrl;

    @GetMapping("/alipay")
    public ApiResult<String> alipay(@RequestParam Long orderId) {
        String formHtml = payService.pay(orderId);
        return ApiResult.success(formHtml);
    }

    @GetMapping("/success")
    public void alipaySuccess(@RequestParam("out_trade_no") String orderSn,
                              @RequestParam(value = "trade_no", required = false) String tradeNo,
                              HttpServletResponse response) throws IOException {
        payService.syncAlipayReturn(orderSn, tradeNo);
        response.sendRedirect(buildMallSuccessUrl(orderSn, tradeNo));
    }

    @GetMapping("/{id}")
    public ApiResult<OmsOrder> getOrderById(@PathVariable("id") Long id) {
        OmsOrder order = orderMapper.selectById(id);
        return ApiResult.success(order);
    }

    private String buildMallSuccessUrl(String orderSn, String tradeNo) {
        String baseUrl = frontendBaseUrl;
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        StringBuilder url = new StringBuilder(baseUrl + "/mall/pay/success?sync=1");
        url.append("&out_trade_no=").append(encode(orderSn));

        if (tradeNo != null && !tradeNo.trim().isEmpty()) {
            url.append("&trade_no=").append(encode(tradeNo));
        }

        return url.toString();
    }

    private String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
}