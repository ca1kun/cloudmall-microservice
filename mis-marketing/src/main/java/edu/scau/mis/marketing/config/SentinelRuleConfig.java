package edu.scau.mis.marketing.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class SentinelRuleConfig {

    @PostConstruct
    public void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();

        FlowRule seckillRule = new FlowRule();

        // 这个名字要和 @SentinelResource(value = "coupon-seckill") 一致
        seckillRule.setResource("coupon-seckill");

        // 按 QPS 限流
        seckillRule.setGrade(RuleConstant.FLOW_GRADE_QPS);

        // 先设置 1500，后面根据压测结果调
        seckillRule.setCount(1500);

        // 直接拒绝
        seckillRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);

        rules.add(seckillRule);

        FlowRuleManager.loadRules(rules);
    }
}