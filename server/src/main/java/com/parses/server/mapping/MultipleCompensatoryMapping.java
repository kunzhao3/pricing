package com.parses.server.mapping;

import lombok.Getter;

@Getter
public enum MultipleCompensatoryMapping {
    PRIORITY_CAPITAL("default","2",""),
    PRIORITY_ZERO("G0009","4","0"),
    PRIORITY_ONE("G0012","7","1"),
    ;
    private final String capitalCode;
    private final String merchantType;
    private final String order;

    MultipleCompensatoryMapping(String capitalCode, String merchantType, String order) {
        this.capitalCode = capitalCode;
        this.merchantType = merchantType;
        this.order = order;
    }

    public static MultipleCompensatoryMapping getPriority(String capitalCode) {
        for (MultipleCompensatoryMapping bankCardPriority : MultipleCompensatoryMapping.values()) {
            if (bankCardPriority.getCapitalCode().equals(capitalCode)) {
                return bankCardPriority;
            }
        }
        return null;
    }
}
