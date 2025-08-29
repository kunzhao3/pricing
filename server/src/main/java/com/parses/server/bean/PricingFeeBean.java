package com.parses.server.bean;

import lombok.Data;

import java.util.List;

@Data
public class PricingFeeBean {
	/**
	 * 总期数 - 必填
	 */
	private Integer totalStage;

	/**
	 * 风险等级 A档 B档 C档 D档  - 必填
	 */
	private String rankLevel;

	/**
	 * 用户标签  - 必填
	 */
	private String consumerLabel;

	/**
	 * 定价类型
	 */
	private Integer pricingType;

	/**
	 * 定价编号
	 */
	private String pricingNo;

	/**
	 * 定价镜像编号
	 */
	private String pricingMirrorNo;

	/**
	 * 费用代码
	 */
	private String feeCode;

	/**
	 * 费用名称
	 */
	private String feeName;

	/**
	 * 费用描述
	 */
	private String feeDescription;

	/**
	 * 计算周期 1-按期计算 2-按日计算
	 */
	private Integer calcPeriod;

	/**
	 * 公式类型 1-本息公式 2-费项公式
	 */
	private Integer formulaType;

	/**
	 * 公式编号
	 */
	private String formulaNo;

	/**
	 * 公式名称
	 */
	private String formulaName;

	/**
	 * 公式参数
	 */
	private List<FormulaParamModel> formulaParamList;

	/**
	 * 操作人
	 */
	private String operaterName;
}
