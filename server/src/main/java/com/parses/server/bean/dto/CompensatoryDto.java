package com.parses.server.bean.dto;

import lombok.Data;

@Data
public class CompensatoryDto {
    private String compensatoryMemberNo;
    private String compensatoryCapitalCode;
    private String compensatoryCapitalName;
    private int defaultAllocateBankCardPriority;
}
