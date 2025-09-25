package com.parses.server.mapping;

import lombok.Getter;

/**
 * 公式参数映射
 * 费项有新参数需添加
 */
@Getter
public enum FormulaParamMapping {
    totalStage("总期数", "totalStage", "","期次"),
    yearRate("年利率", "yearRate", "","资方年利率"),
    preSettleDayRate("提前结清日利率", "preSettleDayRate", "","资方提前结清日利率"),
    baseRate("费用利率", "baseRate", "0.00","费用利率"),
    yearDays("年天数", "yearDays", "365","计算费用的年天数"),
    graceDays("宽限期天数", "graceDays", "0","费用的宽限期天数"),
    chargeMode("收取方式", "chargeMode", "1","1:按比例,2:按固定金额"),
    baseAmountType("计算方式", "baseAmountType", "2","1:全部本金,2:剩余本金,3:全部本息,4:剩余本息,5:期初剩余本金,6:期末剩余本金"),
    includePayDate("是否包含还款日当天", "includePayDate", "0","0:不包含,1:包含"),
    includeGraceDays("逾期天数是否包含宽限期天数", "includeGraceDays", "false","false:不包含,true:包含"),
    complexMonthRate("月综合利率", "complexMonthRate", "","月综合利率"),
    preSettleComplexDayRate("提前结清综合利率千一", "preSettleComplexDayRate", "0.001","提前结清综合利率千一"),
    preSettleSwitchRate("提前结清综合利率百四", "preSettleSwitchRate", "0.04","提前结清综合利率百四"),
    capitalAndUndertakeCapitalGuaranteeYearIRR("资方和对资担保公司IRR", "capitalAndUndertakeCapitalGuaranteeYearIRR", "0.00","资方和对资担保公司IRR"),
    settlementRate("结算利率", "settlementRate", "","资方或担保方结算利率"),
    floatingRate("浮动利率", "floatingRate", "","资方浮动利率"),
    fixedRate("固定利率", "fixedRate", "","服务费结算固定利率"),
    repaymentWay("加速到期时:利息计算方式", "repaymentWay", "2","1:等本等息,2:等额本息;只有信托(中融,华润)等本等息"),
    quickenDayRate("加速到期时:费项计算日利率", "quickenDayRate", "","A等级-0.065%,BCD等级-0.098%"),
    quickenRecalculate("加速到期后:逾期费项重算方式", "quickenRecalculate", "1","1:加速到期后按日重新计算"),
    calculationMode("回购时:重算方式", "calculationMode", "","BY_DAY_ENTIRELY:合并后未还金额*日费率*实际占用天数,REPURCHASE_STAGE_ONLY:只包含回购期次费用,OVER_MUST_PAY_DATE_STAGE:已出账单日期次费用"),
    ;

    private final String paramName;
    private final String paramCode;
    private final String defaultValue;
    private final String description;

    FormulaParamMapping(String paramName, String paramCode, String defaultValue,String description) {
        this.paramName = paramName;
        this.paramCode = paramCode;
        this.defaultValue = defaultValue;
        this.description = description;
    }

    public static FormulaParamMapping getParamMappingByParamName(String paramName) {
        for (FormulaParamMapping paramMapping : FormulaParamMapping.values()) {
            if (paramMapping.paramName.equals(paramName)) {
                return paramMapping;
            }
        }
        return null;
    }
}
