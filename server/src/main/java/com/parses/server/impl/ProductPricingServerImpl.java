package com.parses.server.impl;

import com.parses.dao.mapper.CapitalTemplateMapper;
import com.parses.dao.mapper.ProductMapper;
import com.parses.dao.mapper.ProductPricingMapper;
import com.parses.dao.model.CapitalTemplateEntity;
import com.parses.dao.model.ProductEntity;
import com.parses.dao.model.ProductPricingEntity;
import com.parses.server.ProductPricingServer;
import com.parses.server.bean.CapitalBean;
import com.parses.server.bean.ProductPricingBean;
import com.parses.server.util.UUIDUtils;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class ProductPricingServerImpl implements ProductPricingServer {
    @Autowired
    private ProductPricingMapper productPricingMapper;
    @Autowired
    private MapperFacade mapperFacade;
    @Autowired
    private CapitalTemplateMapper capitalTemplateMapper;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public ProductPricingBean selectByPricingNo(String pricingNo) {
        ProductPricingEntity productPricingEntity = productPricingMapper.selectByPricingNo(pricingNo);
        return mapperFacade.map(productPricingEntity, ProductPricingBean.class);
    }

    @Transactional
    @Override
    public int batchInsertProductPricing(List<ProductPricingBean> list) {
        List<ProductPricingEntity> productPricingEntities = this.mapperFacade.mapAsList(list, ProductPricingEntity.class);
        return productPricingMapper.batchInsert(productPricingEntities);
    }

    @Override
    public void addProductPricingInfo(List<ProductPricingBean> list, CapitalBean capitalBean) {
        List<CapitalTemplateEntity> capitalTemplates = capitalTemplateMapper.selectByCapitalCode(capitalBean.getCapitalCode());
        if (CollectionUtils.isEmpty(capitalTemplates)) {
            throw new RuntimeException("未找到 CapitalTemplate");
        }
        for (ProductPricingBean productPricingBean : list) {
            productPricingBean.setPricingNo(UUIDUtils.genSimpleUUID());
            productPricingBean.setCurrentMirrorNo(UUIDUtils.genSimpleUUID());
            ProductEntity productEntity = productMapper.selectByProductCode(productPricingBean.getProductCode() == null ? "5551" : productPricingBean.getProductCode());
            productPricingBean.setProductNo(productEntity.getProductNo());
            productPricingBean.setProductCode(productEntity.getProductCode());
            productPricingBean.setChannelType(productEntity.getProductChannelCode());
            productPricingBean.setRepaymentWay("1");
            productPricingBean.setCapitalCode(capitalBean.getCapitalCode());
            for (CapitalTemplateEntity capitalTemplate : capitalTemplates) {
                if (capitalTemplate.getMatchTargetRankLevel().contains(productPricingBean.getRankLevel())) {
                    productPricingBean.setRepaymentFormulaNo(capitalTemplate.getRepaymentFormulaNo());
                    productPricingBean.setRepaymentFormulaName(capitalTemplate.getRepaymentFormulaName());
                }
            }
        }
    }
}
