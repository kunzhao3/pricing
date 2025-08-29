package com.parses.server.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.parses.server.bean.FormulaParamModel;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class PricingUtils {

	public static String toFormulaParamJson(List<FormulaParamModel> paramList){
		if(paramList == null){
			return null;
		}
		return JSON.toJSONString(paramList.stream().collect(Collectors.toMap(FormulaParamModel::getParamCode,f -> f,(f1,f2) -> f1)));
	}

	public static List<FormulaParamModel> getFormulaParamList(String paramJson){
		if(StringUtils.isEmpty(paramJson)){
			return null;
		}
		JSONObject jsonObject = JSON.parseObject(paramJson);
		List<FormulaParamModel> list = jsonObject.values().stream().map(json -> ((JSONObject) json).toJavaObject(FormulaParamModel.class))
				.collect(Collectors.toList());
		return list;
	}

}
