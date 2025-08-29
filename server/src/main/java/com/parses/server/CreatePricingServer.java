package com.parses.server;


import com.parses.server.bean.CapitalBean;
import com.parses.server.bean.ElementDataBean;
import com.parses.server.bean.PricingFeeBean;
import com.parses.server.bean.ProductPricingBean;

import java.util.List;

public interface CreatePricingServer {
    void createPricingProcess(CapitalBean capitalBean, List<ElementDataBean> capitalElementList,
                              List<ProductPricingBean> productPricingBeans, List<PricingFeeBean> pricingFeeBeans);
}
