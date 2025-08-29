package com.parses.server.impl;

import com.parses.dao.mapper.CapitalTemplateMapper;
import com.parses.dao.mapper.FeeTemplateMapper;
import com.parses.dao.mapper.PricingFeeMapper;
import com.parses.dao.model.CapitalTemplateEntity;
import com.parses.dao.model.FeeTemplateEntity;
import com.parses.dao.model.PricingFeeEntity;
import com.parses.server.PricingFeeServer;
import com.parses.server.bean.ElementDataBean;
import com.parses.server.bean.FormulaParamModel;
import com.parses.server.bean.PricingFeeBean;
import com.parses.server.bean.ProductPricingBean;
import com.parses.server.config.MilliAndFourPercentPreSettleRate;
import com.parses.server.config.PreSettleRateBean;
import com.parses.server.constant.ElementCode;
import com.parses.server.constant.QuickenDayRate;
import com.parses.server.mapping.FeeFormulaParamMapping;
import com.parses.server.mapping.FormulaParamMapping;
import com.parses.server.mapping.PreSettleComplexDayRateMapping;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PricingFeeServerImpl implements PricingFeeServer {

    private static final Logger logger = LoggerFactory.getLogger(PricingFeeServerImpl.class);

    @Autowired
    private MapperFacade mapperFacade;
    @Autowired
    private PricingFeeMapper pricingFeeEntityMapper;
    @Autowired
    private CapitalTemplateMapper capitalTemplateMapper;
    @Autowired
    private FeeTemplateMapper feeTemplateMapper;

    @Override
    public PricingFeeBean selectByPricingNoAndFeeCode(String pricingNo, String feeCode) {
        PricingFeeEntity pricingFeeEntity = pricingFeeEntityMapper.selectByPricingNoAndFeeCode(pricingNo, feeCode);
        return mapperFacade.map(pricingFeeEntity, PricingFeeBean.class);
    }

    @Transactional
    @Override
    public int batchInsertFee(List<PricingFeeBean> list) {
        List<PricingFeeEntity> pricingFeeEntities = mapperFacade.mapAsList(list, PricingFeeEntity.class);
        return pricingFeeEntityMapper.batchInsert(pricingFeeEntities);
    }

    @Override
    public List<PricingFeeBean> addProductFeeInfo(List<ProductPricingBean> productPricingBeans, List<PricingFeeBean> pricingFeeBeans, List<ElementDataBean> capitalElementList) {
        Map<String, ProductPricingBean> pricingBeanMap = productPricingBeans.stream()
                .collect(Collectors.toMap(
                        bean -> bean.getTotalStage() + bean.getRankLevel() + bean.getConsumerLabel(),
                        bean -> bean
                ));
        Map<String, PricingFeeBean> pricingFeeMap = pricingFeeBeans.stream()
                .collect(Collectors.toMap(
                        bean -> bean.getTotalStage() + bean.getRankLevel() + bean.getConsumerLabel(),
                        bean -> bean,
                        (existing, replacement) -> existing
                ));
        if (pricingFeeMap.size() != pricingBeanMap.size()) {
            // 检查每个键是否存在
            pricingFeeMap.keySet().forEach(key -> {
                if (!pricingBeanMap.containsKey(key)) {
                    logger.error("定价数据缺失{}不存在于产品定价中", key);
                    throw new RuntimeException("定价数据缺失" + key + "不存在于产品定价中");
                }
            });
        }
        String capitalCode = ""; // 资方编码
        String graceDays = "";// 宽限期天数
        String milliAndFourPercent = "[]"; // 提前结清千一/百四标识
        for (ElementDataBean elementDataBean : capitalElementList) {
            switch (elementDataBean.getElementCode()) {
                case ElementCode.CAPITAL_CODE:
                    capitalCode = elementDataBean.getElementData();
                    break;
                case ElementCode.CAPITAL_GRACE_DAYS:
                    graceDays = elementDataBean.getElementData();
                    break;
                case ElementCode.CAPITAL_MILLI_AND_FOURPERCENT_PRE_SETTLE_RATE:
                    milliAndFourPercent = elementDataBean.getElementData();
                    break;
            }
        }
        List<CapitalTemplateEntity> capitalTemplates = capitalTemplateMapper.selectByCapitalCode(capitalCode);
        if (CollectionUtils.isEmpty(capitalTemplates)) {
            logger.error("资方编码{}未找到 CapitalTemplate", capitalCode);
            throw new RuntimeException("资方编码" + capitalCode + "未找到 CapitalTemplate");
        }
        MilliAndFourPercentPreSettleRate milliAndFourPercentPreSettleRate = MilliAndFourPercentPreSettleRate.parseConfig(milliAndFourPercent);
        for (CapitalTemplateEntity capitalTemplate : capitalTemplates) {
            List<FeeTemplateEntity> feeTemplateEntities = feeTemplateMapper.selectByTemplateNo(capitalTemplate.getFeeTemplateNo());
            if (CollectionUtils.isEmpty(feeTemplateEntities)) {
                logger.error("模板编号{}未找到 FeeTemplate", capitalTemplate.getFeeTemplateNo());
                throw new RuntimeException("模板编号" + capitalTemplate.getFeeTemplateNo() + "未找到 FeeTemplate");
            }
            for (PricingFeeBean pricingFeeBean : pricingFeeBeans) {
                ProductPricingBean productPricingBean = pricingBeanMap.get(pricingFeeBean.getTotalStage() + pricingFeeBean.getRankLevel() + pricingFeeBean.getConsumerLabel());
                pricingFeeBean.setPricingNo(productPricingBean.getPricingNo());
                pricingFeeBean.setPricingMirrorNo(productPricingBean.getCurrentMirrorNo());
                pricingFeeBean.setPricingType(1);
                pricingFeeBean.setOperaterName("PEC");
                if (capitalTemplate.getMatchTargetRankLevel().contains(pricingFeeBean.getRankLevel())) {
                    List<FormulaParamMapping> feeFormulaParams = Objects.requireNonNull(FeeFormulaParamMapping.getFeeParamsMapping(pricingFeeBean.getFeeCode())).getParams();
                    List<FormulaParamModel> formulaParamList = pricingFeeBean.getFormulaParamList();
                    for (FormulaParamMapping feeFormulaParam : feeFormulaParams) {
                        FormulaParamModel formulaParamModel = new FormulaParamModel();
                        formulaParamModel.setParamCode(feeFormulaParam.getParamCode());
                        switch (feeFormulaParam) {
                            case chargeMode:
                            case includePayDate:
                            case includeGraceDays:
                                formulaParamModel.setValue(feeFormulaParam.getDefaultValue());
                                formulaParamList.add(formulaParamModel);
                                break;
                            case graceDays:
                                formulaParamModel.setValue(graceDays);
                                formulaParamList.add(formulaParamModel);
                                break;
                            case complexMonthRate:
                                List<PreSettleRateBean> milliPreSettleRate = milliAndFourPercentPreSettleRate.getMilliPreSettleRate();
                                List<PreSettleRateBean> fourPercentPreSettleRate = milliAndFourPercentPreSettleRate.getFourPercentPreSettleRate();
                                if (CollectionUtils.isNotEmpty(milliPreSettleRate) || CollectionUtils.isNotEmpty(fourPercentPreSettleRate)) {
                                    FormulaParamModel preSettleComplexDayRate = new FormulaParamModel();
                                    preSettleComplexDayRate.setParamCode(FormulaParamMapping.preSettleComplexDayRate.getParamCode());
                                    preSettleComplexDayRate.setValue(FormulaParamMapping.preSettleComplexDayRate.getDefaultValue());
                                    if (Objects.nonNull(PreSettleComplexDayRateMapping.getRateMapping(capitalCode))) {
                                        preSettleComplexDayRate.setValue(PreSettleComplexDayRateMapping.getRateMapping(capitalCode).getRate());
                                    }
                                    formulaParamList.add(preSettleComplexDayRate);
                                    FormulaParamModel preSettleSwitchRate = new FormulaParamModel();
                                    preSettleSwitchRate.setParamCode(FormulaParamMapping.preSettleSwitchRate.getParamCode());
                                    preSettleSwitchRate.setValue(FormulaParamMapping.preSettleSwitchRate.getDefaultValue());
                                    formulaParamList.add(preSettleSwitchRate);
                                }
                                break;
                            case repaymentWay:
                            case quickenRecalculate:
                                if (capitalTemplate.getIsSupportAccelerate().equals("1")) {
                                    formulaParamModel.setValue(feeFormulaParam.getDefaultValue());
                                    formulaParamList.add(formulaParamModel);
                                }
                                break;
                            case quickenDayRate:
                                if (capitalTemplate.getIsSupportAccelerate().equals("1")) {
                                    formulaParamModel.setValue(QuickenDayRate.getQuickenDayRateMapping(pricingFeeBean.getRankLevel()).getRate());
                                    formulaParamList.add(formulaParamModel);
                                }
                                break;
                            case calculationMode:
                                if (StringUtils.isNotEmpty(capitalTemplate.getRepurchaseCalculationMode())) {
                                    formulaParamModel.setValue(capitalTemplate.getRepurchaseCalculationMode());
                                    formulaParamList.add(formulaParamModel);
                                }
                                break;
                        }
                    }
                    if (StringUtils.isEmpty(pricingFeeBean.getFeeCode())) {
                        pricingFeeBean.setFeeName("");
                        pricingFeeBean.setFormulaType(1);
                        pricingFeeBean.setFormulaNo(capitalTemplate.getRepaymentFormulaNo());
                        pricingFeeBean.setFormulaName(capitalTemplate.getRepaymentFormulaName());
                        continue;
                    }
                    for (FeeTemplateEntity feeTemplateEntity : feeTemplateEntities) {
                        if (feeTemplateEntity.getFeeCode().equals(pricingFeeBean.getFeeCode())) {
                            pricingFeeBean.setFormulaType(2);
                            pricingFeeBean.setFormulaNo(feeTemplateEntity.getFormulaNo());
                            pricingFeeBean.setFormulaName(feeTemplateEntity.getFormulaName());
                            break;
                        }
                    }
                }
            }
        }
        return pricingFeeBeans.stream()
                .sorted(Comparator.comparing(PricingFeeBean::getTotalStage)
                        .thenComparing(PricingFeeBean::getRankLevel)
                        .thenComparing(PricingFeeBean::getConsumerLabel)
                        .thenComparing(PricingFeeBean::getFeeCode))
                .collect(Collectors.toList());
    }
}
