package com.parses.server;

import com.parses.server.bean.CapitalPricingBean;

import java.util.List;

public interface CapitalPricingServer {
    int batchInsertCapitalPricing(List<CapitalPricingBean> list);
}
