package com.parses.server.mapper;

import com.parses.dao.model.CapitalPricingEntity;
import com.parses.dao.model.ElementDataEntity;
import com.parses.dao.model.PricingFeeEntity;
import com.parses.dao.model.ProductPricingEntity;
import com.parses.server.bean.CapitalPricingBean;
import com.parses.server.bean.ElementDataBean;
import com.parses.server.bean.PricingFeeBean;
import com.parses.server.bean.ProductPricingBean;
import com.parses.server.util.PricingUtils;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Bean拷贝基础工厂，可直接在Service中Autowired MapperFacade使用拷贝功能
 */
@Component
public class MapperFacadeFactory implements FactoryBean<MapperFacade> {
	public MapperFacade getObject() throws Exception {
		DefaultMapperFactory builder = new DefaultMapperFactory.Builder()
				.mapNulls(false).build();

		builder.classMap(ElementDataEntity.class, ElementDataBean.class).byDefault().register();

		builder.classMap(PricingFeeEntity.class, PricingFeeBean.class).customize(new CustomMapper<PricingFeeEntity, PricingFeeBean>() {
			@Override
			public void mapAtoB(PricingFeeEntity entity, PricingFeeBean bean, MappingContext context) {
				bean.setCalcPeriod(1);
				if(!StringUtils.isEmpty(entity.getFormulaParamsJson())) {
					bean.setFormulaParamList(PricingUtils.getFormulaParamList(entity.getFormulaParamsJson()));
				}
			}
			@Override
			public void mapBtoA(PricingFeeBean bean, PricingFeeEntity entity, MappingContext context) {
				entity.setCalcPeriod(1);
				if(bean.getFormulaParamList() != null) {
					entity.setFormulaParamsJson(PricingUtils.toFormulaParamJson(bean.getFormulaParamList()));
				}
			}
		}).byDefault().register();

		builder.classMap(ProductPricingEntity.class, ProductPricingBean.class).customize(new CustomMapper<ProductPricingEntity, ProductPricingBean>() {
			@Override
			public void mapAtoB(ProductPricingEntity entity, ProductPricingBean bean, MappingContext context) {

			}
			@Override
			public void mapBtoA(ProductPricingBean bean, ProductPricingEntity entity, MappingContext context) {
				if(bean.getYearRate() != null) {
					entity.setYearRate(bean.getYearRate().setScale(6, RoundingMode.HALF_UP));
					entity.setMonthRate(bean.getYearRate().divide(new BigDecimal(12),6,BigDecimal.ROUND_HALF_UP));
					entity.setDayRate(bean.getYearRate().divide(new BigDecimal(360),6,BigDecimal.ROUND_HALF_UP));
				}
				if(bean.getAprMonthRate() != null) {
					entity.setAprMonthRate(bean.getAprMonthRate().setScale(6, RoundingMode.HALF_UP));
					entity.setAprYearRate(bean.getAprMonthRate().multiply(new BigDecimal(12)).setScale(6, RoundingMode.HALF_UP));
					entity.setAprDayRate(bean.getAprMonthRate().divide(new BigDecimal(30),6,BigDecimal.ROUND_HALF_UP));
				}
			}
		}).byDefault().register();


		builder.classMap(CapitalPricingEntity.class, CapitalPricingBean.class).customize(new CustomMapper<CapitalPricingEntity, CapitalPricingBean>() {
			@Override
			public void mapAtoB(CapitalPricingEntity entity, CapitalPricingBean bean, MappingContext context) {

			}
			@Override
			public void mapBtoA(CapitalPricingBean bean, CapitalPricingEntity entity, MappingContext context) {
				if(bean.getYearRate() != null) {
					entity.setYearRate(bean.getYearRate().setScale(6, RoundingMode.HALF_UP));
					entity.setMonthRate(bean.getYearRate().divide(new BigDecimal(12),6,BigDecimal.ROUND_HALF_UP));
					entity.setDayRate(bean.getYearRate().divide(new BigDecimal(360),6,BigDecimal.ROUND_HALF_UP));
				}
				entity.setRemainCapitalCode("PT02");
				entity.setRemainCapitalName("国美融通");
				entity.setRemainCapitalMemberNo("f0a9f325cd83428f91de6d54d3fe655a");
			}
		}).byDefault().register();
		return builder.getMapperFacade();
	}




	public Class<?> getObjectType() {
		return MapperFacade.class;
	}

	public boolean isSingleton() {
		return true;
	}
}
