package com.parses.server.impl;

import com.parses.dao.mapper.CapitalMapper;
import com.parses.dao.mapper.TemplateMapper;
import com.parses.dao.model.CapitalEntity;
import com.parses.dao.model.TemplateEntity;
import com.parses.dao.model.csc.MerchantMemberEntity;
import com.parses.server.CapitalServer;
import com.parses.server.constant.TemplateNo;
import com.parses.server.csc.CscMerchantMemberServer;
import com.parses.server.bean.CapitalBean;
import com.parses.server.mapping.MultipleCompensatoryMapping;
import com.parses.server.util.UUIDUtils;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@Service
public class CapitalServerImpl implements CapitalServer {
    @Resource
    MapperFacade mapperFacade;
    @Autowired
    private CapitalMapper capitalMapper;
    @Autowired
    private CscMerchantMemberServer cscMerchantMemberServer;
    @Autowired
    private TemplateMapper templateMapper;
    @Transactional
    @Override
    public int insertCapital(CapitalBean capitalBean) {
        CapitalEntity capitalEntity = this.mapperFacade.map(capitalBean, CapitalEntity.class);
        return capitalMapper.insertCapital(capitalEntity);
    }

    @Override
    public CapitalBean createCapital(String capitalCode) {
        if (StringUtils.isEmpty(capitalCode)){
            throw new RuntimeException("Element CapitalCode is null");
        }
        CapitalBean capitalBean = new CapitalBean();
        capitalBean.setCapitalCode(capitalCode);
        MerchantMemberEntity merchantMember = cscMerchantMemberServer.selectByMerchantCodeAndMerchantType(capitalCode, MultipleCompensatoryMapping.PRIORITY_CAPITAL.getMerchantType());
        capitalBean.setCapitalNo(merchantMember.getMerchantNo());
        capitalBean.setCapitalMemberNo(merchantMember.getMemberNo());
        capitalBean.setCapitalName(merchantMember.getMemberName());
        capitalBean.setCurrentMirrorNo(UUIDUtils.genSimpleUUID());
        TemplateEntity templateEntity = templateMapper.selectBySourceTypeAndTemplateNo(TemplateNo.CAPITAL.getSourceType(), TemplateNo.CAPITAL.getTemplateNo());
        capitalBean.setTemplateNo(TemplateNo.CAPITAL.getTemplateNo());
        capitalBean.setTemplateMirrorNo(templateEntity.getCurrentMirrorNo());
        capitalBean.setCapitalStatus(1);
        capitalBean.setOperaterName("PEC");
        return capitalBean;
    }
}
