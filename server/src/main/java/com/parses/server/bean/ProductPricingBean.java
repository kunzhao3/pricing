package com.parses.server.bean;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class ProductPricingBean {
    /**
     * 资方编码  - 必填
     */
    private String capitalCode;
    /**
     * 产品编号  - 必填
     */
    private String productNo;
    /**
     * 产品代码  - 必填
     */
    private String productCode;
    /**
     * 产品定价编号  - 必填
     */
    private String pricingNo;
    /**
     * 当前镜像编号 -必填
     */
    private String currentMirrorNo;
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
     * 年利率
     */
    private BigDecimal yearRate;
    /**
     * 展示月利率
     */
    private BigDecimal aprMonthRate;
    /**
     * 还款方式 - 必填
     */
    private String repaymentWay;
    /**
     * 债务债权计算公式
     */
    private String repaymentFormulaNo;
    /**
     * 债务债权计算公式名称
     */
    private String repaymentFormulaName;
    /**
     * 渠道  - 必填
     */
    private String channelType;
}
