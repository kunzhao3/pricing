package com.parses.server.csc;

import com.parses.dao.model.csc.MerchantMemberEntity;

public interface CscMerchantMemberServer {
    MerchantMemberEntity selectByMerchantCodeAndMerchantType(String merchantCode, String merchantType);
}
