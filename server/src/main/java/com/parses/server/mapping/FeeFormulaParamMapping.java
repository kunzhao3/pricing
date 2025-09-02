package com.parses.server.mapping;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

import static com.parses.server.mapping.FormulaParamMapping.*;

/**
 * 费项公式参数映射
 * 有新费项需要添加
 */
@Getter
public enum FeeFormulaParamMapping {
    /**
     * 资方利率
     */
    YEAR_RATE("", "", Arrays.asList(yearRate, yearDays, totalStage, preSettleDayRate, repaymentWay, settlementRate, floatingRate)),

    /**
     * 担保费
     */
    GUARANTEE_FEE("担保费", "GUARANTEE_FEE", Arrays.asList(baseRate, yearDays, settlementRate, calculationMode)),

    /**
     * 固收担保费1
     */
    GUARANTEE_FEE_ONE("固收担保费1", "GUARANTEE_FEE_ONE", Arrays.asList(baseRate, yearDays, calculationMode)),

    /**
     * 固收担保费2
     */
    GUARANTEE_FEE_TWO("固收担保费2", "GUARANTEE_FEE_TWO", Arrays.asList(baseRate, yearDays, calculationMode)),

    /**
     * 服务费
     */
    SERVICE_FEE("服务费", "SERVICE_FEE", Arrays.asList(complexMonthRate, preSettleSwitchRate, preSettleComplexDayRate, quickenDayRate, capitalAndUndertakeCapitalGuaranteeYearIRR, yearDays)),

    /**
     * 服务费1
     */
    SERVICE_FEE_ONE("服务费1", "SERVICE_FEE_ONE", Arrays.asList(capitalAndUndertakeCapitalGuaranteeYearIRR, yearDays)),

    /**
     * 服务费2
     */
    SERVICE_FEE_TWO("服务费2", "SERVICE_FEE_TWO", Arrays.asList(complexMonthRate, preSettleSwitchRate, preSettleComplexDayRate, quickenDayRate)),

    /**
     * 罚息
     */
    PENALTY_INTEREST("罚息", "PENALTY_INTEREST", Arrays.asList(baseRate, graceDays, chargeMode, baseAmountType, includePayDate)),

    /**
     * 逾期违约金
     */
    OVERDUE_LIQUIDATED_DAMAGES("逾期违约金", "OVERDUE_LIQUIDATED_DAMAGES", Arrays.asList(baseRate, graceDays, chargeMode, baseAmountType, includePayDate, quickenRecalculate)),

    /**
     * 逾期违约金1
     */
    OVERDUE_LIQUIDATED_DAMAGES_ONE("逾期违约金1", "OVERDUE_LIQUIDATED_DAMAGES_ONE", Arrays.asList(baseRate, graceDays, chargeMode, baseAmountType, includePayDate, quickenRecalculate)),

    /**
     * 逾期违约金2
     */
    OVERDUE_LIQUIDATED_DAMAGES_TWO("逾期违约金2", "OVERDUE_LIQUIDATED_DAMAGES_TWO", Arrays.asList(baseRate, graceDays, chargeMode, baseAmountType, includePayDate, quickenRecalculate)),

    /**
     * 逾期违约金3
     */
    OVERDUE_LIQUIDATED_DAMAGES_THREE("逾期违约金3", "OVERDUE_LIQUIDATED_DAMAGES_THREE", Arrays.asList(baseRate, graceDays, chargeMode, baseAmountType, includePayDate, quickenRecalculate)),

    /**
     * 逾期担保费
     */
    OVERDUE_GUARANTEE_FEE("逾期担保费", "OVERDUE_GUARANTEE_FEE", Arrays.asList(baseRate, graceDays, yearDays, includeGraceDays, calculationMode)),
    /**
     * 逾期担保费1
     */
    OVERDUE_GUARANTEE_FEE_ONE("逾期担保费1", "OVERDUE_GUARANTEE_FEE_ONE", Arrays.asList(baseRate, graceDays, yearDays)),

    /**
     * 逾期担保费2
     */
    OVERDUE_GUARANTEE_FEE_TWO("逾期担保费2", "OVERDUE_GUARANTEE_FEE_TWO", Arrays.asList(baseRate, graceDays, yearDays)),

    /**
     * 资产管理费
     */
    ASSET_MANAGE_FEE("资产管理费", "ASSET_MANAGE_FEE", Arrays.asList(baseRate, yearDays, complexMonthRate, preSettleSwitchRate, preSettleComplexDayRate)),

    /**
     * 资产管理1
     */
    ASSET_MANAGE_FEE_ONE("资产管理1", "ASSET_MANAGE_FEE_ONE", Arrays.asList(baseRate, yearDays)),

    /**
     * 资产管理2
     */
    ASSET_MANAGE_FEE_TWO("资产管理2", "ASSET_MANAGE_FEE_TWO", Arrays.asList(complexMonthRate, preSettleSwitchRate, preSettleComplexDayRate, quickenDayRate)),

    /**
     * 服务费结算
     */
    SERVICE_FEE_SETTLEMENT("服务费结算", "SERVICE_FEE_SETTLEMENT", Arrays.asList(baseRate, yearDays, fixedRate)),
    ;
    private final String feeName;
    private final String feeCode;
    private final List<FormulaParamMapping> params;

    FeeFormulaParamMapping(String feeName, String feeCode, List<FormulaParamMapping> params) {
        this.feeName = feeName;
        this.feeCode = feeCode;
        this.params = params;
    }


    public static FeeFormulaParamMapping getFeeParamsMappingByFeeCode(String feeCode) {
        for (FeeFormulaParamMapping feeFormulaParamMapping : values()) {
            if (feeFormulaParamMapping.feeCode.equals(feeCode)) {
                return feeFormulaParamMapping;
            }
        }
        return null;
    }

    public static FeeFormulaParamMapping getFeeParamsMappingByFeeName(String feeName) {
        for (FeeFormulaParamMapping feeFormulaParamMapping : values()) {
            if (feeFormulaParamMapping.feeName.equals(feeName)) {
                return feeFormulaParamMapping;
            }
        }
        return null;
    }
}
