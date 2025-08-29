package com.parses.server.csc.dto;

import lombok.Data;

@Data
public class QueryMerchantMemberDto {
    private Integer merchantType;
    private String merchantNo;
    private Integer memberType;
    private String memberNo;
    private String memberName;
    private String allowPreSettle;
    private String memberFullName;
}
