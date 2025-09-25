package com.parses.util.bean;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExlFeeParamBean {
    /**
     * 总期数 - 必填
     */
    private Integer totalStage;
    /**
     * 风险等级 A档 B档 C档 D档  - 必填
     */
    private String rankLevel;
    /**
     * 用户标签  - 必填
     */
    private String consumerLabel;
    /**
     * 费用名称  - 必填
     */
    private String feeName;
    /**
     * 费用代码  - 必填
     */
    private String feeCode;
    /**
     * 年利率 - 必填
     */
    private BigDecimal yearRate;
    /**
     * 提前结清日利率
     */
    private BigDecimal preSettleDayRate;
    /**
     * 费用利率  - 必填
     */
    private BigDecimal baseRate;
    /**
     * 年天数
     */
    private Integer yearDays;
    /**
     * 月综合利率
     */
    private BigDecimal complexMonthRate;
    /**
     * 资方和对资担保公司IRR
     */
    private BigDecimal capitalAndUndertakeCapitalGuaranteeYearIRR;
    /**
     * 结算利率
     */
    private BigDecimal settlementRate;
    /**
     * 浮动利率
     */
    private BigDecimal floatingRate;
}
