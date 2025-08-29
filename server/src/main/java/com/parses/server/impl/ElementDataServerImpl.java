package com.parses.server.impl;

import com.parses.dao.mapper.ElementDataMapper;
import com.parses.dao.model.ElementDataEntity;
import com.parses.server.ElementDataServer;
import com.parses.server.bean.CapitalBean;
import com.parses.server.bean.ElementDataBean;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Service
public class ElementDataServerImpl  implements ElementDataServer {
    @Resource
    MapperFacade mapperFacade;
    @Autowired
    private ElementDataMapper elementDataMapper;
    @Transactional
    @Override
    public int batchInsertCapitalElement(List<ElementDataBean> list) {
        List<ElementDataEntity> elementDataEntities = mapperFacade.mapAsList(list, ElementDataEntity.class);
        return elementDataMapper.batchInsert(elementDataEntities);
    }

    @Override
    public void addCapitalInfo(List<ElementDataBean> list, CapitalBean capitalBean) {
        for (ElementDataBean elementDataBean : list) {
            elementDataBean.setDataType(2);
            elementDataBean.setDataNo(capitalBean.getCapitalNo());
            elementDataBean.setDataMirrorNo(capitalBean.getCurrentMirrorNo());
        }
    }
}
