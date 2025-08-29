package com.parses.server.bean;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CapitalPricingBean {
    private String capitalNo;

    private String capitalCode;

    private String pricingNo;

    private String currentMirrorNo;

    private Integer matchType;

    private String matchTargetNo;

    private String repaymentFormulaNo;

    private String repaymentFormulaName;

    private BigDecimal yearRate;

    private String clearingFormulaNo;

    private String clearingFormulaName;

    private String compensatoryCapitalCode;

    private String compensatoryCapitalName;

    private String compensatoryMemberNo;

    private String compensatoryExpandInfo;
}
