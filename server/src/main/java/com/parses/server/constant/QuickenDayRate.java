package com.parses.server.constant;

import lombok.Getter;

/**
 * 加速到期时:费项计算日利率
 */
@Getter
public enum QuickenDayRate {
    QUICKEN_DAY_RATE_A("A","0.00065"),
    QUICKEN_DAY_RATE_B("B","0.00098"),
    QUICKEN_DAY_RATE_C("C","0.00098"),
    QUICKEN_DAY_RATE_D("D","0.00098"),
    ;
    private final String rankLevel;
    private final String rate;

    QuickenDayRate(String rankLevel, String rate){
        this.rankLevel = rankLevel;
        this.rate = rate;
    }

    public static QuickenDayRate getQuickenDayRateMapping(String rankLevel){
        for(QuickenDayRate mapping : QuickenDayRate.values()){
            if(mapping.rankLevel.equals(rankLevel)){
                return mapping;
            }
        }
        return null;
    }
}
