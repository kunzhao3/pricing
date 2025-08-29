package com.parses.server.csc.request;

import com.parses.server.BaseRequest;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QueryMerchantMemberReq extends BaseRequest {
    @NotNull(
            message = "QueryMerchantMemberReq.merchantType 不能为空!"
    )
    private Integer merchantType;
    private String merchantNo;
    private String merchantCode;

    public QueryMerchantMemberReq() {
    }
}
