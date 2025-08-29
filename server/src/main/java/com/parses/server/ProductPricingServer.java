package com.parses.server;

import com.parses.server.bean.CapitalBean;
import com.parses.server.bean.ProductPricingBean;

import java.util.List;

public interface ProductPricingServer {
    ProductPricingBean selectByPricingNo(String pricingNo);

    int batchInsertProductPricing(List<ProductPricingBean> list);

    void addProductPricingInfo(List<ProductPricingBean> list, CapitalBean capitalBean);
}
