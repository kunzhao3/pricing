package com.parses.server.constant;

import lombok.Getter;

@Getter
public enum TemplateCapitalType {
    CAPITAL("1"),
    COMPENSATORY("2"),
    ;
    private final String capitalType;
    TemplateCapitalType(String capitalType) {
        this.capitalType = capitalType;
    }
}
