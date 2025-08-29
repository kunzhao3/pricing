package com.parses.server.mapping;

import lombok.Getter;

/**
 * 资方提前结清综合利率千一
 * 资方不是0.001就添加
 */
@Getter
public enum PreSettleComplexDayRateMapping {
    C5014("C5014","0.00065"),
    C5027("C5027","0.00065"),
    C5030("C5030","0.00065"),
    C5036("C5036","0.00065"),
    ;
    private final String capitalCode;
    private final String rate;

    PreSettleComplexDayRateMapping(String capitalCode, String rate) {
        this.capitalCode = capitalCode;
        this.rate = rate;
    }
    public static PreSettleComplexDayRateMapping getRateMapping(String capitalCode) {
        for (PreSettleComplexDayRateMapping value : PreSettleComplexDayRateMapping.values()) {
            if (value.capitalCode.equals(capitalCode)) {
                return value;
            }
        }
        return null;
    }
}
