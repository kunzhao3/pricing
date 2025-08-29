package com.parses.server.csc.response;

import com.parses.server.BaseResponse;
import com.parses.server.csc.dto.QueryMerchantMemberDto;

public class QueryMerchantMemberResp  extends BaseResponse {
    private QueryMerchantMemberDto merchantMember;

    public QueryMerchantMemberResp() {
    }

    public QueryMerchantMemberDto getMerchantMember() {
        return this.merchantMember;
    }

    public void setMerchantMember(QueryMerchantMemberDto merchantMember) {
        this.merchantMember = merchantMember;
    }
}
