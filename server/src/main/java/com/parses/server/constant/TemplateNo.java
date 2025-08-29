package com.parses.server.constant;

import lombok.Getter;

@Getter
public enum TemplateNo {
    PRODUCT("1", "8a29677055a541419879d43648d10584"),
    CAPITAL("2", "9bad40d7ce1d41ff912be2d09830b96f"),
    GUARANTOR("4", "aabb7fccdfa545efab6c61bd351b09c9"),
    ;
    private final String sourceType;
    private final String templateNo;

    TemplateNo(String sourceType, String templateNo) {
        this.sourceType = sourceType;
        this.templateNo = templateNo;
    }

}
