package com.parses.server.constant;

import lombok.Getter;

@Getter
public enum MilliAndFourPercent {
    /**
     * 千一
     */
    MILLI("milliPreSettleRate"),
    /**
     * 百四
     */
    FOUR_PERCENT("fourPercentPreSettleRate"),
    ;

    private final String feeParam;

    MilliAndFourPercent(String feeParam) {
        this.feeParam = feeParam;
    }
}
