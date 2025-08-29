package com.parses.server.impl;

import com.parses.dao.mapper.CapitalPricingMapper;
import com.parses.dao.model.CapitalPricingEntity;
import com.parses.server.CapitalPricingServer;
import com.parses.server.bean.CapitalPricingBean;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CapitalPricingServerImpl implements CapitalPricingServer {
    @Resource
    private MapperFacade mapperFacade;
    @Autowired
    private CapitalPricingMapper capitalPricingMapper;
    @Transactional
    @Override
    public int batchInsertCapitalPricing(List<CapitalPricingBean> list) {
        List<CapitalPricingEntity> capitalPricingEntities = mapperFacade.mapAsList(list, CapitalPricingEntity.class);
        return capitalPricingMapper.batchInsert(capitalPricingEntities);
    }
}
