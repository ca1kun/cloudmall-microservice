package edu.scau.mis.common.vo;

import java.io.Serializable;
import java.math.BigDecimal;


public class PaymentResultVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应付找零 (Change Due)
     */
    private BigDecimal changeDue;

    /**
     * 无参构造函数。
     * 很多框架（如Jackson进行JSON反序列化时）需要一个公共的无参构造函数。
     */
    public PaymentResultVo() {
    }

    /**
     * 方便创建对象的构造函数。
     * 在Service层计算出找零后，可以直接 new PaymentResultVo(change) 来创建实例。
     *
     * @param changeDue 计算出的找零金额
     */
    public PaymentResultVo(BigDecimal changeDue) {
        this.changeDue = changeDue;
    }

    // --- 标准的 Getter 和 Setter ---

    public BigDecimal getChangeDue() {
        return changeDue;
    }



    public void setChangeDue(BigDecimal changeDue) {
        this.changeDue = changeDue;
    }
}