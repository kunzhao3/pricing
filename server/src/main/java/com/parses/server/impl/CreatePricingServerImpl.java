package com.parses.server.impl;

import com.alibaba.fastjson.JSON;
import com.parses.dao.mapper.CapitalTemplateMapper;
import com.parses.dao.model.CapitalTemplateEntity;
import com.parses.dao.model.csc.MerchantMemberEntity;
import com.parses.server.*;
import com.parses.server.bean.*;
import com.parses.server.bean.dto.CompensatoryDto;
import com.parses.server.constant.TemplateCapitalType;
import com.parses.server.csc.CscMerchantMemberServer;
import com.parses.server.mapping.MultipleCompensatoryMapping;
import com.parses.server.util.UUIDUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class CreatePricingServerImpl implements CreatePricingServer {
    @Autowired
    private CapitalServer capitalServer;
    @Autowired
    private ElementDataServer elementDataServer;
    @Autowired
    private ProductPricingServer productPricingServer;
    @Autowired
    private PricingFeeServer pricingFeeServer;
    @Autowired
    private CscMerchantMemberServer cscMerchantMemberServer;
    @Autowired
    private CapitalTemplateMapper capitalTemplateMapper;
    @Autowired
    private CapitalPricingServer capitalPricingServer;

    @Transactional
    @Override
    public void createPricingProcess(CapitalBean capitalBean, List<ElementDataBean> capitalElementList,
                                     List<ProductPricingBean> productPricingBeans, List<PricingFeeBean> pricingFeeBeans) {
        capitalServer.insertCapital(capitalBean);
        elementDataServer.batchInsertCapitalElement(capitalElementList);
        productPricingServer.batchInsertProductPricing(productPricingBeans);
        pricingFeeServer.batchInsertFee(pricingFeeBeans);
        this.batchInsertCapitalPricingAndFee(productPricingBeans, capitalBean);
    }

    public void batchInsertCapitalPricingAndFee(List<ProductPricingBean> productPricingBeans, CapitalBean capitalBean) {
        List<CapitalTemplateEntity> capitalTemplates = capitalTemplateMapper.selectByCapitalCode(capitalBean.getCapitalCode());
        if (CollectionUtils.isEmpty(capitalTemplates)) {
            throw new RuntimeException("未找到 CapitalTemplate");
        }
        this.batchInsertPricingAndFee(capitalTemplates, productPricingBeans);
        Set<String> compensatoryCodes = new HashSet<>();
        for (CapitalTemplateEntity capitalTemplateEntity : capitalTemplates) {
            if (StringUtils.isEmpty(capitalTemplateEntity.getMatchTargetCode())) {
                continue;
            }
            for (String compensatoryCode : capitalTemplateEntity.getMatchTargetCode().split(",")) {
                boolean flag = compensatoryCodes.add(compensatoryCode);
                if (flag) {
                    List<CapitalTemplateEntity> compensatoryTemplates = capitalTemplateMapper.selectByCapitalCodeAndMatchTargetCode(compensatoryCode, capitalBean.getCapitalCode());
                    if (CollectionUtils.isEmpty(compensatoryTemplates)) {
                        throw new RuntimeException("未找到 CompensatoryTemplate");
                    }
                    this.batchInsertPricingAndFee(compensatoryTemplates, productPricingBeans);
                }
            }
        }
    }

    private void batchInsertPricingAndFee(List<CapitalTemplateEntity> capitalTemplates, List<ProductPricingBean> productPricingBeans) {
        Map<String, CompensatoryDto> compensatoryDtoMap = new HashMap<>();
        for (CapitalTemplateEntity capitalTemplateEntity : capitalTemplates) {
            if (StringUtils.isEmpty(capitalTemplateEntity.getMatchTargetCode())) {
                continue;
            }
            for (String matchTargetCode : capitalTemplateEntity.getMatchTargetCode().split(",")) {
                if (Objects.isNull(compensatoryDtoMap.get(matchTargetCode))
                        && TemplateCapitalType.CAPITAL.getCapitalType().equals(capitalTemplateEntity.getCapitalType())) {
                    MerchantMemberEntity merchantMember = cscMerchantMemberServer.selectByMerchantCodeAndMerchantType(matchTargetCode, MultipleCompensatoryMapping.getPriority(matchTargetCode).getMerchantType());
                    CompensatoryDto compensatoryDto = new CompensatoryDto();
                    compensatoryDto.setCompensatoryCapitalCode(matchTargetCode);
                    compensatoryDto.setCompensatoryCapitalName(merchantMember.getMemberName());
                    compensatoryDto.setCompensatoryMemberNo(merchantMember.getMemberNo());
                    compensatoryDto.setDefaultAllocateBankCardPriority(Integer.parseInt(MultipleCompensatoryMapping.getPriority(matchTargetCode).getOrder()));
                    compensatoryDtoMap.put(matchTargetCode, compensatoryDto);
                }
            }
        }

        List<CapitalPricingBean> capitalPricingBeans = new ArrayList<>();
        // 遍历产品定价，生成资方或代偿方定价
        for (ProductPricingBean productPricingBean : productPricingBeans) {
            for (CapitalTemplateEntity capitalTemplate : capitalTemplates) {
                if (capitalTemplate.getMatchTargetRankLevel().contains(productPricingBean.getRankLevel())) {
                    CapitalPricingBean capitalPricingBean = this.buildCapitalPricingBean(capitalTemplate, productPricingBean);
                    // 只有资方定价才需要生成代偿方
                    if (TemplateCapitalType.CAPITAL.getCapitalType().equals(capitalTemplate.getCapitalType())) {
                        if (StringUtils.isNotBlank(capitalTemplate.getMatchTargetCode())) {
                            String[] compensatoryCodes = capitalTemplate.getMatchTargetCode().split(",");
                            CompensatoryDto compensatoryDto = compensatoryDtoMap.get(compensatoryCodes[0]);
                            capitalPricingBean.setCompensatoryCapitalCode(compensatoryDto.getCompensatoryCapitalCode());
                            capitalPricingBean.setCompensatoryCapitalName(compensatoryDto.getCompensatoryCapitalName());
                            capitalPricingBean.setCompensatoryMemberNo(compensatoryDto.getCompensatoryMemberNo());
                            if (compensatoryCodes.length > 1) {
                                List<CompensatoryDto> compensatoryDtos = new ArrayList<>();
                                for (int i = 1; i < compensatoryCodes.length; i++) {
                                    compensatoryDtos.add(compensatoryDtoMap.get(compensatoryCodes[i]));
                                }
                                String compensatoryJson = JSON.toJSONString(compensatoryDtos);
                                capitalPricingBean.setCompensatoryExpandInfo(compensatoryJson);
                            }
                        }
                    }
                    capitalPricingBeans.add(capitalPricingBean);
                }
            }
        }
        capitalPricingServer.batchInsertCapitalPricing(capitalPricingBeans);
        List<PricingFeeBean> pricingFeeBeanList = new ArrayList<>();
        for (CapitalPricingBean capitalPricingBean : capitalPricingBeans) {
            pricingFeeBeanList.add(this.buildPricingFeeBean(capitalPricingBean, ""));
            ProductPricingBean productPricingBean = productPricingServer.selectByPricingNo(capitalPricingBean.getMatchTargetNo());
            for (CapitalTemplateEntity capitalTemplate : capitalTemplates) {
                if (capitalTemplate.getMatchTargetRankLevel().contains(productPricingBean.getRankLevel())) {
                    if (StringUtils.isNotBlank(capitalTemplate.getFeeCode())) {
                        String[] feeCodes = capitalTemplate.getFeeCode().split(",");
                        for (String feeCode : feeCodes) {
                            pricingFeeBeanList.add(this.buildPricingFeeBean(capitalPricingBean, feeCode));
                        }
                    }
                    break;
                }
            }
        }
        pricingFeeServer.batchInsertFee(pricingFeeBeanList);
    }

    private CapitalPricingBean buildCapitalPricingBean(CapitalTemplateEntity capitalTemplate, ProductPricingBean productPricingBean) {
        CapitalPricingBean capitalPricingBean = new CapitalPricingBean();
        String capitalType;
        if (TemplateCapitalType.CAPITAL.getCapitalType().equals(capitalTemplate.getCapitalType())) {
            capitalType = MultipleCompensatoryMapping.PRIORITY_CAPITAL.getMerchantType();
        } else {
            capitalType = Objects.requireNonNull(MultipleCompensatoryMapping.getPriority(capitalTemplate.getCapitalCode())).getMerchantType();
        }
        MerchantMemberEntity merchantMember = cscMerchantMemberServer.selectByMerchantCodeAndMerchantType(capitalTemplate.getCapitalCode(), capitalType);
        capitalPricingBean.setCapitalNo(merchantMember.getMerchantNo());
        capitalPricingBean.setCapitalCode(capitalTemplate.getCapitalCode());
        capitalPricingBean.setPricingNo(UUIDUtils.genSimpleUUID());
        capitalPricingBean.setCurrentMirrorNo(UUIDUtils.genSimpleUUID());
        capitalPricingBean.setMatchType(3);
        capitalPricingBean.setMatchTargetNo(productPricingBean.getPricingNo());
        capitalPricingBean.setYearRate(productPricingBean.getYearRate());
        capitalPricingBean.setRepaymentFormulaNo(capitalTemplate.getRepaymentFormulaNo());
        capitalPricingBean.setRepaymentFormulaName(capitalTemplate.getRepaymentFormulaName());
        capitalPricingBean.setClearingFormulaNo(capitalTemplate.getClearingFormulaNo());
        capitalPricingBean.setClearingFormulaName(capitalTemplate.getClearingFormulaName());
        capitalPricingBean.setCompensatoryCapitalCode("");
        capitalPricingBean.setCompensatoryCapitalName("");
        capitalPricingBean.setCompensatoryMemberNo("");
        return capitalPricingBean;
    }

    private PricingFeeBean buildPricingFeeBean(CapitalPricingBean capitalPricingBean, String feeCode) {
        PricingFeeBean pricingFeeBean = pricingFeeServer.selectByPricingNoAndFeeCode(capitalPricingBean.getMatchTargetNo(), feeCode);
        pricingFeeBean.setPricingType(2);
        pricingFeeBean.setPricingNo(capitalPricingBean.getPricingNo());
        pricingFeeBean.setPricingMirrorNo(capitalPricingBean.getCurrentMirrorNo());
        return pricingFeeBean;
    }
}
