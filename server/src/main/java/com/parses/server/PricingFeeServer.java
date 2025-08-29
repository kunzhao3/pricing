package com.parses.server;

import com.parses.server.bean.ElementDataBean;
import com.parses.server.bean.PricingFeeBean;
import com.parses.server.bean.ProductPricingBean;

import java.util.List;

public interface PricingFeeServer {
    PricingFeeBean selectByPricingNoAndFeeCode(String pricingNo, String feeCode);

    int batchInsertFee(List<PricingFeeBean> list);

    List<PricingFeeBean> addProductFeeInfo(List<ProductPricingBean> productPricingBeans, List<PricingFeeBean> list, List<ElementDataBean> capitalElementList);
}
