package com.parses.server.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.parses.server.bean.FormulaParamModel;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class PricingUtils {

	public static String toFormulaParamJson(List<FormulaParamModel> paramList,boolean isDownParamCode){
		if(paramList == null){
			return null;
		}
		if(isDownParamCode){
			 return  JSON.toJSONString(
					paramList.stream()
							.sorted(Comparator.comparing(FormulaParamModel::getParamCode).reversed())
							.collect(Collectors.toMap(
									FormulaParamModel::getParamCode,
									f -> f,
									(f1, f2) -> f1,
									LinkedHashMap::new
							))
			);
		}
		return JSON.toJSONString(
				paramList.stream()
				.collect(Collectors.toMap(
						FormulaParamModel::getParamCode,
						f -> f,
						(f1,f2) -> f1)));
	}

	public static List<FormulaParamModel> getFormulaParamList(String paramJson){
		if(StringUtils.isEmpty(paramJson)){
			return null;
		}
		JSONObject jsonObject = JSON.parseObject(paramJson);
        return jsonObject.values().stream().map(json -> ((JSONObject) json).toJavaObject(FormulaParamModel.class)).collect(Collectors.toList());
	}
}
